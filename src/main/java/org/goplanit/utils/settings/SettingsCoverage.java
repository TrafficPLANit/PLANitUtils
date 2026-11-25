package org.goplanit.utils.settings;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.goplanit.utils.reflection.AnnotationUtils;
import org.goplanit.utils.reflection.ReflectionUtils;
import org.goplanit.utils.settings.annotations.CoveredBySettingsKey;
import org.goplanit.utils.settings.annotations.DelegatedToSyncedInternalSettingsKeys;
import org.goplanit.utils.settings.annotations.MarksGetterOfNestedSettings;
import org.goplanit.utils.settings.annotations.MarksMethodChangingSettings;
import org.goplanit.utils.settings.annotations.NoSettingsKey;
import org.goplanit.utils.settings.annotations.SettingsFileReference;
import org.goplanit.utils.settings.annotations.SettingsRead;
import org.goplanit.utils.settings.annotations.SettingsSection;
import org.goplanit.utils.settings.annotations.SettingsWrite;

/**
 * Checks that the annotations of settings classes cover those settings: every method changing settings is a key, is
 * covered by one, or is marked as no key; every getter of nested settings is a section, names the file of settings
 * held, is the read of a key, or is marked as no key; every key has a read; and every key a method is covered by
 * exists
 * <p>
 * For example, a test of a converter's settings asserts that each check finds nothing:
 * </p>
 * <pre>
 * var coverage = SettingsCoverage.of(IntermodalReaderSettings.class);
 * assertEquals(List.of(), coverage.findUnmarkedMethodsChangingSettings());
 * </pre>
 * <p>
 * A method changing settings that a settings file cannot reach goes unnoticed until someone looks for the key, so
 * these checks fail as soon as one is added without a marker. The classes are found by following, from the settings
 * given, every getter returning settings, such as the getter of nested defaults, so no class hides from the checks. It
 * sits with the settings code so that the tests of every converter's settings run the same checks.
 * </p>
 * <p>
 * Nothing is told apart by a list of names of its own. An annotation counts by the mark on its definition:
 * {@link MarksMethodChangingSettings} for those saying how a file reaches a method changing settings, and
 * {@link MarksGetterOfNestedSettings} for those saying how it reaches nested settings, so an annotation added later
 * counts once its definition carries the mark. A class is settings when it names a section or has a method carrying a
 * marked annotation. A method changes settings when it returns nothing, or takes arguments and returns settings, as a
 * fluent setter does, whatever its name, unless Java's own convention names it as a getter ({@code get} or
 * {@code is}), as one looking up settings by an argument is. A method of {@code Object} or of an interface that is not
 * settings, such as
 * {@code reset} and {@code logSettings} of the interfaces of converter settings or {@code writeSettingsFile}, keeps
 * the life cycle of the settings and is left out; any other method changing settings carries a mark, logging methods
 * of a settings class's own included.
 * </p>
 *
 * @author markr
 */
public final class SettingsCoverage {

  /** the package of every PLANit class, whose methods are checked */
  private static final String PLANIT_PACKAGE = "org.goplanit";

  /** what the name of a getter starts with, as Java's own convention for getters (JavaBeans) has it */
  private static final List<String> GETTER_NAME_PREFIXES = List.of("get", "is");

  /**
   * Verify whether a method is a public method of an object, declared by a PLANit class, and written in the code
   *
   * @param method to check
   * @return true when it is
   */
  private static boolean isPlanitMethod(Method method) {
    return Modifier.isPublic(method.getModifiers()) && !Modifier.isStatic(method.getModifiers()) &&
        !method.isSynthetic() && !method.isBridge() &&
        method.getDeclaringClass().getPackageName().startsWith(PLANIT_PACKAGE);
  }

