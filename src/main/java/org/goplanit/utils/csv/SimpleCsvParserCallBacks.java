package org.goplanit.utils.csv;

public interface SimpleCsvParserCallBacks {

  /**
   * Invoked at the start of parsing when we first know how many columns there are in each row
   *
   * @param numColumns to expect each row
   */
  public void numColumnsCallback(int numColumns);

  /**
   * Invoked once for the first row if there is a header
   *
   * @param headerRow content
   */
  public void headerRowCallback(String[] headerRow);

  /**
   * Invoked once each row for its premier entry if it is a column header entry
   *
   * @param headerColEntry of that row (starting with second row if first is a header row)
   */
  public void headerColEntryCallback(String headerColEntry);

  /**
   * Content row, skips first row if first row is a header row, excludes fir column entry of that is a column header
   * entry
   *
   * @param contentRow to parse
   */
  public void contentRowCallback(String[] contentRow);
}
