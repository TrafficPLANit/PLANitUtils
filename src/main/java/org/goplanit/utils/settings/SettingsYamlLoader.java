package org.goplanit.utils.settings;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.exceptions.SettingsProblemsException;
import org.goplanit.utils.exceptions.YamlProblemsException;
import org.goplanit.utils.math.TypeConversionUtil;
import org.goplanit.utils.misc.CollectionUtils;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.misc.StringUtils;
import org.goplanit.utils.reflection.ReflectionUtils;
import org.goplanit.utils.settings.annotations.SettingsFileReference;
import org.goplanit.utils.settings.annotations.SettingsKeyEffect;
import org.goplanit.utils.settings.annotations.SettingsTopLevelValue;
import org.goplanit.utils.settings.annotations.SettingsSection;
import org.goplanit.utils.settings.annotations.SettingsWrite;
import org.goplanit.utils.settings.problem.SettingsProblem;
import org.goplanit.utils.settings.problem.SettingsProblemKind;
import org.goplanit.utils.yaml.SimpleYamlReader;
import org.goplanit.utils.yaml.YamlProblemKind;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Fills settings from YAML settings files and text, by calling the methods of the settings that carry a
 * {@link SettingsWrite} or {@link SettingsSection} annotation
 * <p>
 * For example, settings of a class marked {@code @SettingsHeader(key = "planitExampleSettings", version = 1)} and
 * {@code @SettingsSection(key = "network", ...)}, with the method
 * </p>
 * <pre>
 * &#64;SettingsWrite(key = "trafficSignalKindOverrides", effect = SettingsKeyEffect.PUT_EACH,
 *     level = SettingsKeyLevel.UNCOMMON, description = "Traffic signal kinds by node id")
 * public void overrideTrafficSignalKind(long nodeId, UserOverride&lt;TrafficSignalKind&gt; kind)
 * </pre>
 * <p>
 * made from a file holding
 * </p>
 * <pre>
 * planitExampleSettings: 1
 * network:
 *   trafficSignalKindOverrides:
 *     123: RAMP_METER
 *     456: {value: SIGNAL, reason: "checked on site 2026-10-30"}
 * </pre>
 * <p>
 * by
 * </p>
 * <pre>
 * var settings = new SettingsYamlLoader(new SettingsSchema(ExampleNetworkSettings.class),
 *     List.of(SettingsSource.ofFile(file))).create(ExampleNetworkSettings.class);
 * </pre>
 * <p>
 * get the calls {@code overrideTrafficSignalKind(123, UserOverride.of(RAMP_METER))} and
 * {@code overrideTrafficSignalKind(456, UserOverride.of(SIGNAL, "checked on site 2026-10-30"))}, as if code had made
 * them. Code changing the settings afterwards wins.
 * </p>
 * <p>
 * The loader makes the settings: a value only a constructor can set, such as a country, sits at the top level of the
 * sources, and the constructor taking it ({@link SettingsTopLevelValue}) is called with it, or the constructor taking
 * nothing when no source gives it. The sections are then filled: the settings themselves when their class names a
 * section, otherwise what their getters give, found by the class each gives. Settings made in code are made with
 * their constructors as before.
 * </p>
 * <p>
 * Keys can also sit at the top level of the sources, next to the header, when the settings of several sections share
 * them. They are the keys of the class giving the header, as long as that class fills no section itself. For example,
 * with
 * </p>
 * <pre>
 * &#64;SettingsHeader(key = "planitExampleSettings", version = 1)
 * public abstract class ExampleReaderSettings {
 *   &#64;SettingsWrite(key = "retainTags", level = SettingsKeyLevel.STANDARD, description = "Keep the input tags")
 *   public void setRetainTags(boolean retainTags)
 * }
 *
 * &#64;SettingsSection(key = "network", level = SettingsKeyLevel.STANDARD,
 *     description = "The settings of the network")
 * public class ExampleNetworkSettings extends ExampleReaderSettings
 *
 * &#64;SettingsSection(key = "transport", level = SettingsKeyLevel.STANDARD,
 *     description = "The settings of the transport")
 * public class ExampleTransportSettings extends ExampleReaderSettings
 * </pre>
 * <p>
 * the source
 * </p>
 * <pre>
 * planitExampleSettings: 1
 * retainTags: true
 * network:
 *   maximumSpeed: 80
 * </pre>
 * <p>
 * calls {@code setRetainTags(true)} on the network settings and on the transport settings alike, as both inherit the
 * key, so settings holding both sections get it from one line. Inside {@code network}, {@code retainTags} is no key. A
 * value of a type that is neither a plain value nor a small section, such as an area that comes in several forms, is
 * made by the converter of its type registered on the schema ({@link SettingsValueConverter}).
 * </p>
 * <p>
 * Settings filling no section, made from a schema of their one class, take every key of their class at the top level,
 * whichever class in their class chain declares it, so a file holds the settings of one object. A getter marked with
 * {@link SettingsFileReference} names the file of nested settings, or a list of files: the nested settings the getter
 * gives are filled from each file in the order listed, as stacked sources are, a relative path read against the folder
 * of the file naming it, as they would be filled from those files on their own, and nested settings no file names keep
 * their settings. A named file
 * that cannot be read, has another header, or names, directly or through others, a file being read is a problem of
 * its own.
 * </p>
 * <p>
 * The loader knows no settings class, so any settings can be filled from files by annotating their methods, and they
 * stay ordinary objects, made and changed in code as before. It finds the method of a key through its annotation, and
 * learns what to turn the text into from the types the method declares, which Java keeps in its signature:
 * {@code long} for the node id, and a {@code UserOverride} holding a {@code TrafficSignalKind} for the value (see
 * {@link ReflectionUtils#getTypeArgument(Type, int)}). Each settings class is looked through once, and its keys kept
 * for every later source.
 * </p>
 * <p>
 * Sources apply in order, each calling methods as code would, so a later source wins for a replacing key and adds to an
 * adding key ({@link SettingsKeyEffect}). Within a source the keys apply in the order written, so a later key wins as a
 * later call does in code, with three fixed points: the values at the top level a constructor takes come first; the
 * forms of one list stay together where the first of them is written, the whole list, then its {@code -} form, then its
 * {@code +} form; and sections come after the keys beside them, those at the top level in the order of the schema and
 * nested ones in the order settings are written out in, level first, so the more general settings always apply before
 * the more particular ones below them. Loading is strict: every source is checked, every problem is kept with its
 * source and the path of its key, and all are thrown together once every source is done, so a user fixes every mistake
 * in one go. Reading the YAML is left to {@link SimpleYamlReader}, which gives every value as text and refuses tags, so
 * {@code NO} is never read as false and no Java object is ever made from a file; a source with a YAML problem is not
 * applied, its YAML problems reported with the rest.
 * </p>
 *
 * @author markr
 */
public final class SettingsYamlLoader {

  /** the logger */
  private static final Logger LOGGER = Logger.getLogger(SettingsYamlLoader.class.getCanonicalName());

  /** start of the header key of any format, such as planitOsmConverterSettings */
  private static final String HEADER_KEY_START = "planit";

  /** end of the header key of any format */
  private static final String HEADER_KEY_END = "Settings";

  /** what each kind of value the reader gives is, for problems */
  private static final Map<Class<?>, String> VALUE_DESCRIPTIONS =
      Map.of(String.class, "a single value", List.class, "a list", Map.class, "keys with values");

  /** marks a value that could not be made, its problems recorded already */
  private static final Object FAILED = new Object();

  /**
   * The keys of a section in the order they apply: its settings keys in the order of the source, the forms of one
   * list kept together where the first of them is written, whole list, then minus, then plus; then its nested
   * sections, in the order settings are written out in, level first, whatever the order of the source
   * <p>
   * Applying in the order written lets a later key win, as a later call does in code. Nested sections come last, in
   * the order settings are written out in, so the more general settings of a section always apply before the more
   * particular ones below it: a way type registered for the whole network, for example, before the highway types
   * listed in the highway section
   * </p>
   *
   * @param entries of the section
   * @param index of the settings keys of the settings it fills
   * @return the keys of the entries, in the order they apply
   */
  private static List<String> orderOfApplying(Map<String, Object> entries, SettingsKeyIndex index) {
    var firstPositionOfList = new HashMap<String, Integer>();
    var position = 0;
    for (var key : entries.keySet()) {
      firstPositionOfList.putIfAbsent(SettingsKeyIndex.listKeyOf(key), position++);
    }
    var keys = entries.keySet().stream().filter(key -> index.findSectionGetter(key) == null)
        .sorted(Comparator.comparingInt((String key) -> firstPositionOfList.get(SettingsKeyIndex.listKeyOf(key)))
            .thenComparingInt(SettingsKeyIndex::applyingOrderOfListForm));
    var sections = entries.keySet().stream().filter(key -> index.findSectionGetter(key) != null)
        .sorted(index.inWritingOrder());
    return Stream.concat(keys, sections).collect(Collectors.toList());
  }

  /**
   * Whether a value is an override entry written with its fields, {@code { value: ..., reason: ... }}, the reason
   * being optional
   *
   * @param value to check
   * @return true when it is keys with values, the keys being value, or value and reason
   */
  private static boolean isOverrideWithFields(Object value) {
    if (!(value instanceof Map)) {
      return false;
    }
    var keys = SimpleYamlReader.asMap(value).keySet();
    return keys.contains(UserOverride.VALUE_FIELD) &&
        Set.of(UserOverride.VALUE_FIELD, UserOverride.REASON_FIELD).containsAll(keys);
  }

  /**
   * The kind of settings problem a YAML problem is: a key given twice is one in any text of keys and values; every
   * other YAML problem makes the text unreadable as settings, the YAML problem's detail saying why
   *
   * @param kind of the YAML problem
   * @return the kind of settings problem
   */
  private static SettingsProblemKind settingsKindOf(YamlProblemKind kind) {
    return kind == YamlProblemKind.DUPLICATE_KEY ? SettingsProblemKind.DUPLICATE_KEY :
        SettingsProblemKind.UNREADABLE_TEXT;
  }

  /** the layout of the sources */
  private final SettingsSchema schema;

  /** each source with a right header, with the values at its top level */
  private final List<Pair<SettingsSource, Map<String, Object>>> parsedSources = new ArrayList<>();

  /** problems found so far */
  private final List<SettingsProblem> problems = new ArrayList<>();

  /** warnings found so far */
  private final List<SettingsProblem> warnings = new ArrayList<>();

  /** the files being read, the source applied first, so a file of nested settings naming one of them back is found */
  private final Deque<String> filesBeingRead = new ArrayDeque<>();

  /**
   * Record a problem
   *
   * @param kind of problem
   * @param source it is in
   * @param keyPath of the key it is about, empty for the source as a whole
   * @param detail what is wrong, for people
   */
  private void addProblem(SettingsProblemKind kind, SettingsSource source, String keyPath, String detail) {
    problems.add(new SettingsProblem(kind, source.getDescription(), keyPath, detail));
  }

  /**
   * The text of a key at the top level of the sources
   *
   * @param key at the top level
   * @return the text the last source giving the key as a single value gives, null when none does
   */
  private String findTopLevelText(String key) {
    String found = null;
    for (var parsed : parsedSources) {
      var text = SimpleYamlReader.asText(parsed.second().get(key));
      if (text != null) {
        found = text;
      }
    }
    return found;
  }

  /**
   * Read a source, recording any problem
   *
   * @param source to read
   * @return the values at its top level when its YAML can be read and its header is right, otherwise null
   */
  private Map<String, Object> readTopLevel(SettingsSource source) {
    Object topLevel;
    try {
      topLevel = SimpleYamlReader.read(source.readText());
    } catch (IOException e) {
      addProblem(SettingsProblemKind.FILE_NOT_READ, source, "", "Cannot be read: " + e);
      return null;
    } catch (YamlProblemsException e) {
      e.getProblems().forEach(problem ->
          addProblem(settingsKindOf(problem.getKind()), source, problem.getKeyPath(), problem.getDetail()));
      return null;
    }
    if (!(topLevel instanceof Map)) {
      addProblem(SettingsProblemKind.UNREADABLE_TEXT, source, "", "Holds no keys at its top level");
      return null;
    }
    return isHeaderRight(SimpleYamlReader.asMap(topLevel), source) ? SimpleYamlReader.asMap(topLevel) : null;
  }

  /**
   * Check the first key of a source is the header key, with the version
   *
   * @param topLevel values of the source
   * @param source it is
   * @return true when right, otherwise false with the problem recorded
   */
  private boolean isHeaderRight(Map<String, Object> topLevel, SettingsSource source) {
    var header = schema.getHeader();
    var firstKey = topLevel.isEmpty() ? null : topLevel.keySet().iterator().next();
    if (header.key().equals(firstKey)) {
      var version = SimpleYamlReader.asText(topLevel.get(firstKey));
      if (String.valueOf(header.version()).equals(version)) {
        return true;
      }
      addProblem(SettingsProblemKind.UNKNOWN_VERSION, source, firstKey,
          String.format("Version %s is not known, expected %d", version, header.version()));
    } else if (firstKey != null && firstKey.startsWith(HEADER_KEY_START) && firstKey.endsWith(HEADER_KEY_END)) {
      addProblem(SettingsProblemKind.WRONG_HEADER, source, firstKey,
          String.format("Is for other settings, expected %s", header.key()));
    } else {
      addProblem(SettingsProblemKind.MISSING_HEADER, source, "",
          String.format("The first line is not %s: %d", header.key(), header.version()));
    }
    return false;
  }

  /**
   * Check a value is of the kind expected: a single value, a list, or keys with values
   *
   * @param value as the reader gives it
   * @param kind expected: String, List or Map
   * @param keyPath of the key it is the value of
   * @param source it is in
   * @return true when it is, otherwise false with the problem recorded
   */
  private boolean isKindOf(Object value, Class<?> kind, String keyPath, SettingsSource source) {
    if (kind.isInstance(value)) {
      return true;
    }
    addProblem(SettingsProblemKind.WRONG_TYPE, source, keyPath, "Expected " + VALUE_DESCRIPTIONS.get(kind));
    return false;
  }

  /**
   * Apply the top level of a source, with the source among the files being read while it applies: each key at the top
   * level, in the order of the source, to the settings themselves when they fill no section, otherwise to the settings
   * of every section, a key naming the file of nested settings filling those nested settings; then each section to its
   * settings, in the order of the schema, recording keys outside the schema and sections the settings do not take
   * <p>
   * One method serves the sources given and the files of nested settings alike, so the file of nested settings is read
   * exactly as it would be were the nested settings made on their own. Each source stays among the files being read
   * while it applies, which is how a file of nested settings naming a file further up is found: {@code a.yaml} naming
   * {@code b.yaml}, which names
   * {@code a.yaml} again, for example.
   * </p>
   *
   * @param source the top level is of
   * @param topLevel values of the source
   * @param settings to apply it to
   * @param settingsBySectionKey the settings each top level section fills, empty when the settings fill no section
   */
  private void applyTopLevel(SettingsSource source, Map<String, Object> topLevel, Object settings,
      Map<String, Object> settingsBySectionKey) {
    var index = SettingsKeyIndex.of(settings.getClass());
    Collection<Object> topLevelKeySettings =
        settingsBySectionKey.isEmpty() ? List.of(settings) : settingsBySectionKey.values();
    filesBeingRead.push(source.getDescription());
    for (var entry : topLevel.entrySet()) {
      var key = entry.getKey();
      if (key.equals(schema.getHeader().key()) || schema.isSectionKey(key)) {
        continue;
      }
      var fileReferenceGetter = index.findFileReferenceGetter(key);
      if (index.getTopLevelValueKeys().contains(key)) {
        isKindOf(entry.getValue(), String.class, key, source);
      } else if (fileReferenceGetter != null) {
        applyFileReference(fileReferenceGetter, entry.getValue(), settings, key, source);
      } else {
        applyTopLevelKey(key, entry.getValue(), topLevelKeySettings, source);
      }
    }
    for (var key : schema.getSectionKeys()) {
      if (!topLevel.containsKey(key)) {
        continue;
      }
      if (settingsBySectionKey.containsKey(key)) {
        applySection(topLevel.get(key), settingsBySectionKey.get(key), key, source);
      } else {
        addProblem(SettingsProblemKind.UNKNOWN_KEY, source, key, "A section these settings do not take");
      }
    }
    filesBeingRead.pop();
  }

  /**
   * Fill nested settings from the settings file a key names, or from each file of a list in the order listed, as
   * stacked sources are, a relative path read against the folder of the source naming it. A list is read as entries, so
   * a problem names the entry, and no file is read when an entry is refused
   *
   * @param getter of the nested settings
   * @param value of the key, the path of a file or a list of them
   * @param settings holding the nested settings
   * @param keyPath of the key
   * @param source naming the files
   */
  private void applyFileReference(Method getter, Object value, Object settings, String keyPath, SettingsSource source) {
    Object paths;
    if (value instanceof List) {
      paths = convertEntries(value, Path.class, keyPath, source);
    } else {
      var path = isKindOf(value, String.class, keyPath, source) ?
          convertText((String) value, Path.class, keyPath, source) : FAILED;
      paths = path == FAILED ? FAILED : List.of(path);
    }
    if (paths == FAILED) {
      return;
    }
    for (var path : (List<?>) paths) {
      fillNestedSettingsFromFile(getter, (Path) path, settings, keyPath, source);
    }
  }

  /**
   * Fill nested settings from one settings file, as they would be filled from that file on their own. A file being
   * read already is a loop, recorded and not read again, so files naming each other do not go on without end
   *
   * @param getter of the nested settings
   * @param path of the file
   * @param settings holding the nested settings
   * @param keyPath of the key naming the file
   * @param source naming the file
   */
  private void fillNestedSettingsFromFile(Method getter, Path path, Object settings, String keyPath,
      SettingsSource source) {
    var nestedSettingsSource = SettingsSource.ofFile(path);
    if (filesBeingRead.contains(nestedSettingsSource.getDescription())) {
      addProblem(SettingsProblemKind.FILE_REFERENCE_LOOP, source, keyPath,
          String.format("Names %s, which itself names this file, directly or through other files",
              nestedSettingsSource.getDescription()));
      return;
    }
    var nestedSettingsTopLevel = readTopLevel(nestedSettingsSource);
    var nestedSettings =
        nestedSettingsTopLevel == null ? FAILED : call(getter, settings, new Object[0], keyPath, source);
    if (nestedSettings != FAILED) {
      PlanItRunTimeException.throwIfNull(nestedSettings, "Nested settings %s of %s are null", keyPath,
          settings.getClass().getName());
      applyTopLevel(nestedSettingsSource, nestedSettingsTopLevel, nestedSettings,
          schema.getSectionSettingsOf(nestedSettings));
    }
  }

  /**
   * Apply a key at the top level of a source to the settings it applies to, the value converted for each so no two
   * share it, and a value refused recorded once rather than once for each of them
   *
   * @param key at the top level
   * @param value of the key
   * @param topLevelKeySettings the settings the key applies to: those of every section, or the settings themselves when
   *     they fill no section
   * @param source it is in
   */
  private void applyTopLevelKey(String key, Object value, Collection<Object> topLevelKeySettings,
      SettingsSource source) {
    var anySettings = topLevelKeySettings.iterator().next();
    var write = findWrite(SettingsKeyIndex.of(anySettings.getClass()), key, true, key, source);
    if (write == null) {
      addProblem(SettingsProblemKind.UNKNOWN_KEY, source, key, "No such key");
      return;
    }
    var problemCount = problems.size();
    for (var settings : topLevelKeySettings) {
      applyWrite(write, value, settings, key, source);
      if (problems.size() > problemCount) {
        return;
      }
    }
  }

  /**
   * The method writing a key at the top level or in a section, found by its name or by a former name, which is warned
   * about
   *
   * @param index of the settings keys of the settings
   * @param key as given in the source
   * @param atTopLevel true for a key at the top level of the source, false for one in a section
   * @param keyPath of the key
   * @param source it is in
   * @return the method, null when the settings have no such key there
   */
  private Method findWrite(SettingsKeyIndex index, String key, boolean atTopLevel, String keyPath,
      SettingsSource source) {
    var write = index.findWrite(key);
    var former = write == null ? index.findFormerWrite(key) : null;
    var found = write != null ? write : former;
    if (found == null || index.isTopLevelWrite(found) != atTopLevel) {
      return null;
    }
    if (former != null) {
      var warning = new SettingsProblem(SettingsProblemKind.FORMER_KEY_USED, source.getDescription(), keyPath,
          "Applied under its current name " + former.getAnnotation(SettingsWrite.class).key());
      warnings.add(warning);
      LOGGER.warning(warning.toString());
    }
    return found;
  }

  /**
   * Apply a section to its settings, its keys in the order of {@link #orderOfApplying(Map, SettingsKeyIndex)}
   *
   * @param value of the section
   * @param settings it fills
   * @param path of the section
   * @param source it is in
   */
  private void applySection(Object value, Object settings, String path, SettingsSource source) {
    if (!isKindOf(value, Map.class, path, source)) {
      return;
    }
    var index = SettingsKeyIndex.of(settings.getClass());
    var entries = SimpleYamlReader.asMap(value);
    checkAddedAndRemoved(entries, path, source);
    orderOfApplying(entries, index).forEach(key -> applyEntry(key, entries.get(key), settings, index, path, source));
  }

  /**
   * Record values a section both adds, under a key ending in {@code +}, and removes, under the same key ending in
   * {@code -}
   *
   * @param entries of the section
   * @param path of the section
   * @param source it is in
   */
  private void checkAddedAndRemoved(Map<String, Object> entries, String path, SettingsSource source) {
    for (var entry : entries.entrySet()) {
      var key = entry.getKey();
      var removed = key.endsWith(SettingsKeyIndex.ADD_SUFFIX) ?
          entries.get(SettingsKeyIndex.listKeyOf(key) + SettingsKeyIndex.REMOVE_SUFFIX) : null;
      if (!(removed instanceof List) || !(entry.getValue() instanceof List)) {
        continue;
      }
      var both = ((List<?>) entry.getValue()).stream().map(SimpleYamlReader::asText)
          .collect(Collectors.toCollection(LinkedHashSet::new));
      both.retainAll(((List<?>) removed).stream().map(SimpleYamlReader::asText).collect(Collectors.toSet()));
      both.remove(null);
      if (!both.isEmpty()) {
        addProblem(SettingsProblemKind.ADDED_AND_REMOVED, source, SimpleYamlReader.keyPathOf(path, key),
            String.format("%s both added and removed", both));
      }
    }
  }

  /**
   * Apply one key of a section: a section of its own, or a key written through its method
   *
   * @param key as given in the source
   * @param value of the key
   * @param settings the section fills
   * @param index of the settings keys of the settings
   * @param path of the section
   * @param source it is in
   */
  private void applyEntry(String key, Object value, Object settings, SettingsKeyIndex index, String path,
      SettingsSource source) {
    var keyPath = SimpleYamlReader.keyPathOf(path, key);
    var sectionGetter = index.findSectionGetter(key);
    if (sectionGetter != null) {
      var section = call(sectionGetter, settings, new Object[0], keyPath, source);
      PlanItRunTimeException.throwIfNull(section, "Section %s of %s is null", key, settings.getClass().getName());
      if (section != FAILED) {
        applySection(value, section, keyPath, source);
      }
      return;
    }
    var write = findWrite(index, key, false, keyPath, source);
    if (write == null) {
      addProblem(SettingsProblemKind.UNKNOWN_KEY, source, keyPath,
          index.findWrite(key) != null ? "A key of the top level, not of this section" : "No such key");
      return;
    }
    applyWrite(write, value, settings, keyPath, source);
  }

  /**
   * Apply a key through its method, by the effect its annotation gives
   *
   * @param method writing the key
   * @param value of the key
   * @param settings to call the method on
   * @param keyPath of the key
   * @param source it is in
   */
  private void applyWrite(Method method, Object value, Object settings, String keyPath, SettingsSource source) {
    var types = method.getGenericParameterTypes();
    var effect = method.getAnnotation(SettingsWrite.class).effect();
    switch (effect) {
    case REPLACE:
      call(method, settings, new Object[] {convert(value, types[0], keyPath, source)}, keyPath, source);
      break;
    case ADD_EACH:
    case REMOVE_EACH:
      var entries = convertEntries(value, types[0], keyPath, source);
      if (entries != FAILED) {
        var list = (List<?>) entries;
        for (int entryIndex = 0; entryIndex < list.size(); entryIndex++) {
          call(method, settings, new Object[] {list.get(entryIndex)}, SimpleYamlReader.entryPathOf(keyPath, entryIndex),
              source);
        }
      }
      break;
    case PUT_EACH:
      var map = convertMap(value, types[0], types[1], keyPath, source);
      if (map != FAILED) {
        ((Map<?, ?>) map).forEach((key, entryValue) -> call(method, settings, new Object[] {key, entryValue},
            SimpleYamlReader.keyPathOf(keyPath, String.valueOf(key)), source));
      }
      break;
    default:
      throw new PlanItRunTimeException("Settings key effect %s is not known", effect);
    }
  }

  /**
   * A value as a type a method declares, made by the converter of the type when the schema has one
   *
   * @param value as the reader gives it
   * @param type declared, which can have type arguments
   * @param keyPath of the key
   * @param source it is in
   * @return the value, or FAILED with its problems recorded
   */
  private Object convert(Object value, Type type, String keyPath, SettingsSource source) {
    var rawClass = ReflectionUtils.getRawClass(type);
    var converter = schema.findConverter(rawClass);
    if (converter != null) {
      try {
        return converter.fromPlainValue(value);
      } catch (IllegalArgumentException e) {
        addProblem(SettingsProblemKind.VALUE_REFUSED, source, keyPath, e.getMessage());
        return FAILED;
      }
    }
    if (rawClass == UserOverride.class) {
      return convertOverride(value, ReflectionUtils.getTypeArgument(type, 0), keyPath, source);
    }
    if (rawClass.isArray()) {
      var entries = convertEntries(value, rawClass.getComponentType(), keyPath, source);
      return entries == FAILED ? FAILED : CollectionUtils.toArray((List<?>) entries, rawClass.getComponentType());
    }
    if (Collection.class.isAssignableFrom(rawClass)) {
      var entries = convertEntries(value, ReflectionUtils.getTypeArgument(type, 0), keyPath, source);
      return entries == FAILED ? FAILED : CollectionUtils.toCollectionAssignableTo((List<?>) entries, rawClass);
    }
    if (Map.class.isAssignableFrom(rawClass)) {
      return convertMap(value, ReflectionUtils.getTypeArgument(type, 0), ReflectionUtils.getTypeArgument(type, 1),
          keyPath, source);
    }
    if (value instanceof Map) {
      return convertSection(value, rawClass, keyPath, source);
    }
    if (!isKindOf(value, String.class, keyPath, source)) {
      return FAILED;
    }
    return convertText((String) value, rawClass, keyPath, source);
  }

  /**
   * The value of an override entry, written plainly or as {@code { value: ..., reason: ... }}
   *
   * @param value of the entry
   * @param valueType of the value the override holds
   * @param keyPath of the key
   * @param source it is in
   * @return the override, or FAILED with its problems recorded
   */
  private Object convertOverride(Object value, Type valueType, String keyPath, SettingsSource source) {
    if (!isOverrideWithFields(value)) {
      var overrideValue = convert(value, valueType, keyPath, source);
      return overrideValue == FAILED ? FAILED : UserOverride.of(overrideValue);
    }
    var fields = SimpleYamlReader.asMap(value);
    var reason = fields.get(UserOverride.REASON_FIELD);
    if (reason != null &&
        !isKindOf(reason, String.class, SimpleYamlReader.keyPathOf(keyPath, UserOverride.REASON_FIELD), source)) {
      return FAILED;
    }
    var overrideValue = convert(fields.get(UserOverride.VALUE_FIELD), valueType,
        SimpleYamlReader.keyPathOf(keyPath, UserOverride.VALUE_FIELD), source);
    return overrideValue == FAILED ? FAILED : UserOverride.of(overrideValue, SimpleYamlReader.asText(reason));
  }

  /**
   * The entries of a list, each as a type
   *
   * @param value of the list
   * @param entryType of each entry
   * @param keyPath of the key
   * @param source it is in
   * @return the entries in order, as a List, or FAILED with the problems recorded
   */
  private Object convertEntries(Object value, Type entryType, String keyPath, SettingsSource source) {
    if (!isKindOf(value, List.class, keyPath, source)) {
      return FAILED;
    }
    var list = (List<?>) value;
    var entries = new ArrayList<Object>(list.size());
    for (int entryIndex = 0; entryIndex < list.size(); entryIndex++) {
      entries.add(convert(list.get(entryIndex), entryType, SimpleYamlReader.entryPathOf(keyPath, entryIndex), source));
    }
    return entries.stream().anyMatch(entry -> entry == FAILED) ? FAILED : entries;
  }

  /**
   * The entries of a map, each key and value as a type
   *
   * @param value of the map
   * @param keyType of each key
   * @param valueType of each value
   * @param keyPath of the key the map is the value of
   * @param source it is in
   * @return the map in the order of the source, or FAILED with the problems recorded
   */
  private Object convertMap(Object value, Type keyType, Type valueType, String keyPath, SettingsSource source) {
    if (!isKindOf(value, Map.class, keyPath, source)) {
      return FAILED;
    }
    var map = new LinkedHashMap<Object, Object>();
    var failed = false;
    for (var entry : SimpleYamlReader.asMap(value).entrySet()) {
      var entryPath = SimpleYamlReader.keyPathOf(keyPath, entry.getKey());
      var key = convertText(entry.getKey(), ReflectionUtils.getRawClass(keyType), entryPath, source);
      var entryValue = convert(entry.getValue(), valueType, entryPath, source);
      failed |= key == FAILED || entryValue == FAILED;
      map.put(key, entryValue);
    }
    return failed ? FAILED : map;
  }

  /**
   * A small section given as a value: settings of a class with keys of its own, made with its constructor without
   * arguments, so keys left out keep its defaults
   *
   * @param value of the section
   * @param sectionClass the value is of
   * @param keyPath of the key
   * @param source it is in
   * @return the filled section, or FAILED with its problems recorded
   */
  private Object convertSection(Object value, Class<?> sectionClass, String keyPath, SettingsSource source) {
    if (!SettingsKeyIndex.of(sectionClass).hasKeys()) {
      addProblem(SettingsProblemKind.WRONG_TYPE, source, keyPath, "Expected a single value");
      return FAILED;
    }
    var section = ReflectionUtils.createInstance(sectionClass.getName());
    var problemCount = problems.size();
    applySection(value, section, keyPath, source);
    return problems.size() > problemCount ? FAILED : section;
  }

  /**
   * A text as a type, a name that is no constant of an enum type being an unknown enum value and any other text that
   * does not fit a value of the wrong type
   *
   * @param text to convert
   * @param type to convert to
   * @param keyPath of the key
   * @param source it is in, whose folder relative paths are read against
   * @return the value, or FAILED with the problem recorded
   */
  private Object convertText(String text, Class<?> type, String keyPath, SettingsSource source) {
    try {
      return TypeConversionUtil.fromString(text, type, source.getBaseFolder());
    } catch (IllegalArgumentException e) {
      if (type.isEnum()) {
        addProblem(SettingsProblemKind.UNKNOWN_ENUM_VALUE, source, keyPath,
            String.format("'%s' is not one of %s", text, StringUtils.toStrings(type.getEnumConstants())));
      } else {
        addProblem(SettingsProblemKind.WRONG_TYPE, source, keyPath,
            String.format("'%s' is not a valid %s", text, type.getSimpleName()));
      }
      return FAILED;
    }
  }

  /**
   * Call a method of the settings, recording a value it refuses with an {@code IllegalArgumentException}
   *
   * @param method to call
   * @param settings to call it on
   * @param arguments to call it with; when any is FAILED the method is not called
   * @param keyPath of the key
   * @param source it is in
   * @return what the method returns, or FAILED when not called or refused
   * @throws PlanItRunTimeException when the method cannot be called or fails in any other way
   */
  private Object call(Method method, Object settings, Object[] arguments, String keyPath, SettingsSource source) {
    if (Arrays.stream(arguments).anyMatch(argument -> argument == FAILED)) {
      return FAILED;
    }
    try {
      return method.invoke(settings, arguments);
    } catch (InvocationTargetException e) {
      if (e.getCause() instanceof IllegalArgumentException) {
        addProblem(SettingsProblemKind.VALUE_REFUSED, source, keyPath, e.getCause().getMessage());
        return FAILED;
      }
      throw new PlanItRunTimeException("Settings key %s failed in %s", e.getCause(), keyPath, method);
    } catch (IllegalAccessException e) {
      throw new PlanItRunTimeException("Settings key %s cannot call %s", e, keyPath, method);
    }
  }

  /**
   * Constructor, reading every source, so a problem with reading one is reported when the sources are applied
   *
   * @param schema the layout of the sources
   * @param sources in the order they apply, a later one winning for a replacing key
   */
  public SettingsYamlLoader(SettingsSchema schema, List<SettingsSource> sources) {
    this.schema = schema;
    for (var source : sources) {
      var topLevel = readTopLevel(source);
      if (topLevel != null) {
        parsedSources.add(Pair.of(source, topLevel));
      }
    }
  }

  /**
   * Make settings of a class from the sources: made with the values at the top level its constructor takes, then filled
   * by every source in order. Call it once
   *
   * @param <T> type of the settings
   * @param settingsClass to make: the class of a section of the schema, or one giving such settings through getters
   * @return the settings
   * @throws SettingsProblemsException holding every problem found, when there is any
   */
  public <T> T create(Class<T> settingsClass) {
    var index = SettingsKeyIndex.of(settingsClass);
    var settings = settingsClass.cast(index.createSettings(this::findTopLevelText));
    var settingsBySectionKey = schema.getSectionSettingsOf(settings);
    parsedSources.forEach(parsed -> applyTopLevel(parsed.first(), parsed.second(), settings, settingsBySectionKey));
    if (!problems.isEmpty()) {
      throw new SettingsProblemsException(problems);
    }
    return settings;
  }

  /**
   * Warnings found while applying, such as a key given by a former name, which do not stop the sources from applying
   *
   * @return the warnings, in the order found
   */
  public List<SettingsProblem> getWarnings() {
    return Collections.unmodifiableList(warnings);
  }
}
