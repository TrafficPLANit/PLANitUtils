package org.goplanit.utils.settings.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Names the settings file of nested settings: on a getter of nested settings, a key whose value is the path of the
 * file the nested settings are filled from, or a list of such paths
 * <p>
 * For example, on the getter of the nested zoning settings of intermodal settings:
 * </p>
 * <pre>
 * &#64;SettingsFileReference(key = "zoning", level = SettingsKeyLevel.STANDARD,
 *     description = "The settings file of the zoning writer")
 * public ExampleZoningWriterSettings getZoningSettings()
 * </pre>
 * <p>
 * a file holding
 * </p>
 * <pre>
 * planitExampleWriterSettings: 1
 * zoning: zoning_writer.yaml
 * </pre>
 * <p>
 * fills the zoning settings the getter gives from {@code zoning_writer.yaml}, a relative path read against the folder
 * of the file naming it, as the zoning settings would be filled from that file on their own. The named file starts
 * with the same header. Nested settings no file names keep their settings.
 * </p>
 * <p>
 * The key also takes a list of files, applied to the nested settings in the order listed, as stacked sources are, so
 * a later file wins: with {@code zoning: [shared.yaml, zoning_writer.yaml]} the keys all the nested settings take
 * alike, such as an output folder, can sit in one file each of them names. Written out, settings name one file for
 * each of their nested settings, next to the file written and named after it followed by the key, such as
 * {@code example_zoning.yaml} for {@code example.yaml}.
 * </p>
 * <p>
 * Settings holding nested settings then have one small file naming one file for each of them, mirroring their
 * classes, and the file of nested settings also serves those settings used on their own. A file naming, directly or
 * through others, the file that names it is refused, so files cannot name each other without end. The getter takes no
 * arguments and gives the nested settings themselves, not a copy, so the named file changes the settings that are
 * used.
 * </p>
 *
 * @author markr
 */
@MarksGetterOfNestedSettings
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SettingsFileReference {

  /**
   * The key's name in a settings file, named after the nested settings, such as {@code zoning} for
   * {@code getZoningSettings}
   *
   * @return the name
   */
  public String key();

  /**
   * How often users need the key. It orders the keys of a file when settings are written out
   *
   * @return the level
   */
  public SettingsKeyLevel level();

  /**
   * What the named file holds, in a sentence, written above the key when settings are written out as a file
   *
   * @return the description
   */
  public String description();
}