  /**
   * Verify whether a method is a method of {@code Object} or of an interface that is not settings, declared there or
   * overriding it, such as {@code reset} and {@code logSettings} of the interfaces of converter settings,
   * {@code writeSettingsFile} of {@link YamlWriteSettingsSupport}, or {@code equals}. Such a method keeps the life cycle
   * of the settings rather than being one of them, so it is no key
   *
   * @param method to check
   * @return true when it is
   */
  private static boolean isMethodOfObjectOrOfInterfaceOutsideSettings(Method method) {
    var declaringClass = method.getDeclaringClass();
    return Stream.concat(Stream.of(declaringClass, Object.class),
            ReflectionUtils.findInterfaces(declaringClass).stream())
        .filter(type -> type == Object.class || (type.isInterface() && !isSettingsClass(type)))
        .anyMatch(type -> Arrays.stream(type.getDeclaredMethods())
            .anyMatch(declared -> ReflectionUtils.isSameSignature(declared, method)));
  }

  /**
   * Verify whether a method changes settings: it returns nothing, or it takes arguments and returns settings without
   * being named as a getter, as a fluent setter returning its settings does; and it is no method of {@code Object} or
   * of an interface that is not settings. A getter looking up settings by an argument, such as
   * {@code getOverride(long id)}, changes nothing
   *
   * @param method to check
   * @return true when it changes settings
   */
  private static boolean changesSettings(Method method) {
    return !isMethodOfObjectOrOfInterfaceOutsideSettings(method) && (method.getReturnType() == void.class ||
        (method.getParameterCount() > 0 && !settingsClassesReturnedBy(method).isEmpty() &&
            GETTER_NAME_PREFIXES.stream().noneMatch(prefix -> method.getName().startsWith(prefix))));
  }

  /**
   * Verify whether a method is a getter: it takes nothing, returns a value, and is no method of {@code Object} or of an
   * interface that is not settings
   *
   * @param method to check
   * @return true when it is a getter
   */
  private static boolean isGetter(Method method) {
    return method.getParameterCount() == 0 && method.getReturnType() != void.class &&
        !isMethodOfObjectOrOfInterfaceOutsideSettings(method);
  }

  /**
   * Verify whether a class is settings: a PLANit class naming a section, or with a public method carrying an annotation
   * marked with {@link MarksMethodChangingSettings} or {@link MarksGetterOfNestedSettings}
   *
   * @param candidate to check
   * @return true when it is settings
   */
  private static boolean isSettingsClass(Class<?> candidate) {
    return candidate.getPackageName().startsWith(PLANIT_PACKAGE) &&
        (candidate.isAnnotationPresent(SettingsSection.class) || Arrays.stream(candidate.getMethods())
            .anyMatch(method -> !AnnotationUtils.getAnnotationsMarkedWith(method, MarksMethodChangingSettings.class)
                .isEmpty() || !AnnotationUtils.getAnnotationsMarkedWith(method, MarksGetterOfNestedSettings.class)
                .isEmpty()));
  }

  /**
   * The settings classes a getter returns, through the classes its declared type is made of
   *
   * @param getter to look at
   * @return the settings classes, empty when it returns none
   */
  private static Set<Class<?>> settingsClassesReturnedBy(Method getter) {
    return ReflectionUtils.findClassesIn(getter.getGenericReturnType()).stream()
        .filter(SettingsCoverage::isSettingsClass).collect(Collectors.toCollection(LinkedHashSet::new));
  }

  /**
   * The settings classes some settings reach, themselves included, by following every getter returning settings
   *
   * @param rootSettingsClasses to start from
   * @return the classes, in the order found
   */
  private static Set<Class<?>> findReachableSettingsClasses(Class<?>... rootSettingsClasses) {
    var found = new LinkedHashSet<Class<?>>();
    var toVisit = new ArrayDeque<Class<?>>(List.of(rootSettingsClasses));
    while (!toVisit.isEmpty()) {
      var settingsClass = toVisit.poll();
      if (!found.add(settingsClass)) {
        continue;
      }
      Arrays.stream(settingsClass.getMethods()).filter(method -> isPlanitMethod(method) && isGetter(method))
          .flatMap(getter -> settingsClassesReturnedBy(getter).stream()).forEach(toVisit::add);
    }
    return found;
  }

