package org.goplanit.utils.problem;

/**
 * One problem found while checking something: its kind, where it was found, what there it is about, and what is wrong
 * <p>
 * Whoever checks names the kinds of problem it can find in an enum of its own, and reports each problem it finds as one
 * of these, so problems of any origin are collected, counted and logged alike: a YAML text or a settings file being
 * read, a pricing scheme or cap being registered, a change to the network leaving a scheme with nothing to charge on.
 * A kind may say more, such as what becomes of what was checked: a pricing problem's kind says whether the scheme is
 * refused or registered with a part dropped. For example, a misspelled key in a settings file, and a section of a
 * pricing scheme holding link segments the scheme does not charge on, are reported as:
 * </p>
 * <pre>
 * C:\settings\network.yaml: network.name: No such key (UNKNOWN_KEY)
 * section 3: link segments 12, 14 (SECTION_LINK_SEGMENT_NOT_PRICED)
 * </pre>
 * <p>
 * The source is where the problem was found, such as a file or a text, empty when there is only one place to look, as
 * for YAML text read on its own or the scheme being registered. The subject is what in the source the problem is
 * about: the path of a key from the top level, with a dot between nested maps and the index of a list entry in
 * brackets, or a part of a scheme, such as a section, a time band or one of its modes; empty for the source as a whole,
 * such as a file that cannot be read. The detail says what is wrong, such as the link segments dropped.
 * </p>
 *
 * @author markr
 * @param <K> type of the kinds of problem
 */
public final class Problem<K extends Enum<K>> {

  /** the kind of problem */
  private final K kind;

  /** the file or text the problem is in, empty when not given */
  private final String source;

  /** what in the source the problem is about, empty for the source as a whole */
  private final String subject;

  /** what is wrong, for people */
  private final String detail;

  /**
   * Constructor
   *
   * @param kind the kind of problem
   * @param source the file or text the problem is in, empty when not given
   * @param subject what in the source the problem is about, such as a key path or a scheme id, empty for the source as
   *     a whole
   * @param detail what is wrong, for people
   */
  public Problem(K kind, String source, String subject, String detail) {
    this.kind = kind;
    this.source = source;
    this.subject = subject;
    this.detail = detail;
  }

  /**
   * The kind of problem
   *
   * @return the kind
   */
  public K getKind() {
    return kind;
  }

  /**
   * The file or text the problem is in, as {@link org.goplanit.utils.settings.SettingsSource#getDescription()} gives it
   * for a settings file
   *
   * @return the source, empty when not given
   */
  public String getSource() {
    return source;
  }

  /**
   * What in the source the problem is about, such as the path of a key from the top level, {@code lanes.forward} or
   * {@code modes[1]}, or the id of a scheme
   *
   * @return the subject, empty for the source as a whole
   */
  public String getSubject() {
    return subject;
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
   * The source and the subject when given, the detail and the kind, on one line
   *
   * @return the text
   */
  @Override
  public String toString() {
    return (source.isEmpty() ? "" : source + ": ") + (subject.isEmpty() ? "" : subject + ": ") + detail + " ("
        + kind + ")";
  }
}
