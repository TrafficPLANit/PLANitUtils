package org.goplanit.utils.settings;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.reflection.ReflectionUtils;
import org.goplanit.utils.settings.annotations.SettingsHeader;
import org.goplanit.utils.settings.annotations.SettingsSection;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The layout of the sources of some settings: the settings classes filling the sections at their top level, which give
 * the header the sources start with
 * <p>
 * For example, with {@code ExampleNetworkSettings} marked {@code @SettingsSection(key = "network", ...)} and
 * {@code @SettingsHeader(key = "planitExampleSettings", version = 1)}, and its constructor taking the country
 * ({@code @SettingsTopLevelValue(key = "country", ...)}), the schema
 * </p>
 * <pre>
 * public static final SettingsSchema SCHEMA = new SettingsSchema(ExampleNetworkSettings.class);
 * </pre>
 * <p>
 * describes the source
 * </p>
 * <pre>
 * planitExampleSettings: 1
 * country: Australia
 * network:
 *   maximumSpeed: 80
 * </pre>
 * <p>
 * The header, the name of each section and the values a constructor takes are on the settings classes, as the keys
 * inside a section are (see {@link SettingsYamlLoader}), so nothing about the layout is kept apart from the classes
 * it is about. The schema says which classes make up the sources. A section is found from the class of the settings
 * that fill it, so code making settings from sources names no section; for that, no class of a section is a
 * subclass of another's. The sections apply in the order the schema gives them. The converters of types that are
 * neither plain values nor small sections are registered on the schema too ({@link SettingsValueConverter}).
 * </p>
 * <p>
 * Settings filling no section have a schema of their one class, which gives the header: every key of their class sits
 * at the top level, so {@code new SettingsSchema(ExampleWriterSettings.class)} describes
 * {@code planitExampleWriterSettings: 1} followed by {@code outputDirectory: out}, and a file holds the settings of one
 * object.
 * </p>
 *
 * @author markr
 */
public final class SettingsSchema {

  /** the header every source starts with, given by every class of a section */
  private final SettingsHeader header;

  /** class of the settings each section at the top level fills, by its key, in the order given */
  private final Map<String, Class<?>> sectionClasses = new LinkedHashMap<>();

  /** the converter of each type that is neither a plain value nor a small section, by the type */
  private final Map<Class<?>, SettingsValueConverter<?>> converters = new HashMap<>();

  /**
   * Constructor
   *
   * @param settingsClasses of the settings filling the sections at the top level, each marked with
   *     {@link SettingsSection}, all giving the same {@link SettingsHeader}, no class a subclass of another; or the one
   *     class giving the header of settings filling no section
   */
  public SettingsSchema(Class<?>... settingsClasses) {
    this(List.of(), settingsClasses);
  }

  /**
   * Constructor
   * <p>
   * A schema whose sections or converters could not be told apart, such as two classes naming one section or two
   * converters of one type, would fill the wrong settings without a word. That is a mistake in the code rather than
   * in a file, so it throws at once, before any source is read
   * </p>
   *
   * @param converters making the values of keys of types that are neither plain values nor small sections, one per
   *     type
   * @param settingsClasses of the settings filling the sections at the top level, each marked with
   *     {@link SettingsSection}, all giving the same {@link SettingsHeader}, no class a subclass of another; or the one
   *     class giving the header of settings filling no section
   */
  public SettingsSchema(List<SettingsValueConverter<?>> converters, Class<?>... settingsClasses) {
    PlanItRunTimeException.throwIf(settingsClasses.length == 0, "A schema needs at least one settings class");
    this.header = settingsClasses[0].getAnnotation(SettingsHeader.class);
    if (settingsClasses.length == 1 && settingsClasses[0].getAnnotation(SettingsSection.class) == null) {
      PlanItRunTimeException.throwIf(header == null, "%s gives no header", settingsClasses[0].getName());
    } else {
      for (var sectionClass : settingsClasses) {
        var section = sectionClass.getAnnotation(SettingsSection.class);
        PlanItRunTimeException.throwIf(section == null, "%s is not marked with the section it fills",
            sectionClass.getName());
        PlanItRunTimeException.throwIf(
            header == null || !header.equals(sectionClass.getAnnotation(SettingsHeader.class)),
            "%s does not give the header of the schema", sectionClass.getName());
        for (var known : this.sectionClasses.values()) {
          PlanItRunTimeException.throwIf(known.isAssignableFrom(sectionClass) || sectionClass.isAssignableFrom(known),
              "Sections of %s and %s cannot be told apart", known.getName(), sectionClass.getName());
        }
        PlanItRunTimeException.throwIf(this.sectionClasses.containsKey(section.key()), "Section %s is named twice",
            section.key());
        this.sectionClasses.put(section.key(), sectionClass);
      }
    }
    for (var converter : converters) {
      PlanItRunTimeException.throwIf(this.converters.containsKey(converter.getValueType()),
          "Two converters of %s", converter.getValueType().getName());
      this.converters.put(converter.getValueType(), converter);
    }
  }