  /**
   * The keys and sections of a settings class, by name: its writes, reads and sections, inherited ones included
   *
   * @param settingsClass to take the keys of
   * @return the names
   */
  private static Set<String> findKeysAndSections(Class<?> settingsClass) {
    var names = new HashSet<String>();
    for (var method : settingsClass.getMethods()) {
      Stream.of(method.getAnnotation(SettingsWrite.class)).filter(write -> write != null)
          .forEach(write -> names.add(write.key()));
      Stream.of(method.getAnnotation(SettingsRead.class)).filter(read -> read != null)
          .forEach(read -> names.add(read.key()));
      Stream.of(method.getAnnotation(SettingsSection.class)).filter(section -> section != null)
          .forEach(section -> names.add(section.key()));
    }
    return names;
  }

  /**
   * The classes of the sections of a key among settings classes, through the getters of that section
   *
   * @param settingsClasses to look in
   * @param sectionKey of the sections
   * @return the classes of the sections
   */
  private static Set<Class<?>> findSectionClasses(Collection<Class<?>> settingsClasses, String sectionKey) {
    return settingsClasses.stream().flatMap(settingsClass -> Arrays.stream(settingsClass.getMethods()))
        .filter(getter -> getter.isAnnotationPresent(SettingsSection.class) &&
            getter.getAnnotation(SettingsSection.class).key().equals(sectionKey))
        .map(Method::getReturnType).collect(Collectors.toSet());
  }

  /**
   * Verify whether a key path names a key or section of a settings class: the section's name, a dot and the key for a
   * key of a nested section. A class holding the settings of sections at the top level, such as intermodal settings
   * holding settings that each name their section, has their keys at the top level too
   *
   * @param settingsClass the path is relative to
   * @param keyPath to look up
   * @return true when it names a key or section
   */
  private static boolean namesKeyOrSection(Class<?> settingsClass, String keyPath) {
    Collection<Class<?>> classes = Stream.concat(Stream.of(settingsClass), Arrays.stream(settingsClass.getMethods())
        .filter(getter -> isPlanitMethod(getter) && isGetter(getter) &&
            getter.getReturnType().isAnnotationPresent(SettingsSection.class))
        .map(Method::getReturnType)).collect(Collectors.toSet());
    var keys = keyPath.split("\\.");
    for (int index = 0; index < keys.length - 1; index++) {
      classes = findSectionClasses(classes, keys[index]);
    }
    var lastKey = keys[keys.length - 1];
    return classes.stream().anyMatch(keyClass -> findKeysAndSections(keyClass).contains(lastKey));
  }

  /**
   * Verify whether every settings a class holds internally, those whose getter names their settings file, takes a key
   *
   * @param holderClass holding the settings
   * @param key to look up
   * @return true when it holds such settings and every one of them takes the key
   */
  private static boolean takenByEverySettingsHeld(Class<?> holderClass, String key) {
    var heldClasses = Arrays.stream(holderClass.getMethods())
        .filter(getter -> getter.isAnnotationPresent(SettingsFileReference.class)).map(Method::getReturnType)
        .collect(Collectors.toList());
    return !heldClasses.isEmpty() && heldClasses.stream().allMatch(held -> findKeysAndSections(held).contains(key));
  }

  /**
   * Verify whether the keys a method is covered by, or delegated to, all exist: on its class, or on every settings its
   * class holds internally
   *
   * @param method to check
   * @return true when every key it names exists, or it names none
   */
  private static boolean namesOnlyKnownKeys(Method method) {
    var covered = method.getAnnotation(CoveredBySettingsKey.class);
    if (covered != null && !Arrays.stream(covered.value())
        .allMatch(keyPath -> namesKeyOrSection(method.getDeclaringClass(), keyPath))) {
      return false;
    }
    var delegated = method.getAnnotation(DelegatedToSyncedInternalSettingsKeys.class);
    return delegated == null || Arrays.stream(delegated.value())
        .allMatch(key -> takenByEverySettingsHeld(method.getDeclaringClass(), key));
  }

  /**
   * A method as a check lists it, by its class and name
   *
   * @param method to name
   * @return the name
   */
  private static String nameOf(Method method) {
    return method.getDeclaringClass().getSimpleName() + "#" + method.getName() +
        Arrays.stream(method.getParameterTypes()).map(Class::getSimpleName).collect(Collectors.joining(",", "(", ")"));
  }

  /** the settings classes checked */
  private final Set<Class<?>> settingsClasses;

