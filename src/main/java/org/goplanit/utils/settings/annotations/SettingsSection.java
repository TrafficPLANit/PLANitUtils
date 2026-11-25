package org.goplanit.utils.settings.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Names a section of a settings file: on a getter of a settings class, the section of the nested settings it gives;
 * on a settings class, the section it fills at the top level of a file
 * <p>
 * For example, on the getter of the consolidator settings, which the settings of the intersections hold:
 * </p>
 * <pre>
 * &#64;SettingsSection(key = "intersectionConsolidator", level = SettingsKeyLevel.UNCOMMON,
 *     description = "How the nodes of one junction are found and joined into one intersection")
 * public IntersectionConsolidatorSettings getIntersectionConsolidatorSettings()
 * </pre>
 * <p>
 * A file then writes the consolidator's keys under that name, as they are marked on the consolidator settings, and
 * settings written out as a file with descriptions start the section with its description:
 * </p>
 * <pre>
 * intersectionConsolidator:
 *   # How the nodes of one junction are found and joined into one intersection
 *   maximumJoiningReachMeters: 60
 * </pre>
 * <p>
 * The getter takes no arguments and gives the nested settings themselves, not a copy, so the section's keys change the
 * settings that are used. The description is kept here as well as in the javadoc, because javadoc is not there at run
 * time. The package documentation says how the annotations work together.
 * </p>
 * <p>
 * On a class, for example the settings of a network:
 * </p>
 * <pre>
 * &#64;SettingsSection(key = "network", level = SettingsKeyLevel.STANDARD,
 *     description = "The settings of the network")
 * public class ExampleNetworkSettings extends ExampleReaderSettings
 * </pre>
 * <p>
 * a file holds their keys under {@code network} at its top level, after the header the class gives
 * ({@link SettingsHeader}). Such a class is listed in a {@link org.goplanit.utils.settings.SettingsSchema}, which finds
 * the section from the settings given, so code filling settings from files names no section.
 * </p>
 *
 * @author markr
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface SettingsSection {

  /**
   * The section's name in a settings file, named after its getter, such as {@code intersectionConsolidator} for
   * {@code getIntersectionConsolidatorSettings}, or on a class after what its settings are of, such as {@code network}
   *
   * @return the name
   */
  public String key();

  /**
   * How often users need the section. It orders the keys and sections of the section it is in when settings are
   * written out as a file
   *
   * @return the level
   */
  public SettingsKeyLevel level();

  /**
   * What the section holds, in a sentence, written at its start when settings are written out as a file
   *
   * @return the description
   */
  public String description();
}
