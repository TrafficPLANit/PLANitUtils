package org.goplanit.utils.settings;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.settings.annotations.SettingsHeader;
import org.goplanit.utils.settings.annotations.SettingsSection;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The layout of the sources of some settings: the settings classes filling the sections at their root, which give the
 * header the sources start with
 * <p>
 * For example, with {@code ExampleNetworkSettings} marked {@code @SettingsSection(key = "network", ...)} and
 * {@code @SettingsHeader(key = "planitExampleSettings", version = 1)}, and its constructor taking the country
 * ({@code @SettingsRootValue(key = "country")}), the schema
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
 * subclass of another's.
 * </p>
 *
 * @author markr
 */
public final class SettingsSchema {

  /** the header every source starts with, given by every class of a section */
  private final SettingsHeader header;

  /** class of the settings each section at the root fills, by its key, in the order given */
  private final Map<String, Class<?>> sectionClasses = new LinkedHashMap<>();

  /**
   * Constructor
   *
   * @param sectionClasses of the settings filling the sections at the root, each marked with {@link SettingsSection},
   *     all giving the same {@link SettingsHeader}, no class a subclass of another
   */
  public SettingsSchema(Class<?>... sectionClasses) {
    PlanItRunTimeException.throwIf(sectionClasses.length == 0, "A schema needs at least one section");
    this.header = sectionClasses[0].getAnnotation(SettingsHeader.class);
    for (var sectionClass : sectionClasses) {
      var section = sectionClass.getAnnotation(SettingsSection.class);
      PlanItRunTimeException.throwIf(section == null, "%s is not marked with the section it fills",
          sectionClass.getName());
      PlanItRunTimeException.throwIf(header == null || !header.equals(sectionClass.getAnnotation(SettingsHeader.class)),
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

  /**
   * The header every source starts with
   *
   * @return the header
   */
  public SettingsHeader getHeader() {
    return header;
  }

  /**
   * Whether a key at the root is a section
   *
   * @param key at the root
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
}