  /**
   * Constructor
   *
   * @param settingsClasses to check
   */
  private SettingsCoverage(Set<Class<?>> settingsClasses) {
    this.settingsClasses = settingsClasses;
  }

  /**
   * The public methods of PLANit classes the settings classes have, each once, inherited ones included
   *
   * @return the methods
   */
  private Set<Method> findPlanitMethods() {
    return settingsClasses.stream().flatMap(settingsClass -> Arrays.stream(settingsClass.getMethods()))
        .filter(SettingsCoverage::isPlanitMethod).collect(Collectors.toCollection(LinkedHashSet::new));
  }

  /**
   * The coverage of some settings and of every settings class they reach through getters returning settings
   *
   * @param rootSettingsClasses to start from, such as the intermodal reader settings of a converter
   * @return the coverage
   */
  public static SettingsCoverage of(Class<?>... rootSettingsClasses) {
    return new SettingsCoverage(findReachableSettingsClasses(rootSettingsClasses));
  }

  /**
   * The methods changing settings without exactly one annotation marked with {@link MarksMethodChangingSettings}, such
   * as {@link SettingsWrite}, {@link CoveredBySettingsKey}, {@link DelegatedToSyncedInternalSettingsKeys} and
   * {@link NoSettingsKey}
   *
   * @return the methods, by class and name, sorted
   */
  public List<String> findUnmarkedMethodsChangingSettings() {
    return findPlanitMethods().stream()
        .filter(method -> changesSettings(method) &&
            AnnotationUtils.getAnnotationsMarkedWith(method, MarksMethodChangingSettings.class).size() != 1)
        .map(SettingsCoverage::nameOf).sorted().collect(Collectors.toList());
  }

  /**
   * The getters of nested settings without an annotation marked with {@link MarksGetterOfNestedSettings}, such as
   * {@link SettingsSection}, {@link SettingsFileReference}, {@link SettingsRead} and {@link NoSettingsKey}, leaving out
   * those returning settings that name their own section, such as the network settings intermodal settings hold
   *
   * @return the getters, by class and name, sorted
   */
  public List<String> findUnmarkedGettersOfNestedSettings() {
    return findPlanitMethods().stream()
        .filter(method -> isGetter(method) && !settingsClassesReturnedBy(method).isEmpty() &&
            AnnotationUtils.getAnnotationsMarkedWith(method, MarksGetterOfNestedSettings.class).isEmpty() &&
            !method.getReturnType().isAnnotationPresent(SettingsSection.class))
        .map(SettingsCoverage::nameOf).sorted().collect(Collectors.toList());
  }

  /**
   * The keys without a read of the same key in their class, the {@code +} and {@code -} forms of a list taking that of
   * the whole list ({@link SettingsKeyIndex#listKeyOf(String)}), so settings could not be written out
   *
   * @return the keys, as their class, a colon and the key, sorted
   */
  public List<String> findKeysWithoutRead() {
    var withoutRead = new HashSet<String>();
    for (var settingsClass : settingsClasses) {
      var readKeys = Arrays.stream(settingsClass.getMethods()).filter(method -> method.isAnnotationPresent(
          SettingsRead.class)).map(method -> method.getAnnotation(SettingsRead.class).key()).collect(Collectors.toSet());
      Arrays.stream(settingsClass.getMethods()).filter(method -> method.isAnnotationPresent(SettingsWrite.class))
          .map(method -> method.getAnnotation(SettingsWrite.class).key())
          .filter(key -> !readKeys.contains(SettingsKeyIndex.listKeyOf(key)))
          .forEach(key -> withoutRead.add(settingsClass.getSimpleName() + ": " + key));
    }
    return withoutRead.stream().sorted().collect(Collectors.toList());
  }

  /**
   * The methods covered by a key or section that is not on their class, a key of a nested section found through the
   * path of its sections, or delegated to a key that not every settings their class holds internally takes
   *
   * @return the methods, by class and name, sorted
   */
  public List<String> findUnknownCoveringKeys() {
    return findPlanitMethods().stream().filter(method -> !namesOnlyKnownKeys(method))
        .map(SettingsCoverage::nameOf).sorted().collect(Collectors.toList());
  }
}
