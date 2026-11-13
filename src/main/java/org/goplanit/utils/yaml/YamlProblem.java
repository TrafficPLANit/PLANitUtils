package org.goplanit.utils.yaml;

/**
 * One problem {@link SimpleYamlReader} found in YAML text: its kind, the key it is about and what is wrong
 * <p>
 * For example, a key given twice in the second entry of a list is reported as:
 * </p>
 * <pre>
 * groups[1].name: given twice on line 7 (DUPLICATE_KEY)
 * </pre>
 * <p>
 * The key is given as its path from the root, with a dot between nested maps and the index of a list entry in
 * brackets, so it can be found in the text. A problem with the text as a whole, such as text that is not YAML, has an
 * empty path.
 * </p>
 *
 * @author markr
 */
public final class YamlProblem {

  /** the kind of problem */
  private final YamlProblemKind kind;

  /** path of the key from the root, empty for the text as a whole */
  private final String keyPath;

  /** what is wrong, for people */
  private final String detail;

  /**
   * Constructor
   *
   * @param kind the kind of problem
   * @param keyPath path of the key from the root, empty for the text as a whole
   * @param detail what is wrong, for people
   */
  public YamlProblem(YamlProblemKind kind, String keyPath, String detail) {
    this.kind = kind;
    this.keyPath = keyPath;
    this.detail = detail;
  }

  /**
   * The kind of problem
   *
   * @return the kind
   */
  public YamlProblemKind getKind() {
    return kind;
  }

  /**
   * The path of the key the problem is about, from the root, such as {@code lanes.forward} or {@code modes[1]}
   *
   * @return the path, empty for the text as a whole
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
   * The key path when there is one, the detail and the kind, on one line
   *
   * @return the text
   */
  @Override
  public String toString() {
    return (keyPath.isEmpty() ? "" : keyPath + ": ") + detail + " (" + kind + ")";
  }
}
