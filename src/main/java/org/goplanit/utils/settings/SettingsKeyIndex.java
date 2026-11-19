package org.goplanit.utils.settings;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.reflection.AnnotationUtils;
import org.goplanit.utils.reflection.ReflectionUtils;
import org.goplanit.utils.settings.annotations.SettingsHeader;
import org.goplanit.utils.settings.annotations.SettingsKeyEffect;
import org.goplanit.utils.settings.annotations.SettingsKeyLevel;
import org.goplanit.utils.settings.annotations.SettingsRead;
import org.goplanit.utils.settings.annotations.SettingsRootValue;
import org.goplanit.utils.settings.annotations.SettingsSection;
import org.goplanit.utils.settings.annotations.SettingsWrite;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The settings keys of one settings class, found through its annotations, with the method that writes each key or
 * former key and the getter that reads it, the level of each key and section, which keys sit at the root, the getter
 * of each section, and the keys at the root its constructor takes
 * <p>
 * For example, for a class whose method {@code setMaximumReachMeters(double)} carries
 * {@code @SettingsWrite(key = "maximumReachMeters", formerKeys = {"maximumReachInMeters"}, ...)}, both keys find that
 * method, the second as a former key. Each class is looked through once, on first use, and its index kept, since the
 * methods of a class do not change while a program runs. A mistake in the annotations, such as two methods taking one
 * key, is an error in the code rather than in a file, so it throws at once instead of being reported as a problem.
 * Keys and sections go by level and then by name, which is the order a settings file holds them in and nested sections
 * apply in, so the loader and the writer share it.
 * </p>
 *
 * @author markr
 */
final class SettingsKeyIndex {

  /** the index of each settings class met so far */
  private static final Map<Class<?>, SettingsKeyIndex> INDEXES = new ConcurrentHashMap<>();

  /**
   * Check a method takes as many arguments as its annotation needs
   *
   * @param method to check
   * @param count of arguments needed
   * @throws PlanItRunTimeException when it takes another number
   */
  private static void checkArgumentCount(Method method, int count) {
    PlanItRunTimeException.throwIf(method.getParameterCount() != count,
        "Method %s takes %d arguments, its settings annotation needs %d", method, method.getParameterCount(), count);
  }

  /** the class indexed */
  private final Class<?> settingsClass;

  /** the method writing each key */
  private final Map<String, Method> writes = new HashMap<>();

  /** the getter reading the whole value of each key, the keys at the root the constructor takes included */
  private final Map<String, Method> reads = new HashMap<>();

  /** the level of each key and section */
  private final Map<String, SettingsKeyLevel> levels = new HashMap<>();

  /** the method writing each former key, under that former key */
  private final Map<String, Method> formerWrites = new HashMap<>();

  /** the methods writing keys at the root of the sources */
  private final Set<Method> rootWrites = new HashSet<>();

  /** the getter of each section */
  private final Map<String, Method> sectionGetters = new HashMap<>();

  /** values at the root the constructor taking root values takes, in the order of its parameters, empty without one */
  private final List<SettingsRootValue> rootValues = new ArrayList<>();

  /**
   * Index the annotated public methods of a class, inherited ones included, and its public constructor taking root
   * values. An override of an annotated method repeats the annotation, since Java does not pass annotations on to
   * overrides
   *
   * @param settingsClass to index
   * @throws PlanItRunTimeException when two methods take one key or read one key, a method takes the wrong number of
   *     arguments, or a constructor takes root values the way {@link SettingsRootValue} does not allow
   */
  private SettingsKeyIndex(Class<?> settingsClass) {
    this.settingsClass = settingsClass;
    /* the keys at the root are those of the class declaring the header, when the classes of the sections share it,
     * so it fills no section itself */
    var headerClass = AnnotationUtils.findNearestClassDeclaring(settingsClass, SettingsHeader.class);
    var rootKeyClass = headerClass != null && headerClass.getDeclaredAnnotation(SettingsSection.class) == null ?
        headerClass : null;
    for (var method : settingsClass.getMethods()) {
      var write = method.getAnnotation(SettingsWrite.class);
      if (write != null) {
        checkArgumentCount(method, write.effect() == SettingsKeyEffect.PUT_EACH ? 2 : 1);
        register(writes, write.key(), method);
        Arrays.stream(write.formerKeys()).forEach(formerKey -> register(formerWrites, formerKey, method));
        if (method.getDeclaringClass() == rootKeyClass) {
          rootWrites.add(method);
        }
        levels.put(write.key(), write.level());
      }
      var read = method.getAnnotation(SettingsRead.class);
      if (read != null) {
        checkArgumentCount(method, 0);
        PlanItRunTimeException.throwIf(reads.containsKey(read.key()), "Settings key %s is read by both %s and %s",
            read.key(), reads.get(read.key()), method);
        reads.put(read.key(), method);
      }
      var section = method.getAnnotation(SettingsSection.class);
      if (section != null) {
        checkArgumentCount(method, 0);
        register(sectionGetters, section.key(), method);
        levels.put(section.key(), section.level());
      }
    }
    indexRootValueConstructor(settingsClass);
  }

