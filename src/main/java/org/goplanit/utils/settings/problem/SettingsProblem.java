package org.goplanit.utils.settings.problem;

/**
 * One problem found in a settings file: its kind, the file or text it is in, the key it is about and what is wrong
 * <p>
 * For example, a misspelt key in a file is reported as:
 * </p>
 * <pre>
 * C:\settings\network.yaml: network.nmae: no such key (UNKNOWN_KEY)
 * </pre>
 * <p>
 * The key is given as its path from the root of the file, with dots between sections, so it can be found in the file.
 * A problem with the file as a whole, such as one that cannot be read, has an empty path.
 * </p>
 *
 * @author markr
 */
public final class SettingsProblem {

  /** the kind of problem */
  private final SettingsProblemKind kind;

  /** the file or text the problem is in */
  private final String source;

  /** path of the key from the root, empty for the file as a whole */
  private final String keyPath;

  /** what is wrong, for people */
  private final String detail;

  /**
   * Constructor
   *
   * @param kind the kind of problem
   * @param source description of the file or text the problem is in
   * @param keyPath path of the key from the root, sections separated by dots, empty for the file as a whole
   * @param detail what is wrong, for people
   */
  public SettingsProblem(SettingsProblemKind kind, String source, String keyPath, String detail) {
    this.kind = kind;
    this.source = source;
    this.keyPath = keyPath;
    this.detail = detail;
  }

  /**
   * The kind of problem
   *
   * @return the kind
   */
  public SettingsProblemKind getKind() {
    return kind;
  }

  /**
   * The file or text the problem is in, as {@link org.goplanit.utils.settings.SettingsSource#getDescription()} gives it
   *
   * @return the description of the source
   */
  public String getSource() {
    return source;
  }

  /**
   * The path of the key the problem is about, from the root of the file, sections separated by dots
   *
   * @return the path, empty for the file as a whole
   */
  public String getKeyPath() {
    return keyPath;
  }

  /**
   * What is wrong, written for people
   *
   * @return the detail
   */
  public String getDetail() {
    return detail;
  }

  /**
   * The source, the key path when there is one, the detail and the kind, on one line
   *
   * @return the text
   */
  @Override
  public String toString() {
    return source + ": " + (keyPath.isEmpty() ? "" : keyPath + ": ") + detail + " (" + kind + ")";
  }
}
