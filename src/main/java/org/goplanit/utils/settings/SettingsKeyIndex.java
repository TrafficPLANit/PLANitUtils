package org.goplanit.utils.settings;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.settings.annotations.SettingsKeyEffect;
import org.goplanit.utils.settings.annotations.SettingsRootValue;
import org.goplanit.utils.settings.annotations.SettingsSection;
import org.goplanit.utils.settings.annotations.SettingsWrite;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The settings keys of one settings class, found through its annotations, with the method that writes each key or
 * former key, the getter of each section, and the keys at the root its constructor takes
 * <p>
 * For example, for a class whose method {@code setMaximumReachMeters(double)} carries
 * {@code @SettingsWrite(key = "maximumReachMeters", formerKeys = {"maximumReachInMeters"}, ...)}, both keys find that
 * method, the second as a former key. Each class is looked through once, on first use, and its index kept, since the
 * methods of a class do not change while a program runs. A mistake in the annotations, such as two methods taking one
 * key, is an error in the code rather than in a file, so it throws at once instead of being reported as a problem.
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

  /** the method writing each key */
  private final Map<String, Method> writes = new HashMap<>();

  /** the method writing each former key, under that former key */
  private final Map<String, Method> formerWrites = new HashMap<>();

  /** the getter of each section */
  private final Map<String, Method> sectionGetters = new HashMap<>();

  /** keys at the root the constructor taking root values takes, in the order of its parameters, empty without one */
  private final List<String> rootValueKeys = new ArrayList<>();

  /**
   * Index the annotated public methods of a class, inherited ones included, and its public constructor taking root
   * values. An override of an annotated method repeats the annotation, since Java does not pass annotations on to
   * overrides
   *
   * @param settingsClass to index
   * @throws PlanItRunTimeException when two methods take one key, a method takes the wrong number of arguments, or a
   *     constructor takes root values the way {@link SettingsRootValue} does not allow
   */
  private SettingsKeyIndex(Class<?> settingsClass) {
    for (var method : settingsClass.getMethods()) {
      var write = method.getAnnotation(SettingsWrite.class);
      if (write != null) {
        checkArgumentCount(method, write.effect() == SettingsKeyEffect.PUT_EACH ? 2 : 1);
        register(writes, write.key(), method);
        Arrays.stream(write.formerKeys()).forEach(formerKey -> register(formerWrites, formerKey, method));
      }
      var section = method.getAnnotation(SettingsSection.class);
      if (section != null) {
        checkArgumentCount(method, 0);
        register(sectionGetters, section.key(), method);
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
      PlanItRunTimeException.throwIf(!rootValueKeys.isEmpty(), "%s has more than one constructor taking root values",
          settingsClass.getName());
      PlanItRunTimeException.throwIf(
          Arrays.stream(parameters).anyMatch(parameter -> parameter.getType() != String.class),
          "Root values are text, %s takes other values", constructor);
      rootValues.forEach(rootValue -> rootValueKeys.add(rootValue.key()));
    }
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
   * The method writing a key by a name it had before
   *
   * @param formerKey to find
   * @return the method, null when no key had that name
   */
  public Method findFormerWrite(String formerKey) {
    return formerWrites.get(formerKey);
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
    return Collections.unmodifiableList(rootValueKeys);
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