  /**
   * Register the method of a key, a key being taken once only, whether as key, former key or section
   *
   * @param target the map to register it in
   * @param key to register
   * @param method of the key
   * @throws PlanItRunTimeException when the key is taken already
   */
  private void register(Map<String, Method> target, String key, Method method) {
    var taken = Optional.ofNullable(writes.get(key)).or(() -> Optional.ofNullable(formerWrites.get(key)))
        .or(() -> Optional.ofNullable(sectionGetters.get(key)));
    PlanItRunTimeException.throwIf(taken.isPresent(), "Settings key %s is taken by both %s and %s", key,
        taken.orElse(null), method);
    target.put(key, method);
  }

  /**
   * Index the keys of the public constructor of a class taking root values, every parameter of which carries
   * {@link SettingsRootValue}
   *
   * @param settingsClass to index
   * @throws PlanItRunTimeException when a constructor takes root values the way {@link SettingsRootValue} does not
   *     allow: some parameters only, values other than text, or in more than one constructor
   */
  private void indexRootValueConstructor(Class<?> settingsClass) {
    for (var constructor : settingsClass.getConstructors()) {
      var parameters = constructor.getParameters();
      var rootValues = Arrays.stream(parameters).map(parameter -> parameter.getAnnotation(SettingsRootValue.class))
          .filter(Objects::nonNull).collect(Collectors.toList());
      if (rootValues.isEmpty()) {
        continue;
      }
      PlanItRunTimeException.throwIf(rootValues.size() != parameters.length,
          "Every parameter of %s takes a root value, or none does", constructor);
      PlanItRunTimeException.throwIf(!this.rootValues.isEmpty(),
          "%s has more than one constructor taking root values", settingsClass.getName());
      PlanItRunTimeException.throwIf(
          Arrays.stream(parameters).anyMatch(parameter -> parameter.getType() != String.class),
          "Root values are text, %s takes other values", constructor);
      this.rootValues.addAll(rootValues);
    }
  }

  /**
   * The plain keys among the given ones, by level and then by name: a list's {@code +} and {@code -} forms are left
   * out, as the whole list is written through its getter
   *
   * @param keys to order, of keys and sections
   * @return the keys, in order
   */
  private List<String> plainKeysByLevelThenName(Stream<String> keys) {
    return keys.filter(SettingsKeyIndex::isPlainKey).sorted(byLevelThenName()).collect(Collectors.toList());
  }

  /** suffix of a key that removes entries from a list, such as {@code types-} */
  public static final String REMOVE_SUFFIX = "-";

  /** suffix of a key that adds entries to a list, such as {@code types+} */
  public static final String ADD_SUFFIX = "+";

  /**
   * Where a key comes when the forms of one list in a section apply: the whole list first, then the removals, then
   * the additions. For example {@code types: [a, b]}, {@code types-: [b]} and {@code types+: [c]} give {@code [a, c]},
   * in whatever order they are written
   *
   * @param key to place
   * @return 0 for a plain key, 1 for one ending in {@code -}, 2 for one ending in {@code +}
   */
  public static int applyingOrderOfListForm(String key) {
    return key.endsWith(REMOVE_SUFFIX) ? 1 : (key.endsWith(ADD_SUFFIX) ? 2 : 0);
  }

  /**
   * Whether a key is a plain key, ending in neither {@code +} nor {@code -}, such as {@code types} rather than
   * {@code types+}
   *
   * @param key to check
   * @return true for a plain key
   */
  public static boolean isPlainKey(String key) {
    return !key.endsWith(REMOVE_SUFFIX) && !key.endsWith(ADD_SUFFIX);
  }

  /**
   * The list a key belongs to: the key without its {@code +} or {@code -}
   *
   * @param key as given in the source
   * @return the key of the whole list, the key itself when it is a plain key
   */
  public static String listKeyOf(String key) {
    return isPlainKey(key) ? key : key.substring(0, key.length() - 1);
  }

