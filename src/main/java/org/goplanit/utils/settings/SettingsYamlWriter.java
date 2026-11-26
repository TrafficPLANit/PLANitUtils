package org.goplanit.utils.settings;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.math.TypeConversionUtil;
import org.goplanit.utils.misc.CollectionUtils;
import org.goplanit.utils.misc.FileUtils;
import org.goplanit.utils.misc.StringUtils;
import org.goplanit.utils.reflection.ReflectionUtils;
import org.goplanit.utils.settings.annotations.SettingsFileReference;
import org.goplanit.utils.settings.annotations.SettingsKeyEffect;
import org.goplanit.utils.settings.annotations.SettingsKeyLevel;
import org.goplanit.utils.settings.annotations.SettingsRead;
import org.goplanit.utils.settings.annotations.SettingsSection;
import org.goplanit.utils.settings.annotations.SettingsWrite;
import org.goplanit.utils.yaml.SimpleYamlWriter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Writes settings out as YAML text in the format {@link SettingsYamlLoader} reads, through the getters of the settings
 * marked with {@link SettingsRead}, the reverse of loading
 * <p>
 * For example, settings of a class marked {@code @SettingsHeader(key = "planitExampleSettings", version = 1)} and
 * {@code @SettingsSection(key = "network", ...)}, whose key {@code maximumSpeed} was changed to 80 in code, written
 * with
 * </p>
 * <pre>
 * new SettingsYamlWriter(new SettingsSchema(ExampleNetworkSettings.class), settings,
 *     SettingsFileContent.KEYS_DIFFERING_FROM_DEFAULTS, true, Path.of("network.yaml")).write();
 * </pre>
 * <p>
 * give the file
 * </p>
 * <pre>
 * # The kind of these settings and the version of their keys
 * planitExampleSettings: 1
 * network:
 *   # The settings of the network
 *   # --- standard ---
 *   # Highest speed on any road, in km/h
 *   maximumSpeed: 80
 * </pre>
 * <p>
 * It exists so that anyone can see every key of some settings with its value and description, start a file from them,
 * or keep the choices made in a run, and read them back into equal settings. A writer holds the text of the one file
 * it is made for, made when it is constructed. A file holds the header, then the values at the top level the
 * constructor of the settings took and the keys at the top level, both read from the settings themselves when they fill
 * no section, otherwise from the settings of the first section, which settings holding the settings of the sections
 * pass their values at the top level on to, then the sections in the order of the schema.
 * The keys at the top level, and the keys and nested sections inside a section, go by level, then by the class first
 * annotating their method, the keys of a class before those of the classes extending it, then by name: the order nested
 * sections apply in when read.
 * With descriptions, each level has a heading, each section starts with its description, and each key has its
 * description above it, with what it does when it is not a plain replace and the values of an enum it takes; without,
 * the file holds no comment at all. A key is written through its getter, whole: a list in place of its {@code +} and
 * {@code -} forms, the entries of an adding key, and the map of a putting key. Values are written as
 * {@link TypeConversionUtil#toString(Object)} gives them, an override with a reason as
 * {@code {value: ..., reason: ...}}, a small section as its keys that have a value, and a type with a converter as the
 * converter gives it. Of every key, one with no value is commented out when descriptions are written, so taking the
 * {@code #} away puts it where it belongs, and left out otherwise. Of the keys differing from defaults, each key is
 * compared with that of fresh settings made with the same values at the top level. A section with no key to write is
 * left out. A key without a getter is a mistake in the code, so it throws.
 * </p>
 * <p>
 * With descriptions, the header line has a comment above it saying it gives the kind of the settings and the version of
 * their keys. Settings filling no section have every key of their class at the top level. A key naming the settings
 * file of nested settings ({@link SettingsFileReference}) is always written, as the name of a file of their own: the
 * name of the file written without its extension, then {@code _}, the key and {@code .yaml}, such as
 * {@code zoning: intermodal_zoning.yaml} in {@code intermodal.yaml}. {@link #write()} writes that file next to the file
 * naming it, so the files read back give the same settings, and settings with nested settings written into one folder
 * keep their own files, while {@link #toString()} gives the text of the naming file only.
 * </p>
 *
 * @author markr
 */
public final class SettingsYamlWriter {

  /** what the comment above a key adds for an effect other than a plain replace; the {@code -} forms are not written */
  private static final Map<SettingsKeyEffect, String> EFFECT_NOTES = Map.of(
      SettingsKeyEffect.ADD_EACH, " (adds to the list)",
      SettingsKeyEffect.PUT_EACH, " (adds or replaces by key)");

  /** what the comment above the header line says, written with the descriptions */
  private static final String HEADER_DESCRIPTION = "The kind of these settings and the version of their keys";

  /** what joins the name of a file and the key naming the file of its nested settings, in the name of that file */
  private static final String NESTED_SETTINGS_FILE_NAME_SEPARATOR = "_";

  /** the extension of the file nested settings are written to, after the key naming it */
  private static final String NESTED_SETTINGS_FILE_EXTENSION = ".yaml";

  /**
   * The heading of a level, such as "--- standard ---"
   *
   * @param level to head
   * @return the heading
   */
  private static String headingOf(SettingsKeyLevel level) {
    return "--- " + level.name().toLowerCase() + " ---";
  }

  /**
   * The enum a value of a type is or holds, through overrides, collections, arrays and the values of maps; the keys of
   * maps are left out, as a file need not list what an id can be
   *
   * @param type declared, which can have type arguments
   * @return the enum, null when there is none
   */
  private static Class<?> findEnumOf(Type type) {
    var rawClass = ReflectionUtils.getRawClass(type);
    if (rawClass.isEnum()) {
      return rawClass;
    }
    if (rawClass.isArray()) {
      return findEnumOf(rawClass.getComponentType());
    }
    if (rawClass == UserOverride.class || Collection.class.isAssignableFrom(rawClass)) {
      return findEnumOf(ReflectionUtils.getTypeArgument(type, 0));
    }
    if (Map.class.isAssignableFrom(rawClass)) {
      return findEnumOf(ReflectionUtils.getTypeArgument(type, 1));
    }
    return null;
  }

  /**
   * The lines describing a key: its description, with what it does when it is not a plain replace, and the values of
   * an enum it takes
   *
   * @param write the method writing the key
   * @return the lines
   */
  private static List<String> describeYamlKey(Method write) {
    var annotation = write.getAnnotation(SettingsWrite.class);
    var lines = new ArrayList<String>();
    lines.add(annotation.description() + EFFECT_NOTES.getOrDefault(annotation.effect(), ""));
    /* the value is the last parameter: the only one, or the value of a putting key's entry, after its key */
    var types = write.getGenericParameterTypes();
    var enumClass = findEnumOf(types[types.length - 1]);
    if (enumClass != null) {
      lines.add("Values: " + String.join(", ", StringUtils.toStrings(enumClass.getEnumConstants())));
    }
    return lines;
  }

  /**
   * The text of a value at the top level that the constructor of the settings took
   *
   * @param settings of the first section, holding the value, as the settings made with it pass it on to them
   * @param index of the keys of the settings of the first section
   * @param key of the value
   * @return the text, null when there is no value
   * @throws PlanItRunTimeException when the value has no getter
   */
  private static String readTopLevelValueText(Object settings, SettingsKeyIndex index, String key) {
    var value = index.readValue(settings, key);
    return value == null ? null : TypeConversionUtil.toString(value);
  }

  /**
   * The settings of a nested section, through its getter
   *
   * @param sectionGetter of the nested section
   * @param settings holding the nested section, null for none, as when there are no defaults to compare with
   * @return the settings of the nested section, null when there are no settings to read
   */
  private static Object readNestedSectionOf(Method sectionGetter, Object settings) {
    return settings == null ? null : ReflectionUtils.invoke(sectionGetter, settings);
  }

  /**
   * The plain value of a value of a converter's type
   *
   * @param <T> type converted
   * @param converter of the type
   * @param value of the type
   * @return the plain value the converter gives
   */
  private static <T> Object toPlainValueOf(SettingsValueConverter<T> converter, Object value) {
    return converter.toPlainValue(converter.getValueType().cast(value));
  }

  /** the layout of the text */
  private final SettingsSchema schema;

  /** which keys to write */
  private final SettingsFileContentType content;

  /** true to write the descriptions and level headings as comments, and keys with no value commented out */
  private final boolean withDescriptions;

  /** the file the text is written to, which the files of the nested settings are named after */
  private final Path file;

  /** the YAML written so far */
  private final SimpleYamlWriter yaml = new SimpleYamlWriter();

  /** the writer of the file of each of the nested settings a key names, in the order of the keys */
  private final List<SettingsYamlWriter> nestedSettingsWriters = new ArrayList<>();

  /**
   * The plain value of a value of a type the method of a key takes, from which the loader makes an equal value
   *
   * @param value to write, not null
   * @param type declared by the method, which can have type arguments
   * @return text, a list, or a map
   */
  private Object toPlainValue(Object value, Type type) {
    var rawClass = ReflectionUtils.getRawClass(type);
    var converter = schema.findConverter(rawClass);
    if (converter != null) {
      return toPlainValueOf(converter, value);
    }
    if (value instanceof UserOverride) {
      return toPlainOverride((UserOverride<?>) value, ReflectionUtils.getTypeArgument(type, 0));
    }
    if (rawClass.isArray()) {
      return toPlainValues(CollectionUtils.toList(value), rawClass.getComponentType());
    }
    if (value instanceof Collection) {
      return toPlainValues((Collection<?>) value, ReflectionUtils.getTypeArgument(type, 0));
    }
    if (value instanceof Map) {
      return toPlainMap((Map<?, ?>) value, ReflectionUtils.getTypeArgument(type, 1));
    }
    if (SettingsKeyIndex.of(value.getClass()).hasKeys()) {
      return toPlainSection(value);
    }
    return TypeConversionUtil.toString(value);
  }

  /**
   * The plain value of an override: that of its value, or {@code {value: ..., reason: ...}} when it has a reason
   *
   * @param override to write
   * @param valueType declared for its value
   * @return the plain value
   */
  private Object toPlainOverride(UserOverride<?> override, Type valueType) {
    var plainValue = toPlainValue(override.getValue(), valueType);
    if (!override.hasReason()) {
      return plainValue;
    }
    var fields = new LinkedHashMap<String, Object>();
    fields.put(UserOverride.VALUE_FIELD, plainValue);
    fields.put(UserOverride.REASON_FIELD, override.getReason());
    return fields;
  }

  /**
   * The plain values of values of one type, in order
   *
   * @param values to write
   * @param entryType declared for each value
   * @return the plain values
   */
  private List<Object> toPlainValues(Collection<?> values, Type entryType) {
    return values.stream().map(value -> toPlainValue(value, entryType)).collect(Collectors.toList());
  }

  /**
   * The plain map of a map, each key as text
   *
   * @param map to write
   * @param valueType declared for each value
   * @return the plain map, in the order of the map
   */
  private Map<String, Object> toPlainMap(Map<?, ?> map, Type valueType) {
    var plainMap = new LinkedHashMap<String, Object>();
    map.forEach((key, value) -> plainMap.put(TypeConversionUtil.toString(key), toPlainValue(value, valueType)));
    return plainMap;
  }

  /**
   * The plain value of a small section, the value of a key: its keys that have a value, in writing order
   *
   * @param section to write
   * @return the keys with their plain values
   */
  private Map<String, Object> toPlainSection(Object section) {
    var index = SettingsKeyIndex.of(section.getClass());
    var fields = new LinkedHashMap<String, Object>();
    for (var key : index.getSectionEntryKeysInWritingOrder()) {
      var sectionGetter = index.findSectionGetter(key);
      var plainValue = sectionGetter != null ? toPlainSection(readNestedSectionOf(sectionGetter, section)) :
          toPlainValueOfKey(section, index, key);
      if (plainValue != null) {
        fields.put(key, plainValue);
      }
    }
    return fields;
  }

  /**
   * The plain value of a key of settings, read whole through its getter: the value of a replacing key, the entries of
   * an adding key, the values of a map by id being its entries, and the map of a putting key
   *
   * @param settings to read
   * @param index of the keys of the settings
   * @param key to write
   * @return the plain value, null when the key has no value
   * @throws PlanItRunTimeException when the key has no getter
   */
  private Object toPlainValueOfKey(Object settings, SettingsKeyIndex index, String key) {
    var value = index.readValue(settings, key);
    if (value == null) {
      return null;
    }
    var write = index.findWrite(key);
    var types = write.getGenericParameterTypes();
    switch (write.getAnnotation(SettingsWrite.class).effect()) {
    case ADD_EACH:
      var entries = value instanceof Map ? ((Map<?, ?>) value).values() : CollectionUtils.toList(value);
      return toPlainValues(entries, types[0]);
    case PUT_EACH:
      return toPlainMap((Map<?, ?>) value, types[1]);
    default:
      return toPlainValue(value, types[0]);
    }
  }

  /**
   * Verify whether a key or nested section of settings is written. Of every key, a key is written when it has a
   * value, or when descriptions are written, to be commented out; of the keys differing from defaults, when its value
   * differs from theirs. A nested section is written when it has a key to write
   *
   * @param settings holding the key or nested section
   * @param defaults fresh settings of the same class to compare with, null to write every key
   * @param index of the keys of the settings
   * @param key of the key or nested section
   * @return true when it is written, false otherwise
   */
  private boolean isWritten(Object settings, Object defaults, SettingsKeyIndex index, String key) {
    var sectionGetter = index.findSectionGetter(key);
    if (sectionGetter != null) {
      return hasKeysToWrite(readNestedSectionOf(sectionGetter, settings), readNestedSectionOf(sectionGetter, defaults));
    }
    var plainValue = toPlainValueOfKey(settings, index, key);
    if (defaults == null) {
      return plainValue != null || withDescriptions;
    }
    return plainValue != null && !plainValue.equals(toPlainValueOfKey(defaults, index, key));
  }

  /**
   * Verify whether the settings of a section have a key to write, of their own or in a nested section
   *
   * @param settings of the section
   * @param defaults fresh settings of the same class to compare with, null to write every key
   * @return true when a key is written, false when the section has nothing to write
   */
  private boolean hasKeysToWrite(Object settings, Object defaults) {
    var index = SettingsKeyIndex.of(settings.getClass());
    return index.getSectionEntryKeysInWritingOrder().stream()
        .anyMatch(key -> isWritten(settings, defaults, index, key));
  }

  /**
   * Add a key, with the lines describing it above it when descriptions are written
   *
   * @param key to add
   * @param descriptionLines of the key
   * @param plainValue of the key, null to add it commented out
   */
  private void addKey(String key, List<String> descriptionLines, Object plainValue) {
    if (withDescriptions) {
      descriptionLines.forEach(yaml::addComment);
    }
    if (plainValue == null) {
      yaml.addCommentedOutKey(key);
    } else {
      yaml.addEntry(key, plainValue);
    }
  }

  /**
   * Add a section, starting with its description when descriptions are written, then its keys and nested sections
   *
   * @param key of the section
   * @param description of the section
   * @param settings of the section
   * @param defaults fresh settings of the same class to compare with, null to write every key
   */
  private void addSection(String key, String description, Object settings, Object defaults) {
    yaml.startSection(key);
    if (withDescriptions) {
      yaml.addComment(description);
    }
    addKeys(settings, defaults, false);
    yaml.endSection();
  }

  /**
   * Add a key naming the settings file of nested settings, the file next to the file written, named after it followed
   * by the key, keeping a writer of the nested settings to write that file
   *
   * @param key naming the file
   * @param fileReferenceGetter of the nested settings
   * @param settings holding the nested settings
   */
  private void addFileReference(String key, Method fileReferenceGetter, Object settings) {
    var nestedSettingsFile = file.resolveSibling(FileUtils.getFileNameWithoutExtension(file.getFileName().toString())
        + NESTED_SETTINGS_FILE_NAME_SEPARATOR + key + NESTED_SETTINGS_FILE_EXTENSION);
    addKey(key, List.of(fileReferenceGetter.getAnnotation(SettingsFileReference.class).description()),
        nestedSettingsFile.getFileName().toString());
    nestedSettingsWriters.add(new SettingsYamlWriter(schema, ReflectionUtils.invoke(fileReferenceGetter, settings),
        content, withDescriptions, nestedSettingsFile));
  }

  /**
   * Add the keys of settings that are written: their keys at the top level, or the keys and nested sections of a
   * section, in writing order, under a heading per level when descriptions are written. A key naming the file of nested
   * settings is always written
   *
   * @param settings to add the keys of
   * @param defaults fresh settings of the same class to compare with, null to write every key
   * @param atTopLevel true for the keys at the top level, false for those of a section
   */
  private void addKeys(Object settings, Object defaults, boolean atTopLevel) {
    var index = SettingsKeyIndex.of(settings.getClass());
    SettingsKeyLevel headedLevel = null;
    for (var key : atTopLevel ? index.getTopLevelKeysInWritingOrder() : index.getSectionEntryKeysInWritingOrder()) {
      var fileReferenceGetter = index.findFileReferenceGetter(key);
      if (fileReferenceGetter == null && !isWritten(settings, defaults, index, key)) {
        continue;
      }
      if (withDescriptions && index.getLevel(key) != headedLevel) {
        headedLevel = index.getLevel(key);
        yaml.addComment(headingOf(headedLevel));
      }
      var sectionGetter = index.findSectionGetter(key);
      if (fileReferenceGetter != null) {
        addFileReference(key, fileReferenceGetter, settings);
      } else if (sectionGetter == null) {
        addKey(key, describeYamlKey(index.findWrite(key)), toPlainValueOfKey(settings, index, key));
      } else {
        addSection(key, sectionGetter.getAnnotation(SettingsSection.class).description(),
            readNestedSectionOf(sectionGetter, settings), readNestedSectionOf(sectionGetter, defaults));
      }
    }
  }

  /**
   * Constructor, writing the YAML text of settings
   *
   * @param schema the layout of the text, as the loader reads it
   * @param settings to write out: the settings of a section of the schema, settings giving such settings through
   *     getters, or settings filling no section, as the loader makes them
   * @param content which keys to write
   * @param withDescriptions true to write the description of each section and key, and a heading per level, as
   *     comments; false for no comment at all
   * @param file the file the text is for, which the files of the nested settings are named after and written next to
   * @throws PlanItRunTimeException when a key or value at the top level has no getter marked with {@link SettingsRead}
   * @throws IllegalStateException when a section to write holds keys with no value only, which YAML cannot keep
   *     commented out at their depth
   */
  public SettingsYamlWriter(SettingsSchema schema, Object settings, SettingsFileContentType content,
      boolean withDescriptions, Path file) {
    this.schema = schema;
    this.content = content;
    this.withDescriptions = withDescriptions;
    this.file = file;
    var sections = schema.getSectionSettingsOf(settings);
    var sectionKeys = schema.getSectionKeys().stream().filter(sections::containsKey).collect(Collectors.toList());
    /* the keys and values at the top level are read from the settings themselves when they fill no section, otherwise
     * from those of the first section, which settings holding the sections pass them on to */
    var topLevelSettings = sections.isEmpty() ? settings : sections.get(sectionKeys.get(0));
    var topLevelIndex = SettingsKeyIndex.of(topLevelSettings.getClass());
    var index = SettingsKeyIndex.of(settings.getClass());
    var defaults = content == SettingsFileContentType.KEYS_DIFFERING_FROM_DEFAULTS ?
        index.createSettings(key -> readTopLevelValueText(topLevelSettings, topLevelIndex, key)) : null;
    Map<String, Object> defaultSections = defaults == null ? Map.of() : schema.getSectionSettingsOf(defaults);
    var topLevelDefaults =
        defaults == null || sections.isEmpty() ? defaults : defaultSections.get(sectionKeys.get(0));
    if (withDescriptions) {
      yaml.addComment(HEADER_DESCRIPTION);
    }
    yaml.addEntry(schema.getHeader().key(), String.valueOf(schema.getHeader().version()));
    for (var topLevelValue : index.getTopLevelValues()) {
      var text = readTopLevelValueText(topLevelSettings, topLevelIndex, topLevelValue.key());
      if (text != null || (defaults == null && withDescriptions)) {
        addKey(topLevelValue.key(), List.of(topLevelValue.description()), text);
      }
    }
    addKeys(topLevelSettings, topLevelDefaults, true);
    for (var key : sectionKeys) {
      if (hasKeysToWrite(sections.get(key), defaultSections.get(key))) {
        addSection(key, schema.getSection(key).description(), sections.get(key), defaultSections.get(key));
      }
    }
  }

  /**
   * The YAML text of the settings, without the files of their nested settings
   *
   * @return the text
   */
  @Override
  public String toString() {
    return yaml.toString();
  }

  /**
   * Write the YAML text of the settings, as UTF-8, to the file the writer is made for, and the file of each of the
   * nested settings a key names next to it
   *
   * @throws IOException when a file cannot be written
   */
  public void write() throws IOException {
    yaml.write(file);
    for (var nestedSettingsWriter : nestedSettingsWriters) {
      nestedSettingsWriter.write();
    }
  }
}
