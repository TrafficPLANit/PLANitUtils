package org.goplanit.utils.settings;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Settings that have settings files: they give the layout of those files, and can write themselves to one
 * <p>
 * For example, settings whose class implements it, with {@code getSettingsSchema()} giving
 * {@code new SettingsSchema(ExampleNetworkSettings.class)}, write every option at its default with its description
 * with
 * </p>
 * <pre>
 * new ExampleNetworkSettings().writeSettingsFile(Path.of("defaults.yaml"), SettingsFileContent.ALL_KEYS, true);
 * </pre>
 * <p>
 * It exists so that settings read from settings files can also be written to one, to see every option with its value
 * and description, to start a file from, or to keep the choices made in a run, by code that does not know their
 * layout. It is separate from what the settings are for, such as the settings of a converter, so settings without
 * settings files do not have it, and writing them out cannot be asked for.
 * </p>
 *
 * @author markr
 */
public interface YamlWriteSettingsSupport {

  /**
   * The layout of the settings files of these settings, as {@link SettingsYamlLoader} reads them
   *
   * @return the schema
   */
  public abstract SettingsSchema getSettingsSchema();

  /**
   * Write these settings to a settings file, in the layout {@link #getSettingsSchema()} gives, and each part whose
   * settings file a key names to a file of its own next to it
   *
   * @param file to write
   * @param content which keys to write
   * @param withDescriptions true to write the description of each section and key, and a heading per level, as
   *     comments; false for no comment at all
   * @throws IOException when the file cannot be written
   */
  public default void writeSettingsFile(Path file, SettingsFileContentType content, boolean withDescriptions)
      throws IOException {
    new SettingsYamlWriter(getSettingsSchema(), this, content, withDescriptions).write(file);
  }
}
