package org.goplanit.utils.yaml;

/**
 * The kinds of problem {@link SimpleYamlReader} refuses YAML text for
 * <p>
 * For example, picking out the keys given twice among the problems found:
 * </p>
 * <pre>
 * problems.stream().filter(problem -&gt; problem.getKind() == YamlProblemKind.DUPLICATE_KEY)
 * </pre>
 * <p>
 * The kind lets code tell problems apart without reading their text, which is written for people.
 * </p>
 *
 * @author markr
 */
public enum YamlProblemKind {

  /** The text is not YAML, such as a list left open, or holds more aliases than the reader allows */
  NOT_YAML,

  /** A key that is not text, such as a list used as a key */
  NON_TEXT_KEY,

  /** A key given twice in one map */
  DUPLICATE_KEY,

  /** A value tagged with a type, such as {@code !!java.io.File}, which is never turned into that type */
  UNSUPPORTED_TAG,

  /** A value that holds itself through an alias, such as the list {@code a: &x [*x]} */
  HOLDS_ITSELF
}
