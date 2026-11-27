package org.goplanit.utils.csv;

import org.goplanit.utils.misc.StringUtils;

/**
 * Rules of the CSV format, as PLANit's CSV writers follow them
 * <p>
 * A value written to a CSV row must not break the row: a value holding the separator, a double quote or a line break
 * is quoted. That rule is about the format, not about one writer, so it is kept here for any code writing CSV text.
 * </p>
 *
 * @author markr
 */
public final class CsvUtils {

  /** Not instantiable */
  private CsvUtils() {
  }

  /**
   * A string as it is to appear in a CSV row with the given separator: quoted when it holds the separator, and also
   * when it holds a double quote or a line break, with each double quote inside it doubled; otherwise as it is
   *
   * @param text to escape
   * @param separator between the values of a row
   * @return the text as it is to appear in the row
   */
  public static String escapeStringIfContainsSeparator(String text, String separator) {
    return StringUtils.quoteIfContainsAny(text, separator, "\"", "\n", "\r");
  }
}