  /**
   * The header every source starts with
   *
   * @return the header
   */
  public SettingsHeader getHeader() {
    return header;
  }

  /**
   * Whether a key at the top level is a section
   *
   * @param key at the top level
   * @return true when it is
   */
  public boolean isSectionKey(String key) {
    return sectionClasses.containsKey(key);
  }

  /**
   * The key of the section settings of a class fill
   *
   * @param settingsClass to find the section of
   * @return the key of the section whose class it is or extends, null when there is none
   */
  public String findSectionKeyOf(Class<?> settingsClass) {
    return sectionClasses.entrySet().stream().filter(entry -> entry.getValue().isAssignableFrom(settingsClass))
        .map(Map.Entry::getKey).findFirst().orElse(null);
  }

  /**
   * The keys of the sections at the top level, in the order the schema gives them, which is the order they apply in
   *
   * @return the keys, unmodifiable
   */
  public List<String> getSectionKeys() {
    return List.copyOf(sectionClasses.keySet());
  }

  /**
   * The annotation of a section at the top level, on the class of its settings, giving its level and description
   *
   * @param key of the section
   * @return the annotation, null when the schema has no such section
   */
  public SettingsSection getSection(String key) {
    var sectionClass = sectionClasses.get(key);
    return sectionClass == null ? null : sectionClass.getAnnotation(SettingsSection.class);
  }

  /**
   * The settings filling each section of the schema: the settings themselves when their class names a section,
   * otherwise what their getters give, found by the class each gives
   *
   * @param settings to find the sections of
   * @return the settings of each section, by its key, empty for a schema of settings filling no section
   * @throws PlanItRunTimeException when the settings fill no section of a schema with sections and give none, or give
   *     one twice
   */
  public Map<String, Object> getSectionSettingsOf(Object settings) {
    if (sectionClasses.isEmpty()) {
      return Map.of();
    }
    var ownKey = findSectionKeyOf(settings.getClass());
    if (ownKey != null) {
      return Map.of(ownKey, settings);
    }
    var sections = new LinkedHashMap<String, Object>();
    for (var getter : settings.getClass().getMethods()) {
      var key = getter.getParameterCount() == 0 && !getter.isBridge() ? findSectionKeyOf(getter.getReturnType()) : null;
      if (key == null) {
        continue;
      }
      PlanItRunTimeException.throwIf(sections.containsKey(key), "%s gives section %s twice",
          settings.getClass().getName(), key);
      sections.put(key, ReflectionUtils.invoke(getter, settings));
    }
    PlanItRunTimeException.throwIf(sections.isEmpty(), "%s fill no section of the schema and give none",
        settings.getClass().getName());
    return sections;
  }

  /**
   * The converter of a type
   *
   * @param valueType a method of a key takes
   * @return its converter, null when it has none
   */
  public SettingsValueConverter<?> findConverter(Class<?> valueType) {
    return converters.get(valueType);
  }
}
