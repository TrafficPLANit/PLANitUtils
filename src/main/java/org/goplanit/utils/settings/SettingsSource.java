package org.goplanit.utils.settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Where settings text comes from: a file, or text given in code
 * <p>
 * For example, a shipped file followed by a line a program adds:
 * </p>
 * <pre>
 * var sources = List.of(SettingsSource.ofFile(Path.of("settings/network.yaml")),
 *     SettingsSource.ofText("planitExampleSettings: 1\ncountry: Australia"));
 * </pre>
 * <p>
 * A relative path written in a file is read against the folder of that file, so a file and the data it names can be
 * moved together. Text has no folder, so its relative paths are read against the working folder. Text lets a program
 * build a variant of its settings without writing a temporary file.
 * </p>
 *
 * @author markr
 */
public final class SettingsSource {

  /** the file, null for text */
  private final Path file;

  /** the text, null for a file */
  private final String text;

  /**
   * Constructor
   *
   * @param file the file, null for text
   * @param text the text, null for a file
   */
  private SettingsSource(Path file, String text) {
    this.file = file;
    this.text = text;
  }

  /**
   * A settings file
   *
   * @param file the file
   * @return the source
   */
  public static SettingsSource ofFile(Path file) {
    return new SettingsSource(file.toAbsolutePath().normalize(), null);
  }

  /**
   * Text given in code, written as a settings file would be
   *
   * @param text the text
   * @return the source
   */
  public static SettingsSource ofText(String text) {
    return new SettingsSource(null, text);
  }

  /**
   * The text of the source
   *
   * @return the text of the file, read as UTF-8, or the text given
   * @throws IOException when the file cannot be read
   */
  public String readText() throws IOException {
    return file != null ? Files.readString(file) : text;
  }

  /**
   * The folder a relative path written in the source is read against
   *
   * @return the folder of the file, or the working folder for text
   */
  public Path getBaseFolder() {
    return file != null ? file.getParent() : Path.of("").toAbsolutePath();
  }

  /**
   * What the source is, for problems found in it
   *
   * @return the path of the file, or "text given in code"
   */
  public String getDescription() {
    return file != null ? file.toString() : "text given in code";
  }

  /**
   * Same as {@link #getDescription()}
   *
   * @return the description
   */
  @Override
  public String toString() {
    return getDescription();
  }
}