  /**
   * The settings keys of a settings class, looked through on first use
   *
   * @param settingsClass to get the keys of
   * @return its index
   * @throws PlanItRunTimeException when its annotations are wrong, as the constructor says
   */
  public static SettingsKeyIndex of(Class<?> settingsClass) {
    return INDEXES.computeIfAbsent(settingsClass, SettingsKeyIndex::new);
  }

  /**
   * The method writing a key
   *
   * @param key to find
   * @return the method, null when the class has no such key
   */
  public Method findWrite(String key) {
    return writes.get(key);
  }

  /**
   * The whole value of a key of settings of the class, through the getter marked with {@link SettingsRead}
   *
   * @param settings to read
   * @param key to read, a key at the root the constructor takes included
   * @return the value, null when the key has none
   * @throws PlanItRunTimeException when the class has no getter for the key, a mistake in its annotations
   */
  public Object readValue(Object settings, String key) {
    var read = reads.get(key);
    PlanItRunTimeException.throwIf(read == null, "Settings key %s of %s has no getter marked with @SettingsRead", key,
        settingsClass.getName());
    return ReflectionUtils.invoke(read, settings);
  }

  /**
   * The level of a key or section
   *
   * @param key of the key or section
   * @return the level, null when the class has no such key or section
   */
  public SettingsKeyLevel getLevel(String key) {
    return levels.get(key);
  }

  /**
   * The order of keys and sections by level and then by name: the order a settings file holds them in, and nested
   * sections apply in, so the more general settings of a section apply before the more particular ones below it
   *
   * @return the order of the keys and sections of the class
   */
  public Comparator<String> byLevelThenName() {
    return Comparator.comparing((String key) -> levels.get(key)).thenComparing(Comparator.naturalOrder());
  }

  /**
   * The plain keys at the root of a settings file, by level and then by name
   *
   * @return the keys, in order, the {@code +} and {@code -} forms of a list left out
   */
  public List<String> getRootKeysByLevelThenName() {
    return plainKeysByLevelThenName(writes.entrySet().stream()
        .filter(entry -> rootWrites.contains(entry.getValue())).map(Map.Entry::getKey));
  }

  /**
   * The plain keys of a section, and its nested sections, by level and then by name
   *
   * @return the keys and sections, in order, the {@code +} and {@code -} forms of a list left out
   */
  public List<String> getSectionEntryKeysByLevelThenName() {
    var keys = writes.entrySet().stream().filter(entry -> !rootWrites.contains(entry.getValue()))
        .map(Map.Entry::getKey);
    return plainKeysByLevelThenName(Stream.concat(keys, sectionGetters.keySet().stream()));
  }

  /**
   * The method writing a key by a name it had before
   *
   * @param formerKey to find
   * @return the method, null when no key had that name
   */
  public Method findFormerWrite(String formerKey) {
    return formerWrites.get(formerKey);
  }

  /**
   * Whether a method writes a key at the root of the sources, declared on the class giving the header above the
   * sections, rather than a key of a section
   *
   * @param write method writing a key
   * @return true when its key sits at the root
   */
  public boolean isRootWrite(Method write) {
    return rootWrites.contains(write);
  }

  /**
   * The getter of a section
   *
   * @param key of the section
   * @return the getter, null when the class has no such section
   */
  public Method findSectionGetter(String key) {
    return sectionGetters.get(key);
  }

  /**
   * The keys at the root of the sources its constructor taking root values takes
   *
   * @return the keys, in the order of its parameters, empty when it has no such constructor
   */
  public List<String> getRootValueKeys() {
    return rootValues.stream().map(SettingsRootValue::key).collect(Collectors.toUnmodifiableList());
  }

  /**
   * The values at the root of the sources its constructor taking root values takes
   *
   * @return their annotations, in the order of its parameters, empty when it has no such constructor
   */
  public List<SettingsRootValue> getRootValues() {
    return Collections.unmodifiableList(rootValues);
  }

  /**
   * Settings of the class: made with its constructor taking root values when every one of them is given, otherwise
   * with its constructor taking nothing
   *
   * @param rootValueOf the text of a key at the root, null when it is not given
   * @return the settings
   */
  public Object createSettings(Function<String, String> rootValueOf) {
    var values = rootValues.stream().map(rootValue -> rootValueOf.apply(rootValue.key())).toArray();
    var allGiven = values.length > 0 && Arrays.stream(values).allMatch(Objects::nonNull);
    return ReflectionUtils.createInstance(settingsClass.getName(), allGiven ? values : new Object[0]);
  }

  /**
   * Whether the class has keys, so it can be a small section, the value of a key
   *
   * @return true when it has at least one key or section
   */
  public boolean hasKeys() {
    return !writes.isEmpty() || !sectionGetters.isEmpty();
  }
}
