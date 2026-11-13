package org.goplanit.utils.settings.problem;

/**
 * The kinds of problem settings text can have, whether a file or text given in code
 * <p>
 * For example, picking out the misspelt keys among the problems found:
 * </p>
 * <pre>
 * problems.stream().filter(problem -&gt; problem.getKind() == SettingsProblemKind.UNKNOWN_KEY)
 * </pre>
 * <p>
 * The kind lets code and tests tell problems apart without reading their text, which is written for people. All but
 * {@link #FORMER_KEY_USED} stop the settings from being applied.
 * </p>
 *
 * @author markr
 */
public enum SettingsProblemKind {

  /** The file cannot be read, such as a file that does not exist */
  FILE_NOT_READ,

  /**
   * The text cannot be read as settings, such as text with a list left open or a value tagged with a Java type, or
   * text without keys at its root; the detail says what is wrong
   */
  UNREADABLE_TEXT,

  /** The first line is not the header key of the settings */
  MISSING_HEADER,

  /** The first line is the header of other settings */
  WRONG_HEADER,

  /** The header gives a version of the settings that is not known */
  UNKNOWN_VERSION,

  /** A key the settings do not have, such as a misspelt one */
  UNKNOWN_KEY,

  /** A key given twice in one section */
  DUPLICATE_KEY,

  /** A value that is not of the type its key takes, such as text for a number, or {@code NO} for true or false */
  WRONG_TYPE,

  /** A name that is not one of the values its enum key takes; names match exactly, upper case included */
  UNKNOWN_ENUM_VALUE,

  /** A value both added, under a key ending in {@code +}, and removed, under one ending in {@code -}, in one section */
  ADDED_AND_REMOVED,

  /** A value the settings refuse, such as a number below what a setting allows */
  VALUE_REFUSED,

  /** A warning, not a problem: a key written by a name it had before, applied under its current name */
  FORMER_KEY_USED
}
