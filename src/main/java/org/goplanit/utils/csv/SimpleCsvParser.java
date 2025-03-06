package org.goplanit.utils.csv;

import org.goplanit.utils.misc.FileUtils;
import org.goplanit.utils.misc.Triple;

import java.util.List;
import java.util.Scanner;

import static org.goplanit.utils.misc.FileUtils.convertFileStringToFile;

/**
 * Simple CSV parser class that takes care of parsing the file and can be use din three ways:
 * <ul>
 *   <li>directly parse contents to memory via parse()</li>
 *   <li>extend class and arse with hook methods to override via parseWithHooks()</li>
 *   <li>use class directly but provide consumers for callbacks via parseWithCallBacks()</li>
 * </ul>
 */
public class SimpleCsvParser{

  private String filePath;

  private String separator;

  private boolean hasHeaderRow;

  private boolean hasHeaderColumn;

  /**
   * Actual parser using provided callback. In case header col or row are not present, the callback is still invoked
   * for completeness albeit with null parameter.
   *
   * @param scanner to extract content from file
   * @param callbacks to apply
   */
  private void parseInternalWithCallBacks(Scanner scanner, SimpleCsvParserCallBacks callbacks) {
    boolean firstRow = true;
    while (scanner.hasNextLine()) {
      String currLine = scanner.nextLine();
      String[] splitCurrLine = currLine.split(separator);

      boolean skipRowForContent = false;
      if (firstRow) {
        callbacks.numColumnsCallback(splitCurrLine.length);
        callbacks.headerRowCallback(hasHeaderRow() ? splitCurrLine : null);
        skipRowForContent = hasHeaderRow();
        firstRow = false;
      }

      if (!skipRowForContent) {
        String[] withoutColHeader = splitCurrLine;
        if (hasHeaderColumn()) {
          String headerColEntry = splitCurrLine[0];
          callbacks.headerColEntryCallback(headerColEntry);
          withoutColHeader = new String[splitCurrLine.length - 1];
          System.arraycopy(splitCurrLine, 1, withoutColHeader, 0, withoutColHeader.length);
        }else {
          callbacks.headerColEntryCallback(null);
        }
        callbacks.contentRowCallback(withoutColHeader);
      }
    }
  }

  /**
   * Constructor
   * @param filePath to use
   * @param separator to use
   * @param hasHeaderRow to use
   * @param hasHeaderColumn to use
   */
  private SimpleCsvParser(String filePath, String separator, boolean hasHeaderRow, boolean hasHeaderColumn){
    this.filePath = filePath;
    this.separator = separator;
    this.hasHeaderRow = hasHeaderRow;
    this.hasHeaderColumn = hasHeaderColumn;
  }

  /**
   * Parse contents in raw memory form.
   *
   * @param scanner to use
   * @return triple containing [header column entries, header row entries, value content: values as string array per row]
   */
  protected Triple<List<String>, String[], List<String[]>> parseToMemory(Scanner scanner) {
    var callBacks = new SimpleCsvParserCallbacksImpl();
    parseWithCallbacks(callBacks);
    return Triple.of(callBacks.getParsedHeaderColumn(), callBacks.getParsedHeaderRow(), callBacks.getParsedContent());
  }

  /**
   * Use this method when custom parsing of various aspects is required. Use callback interface implementation
   * to do so
   *
   * @param scanner to use
   * @param callbacks to use
   */
  protected void parseWithCallbacks(Scanner scanner, SimpleCsvParserCallBacks callbacks){
    parseInternalWithCallBacks(scanner, callbacks);
  }

  /**
   * Default parser with "," as separator and assumed header row but no header column
   *
   * @param filePath to use
   * @return created parser
   */
  public static SimpleCsvParser createDefault(String filePath){
    return create(filePath, ",", true, false);
  }

  /**
   * Parser with separator provided but no headers
   *
   * @param filePath to use
   * @param separator to use
   * @return created parser
   */
  public static SimpleCsvParser createWithoutHeaders(String filePath, String separator){
    return create(filePath, separator, false, false);
  }

  /**
   * Parser with separator provided and a header row but no header column
   *
   * @param filePath to use
   * @param separator to use
   * @return created parser
   */
  public static SimpleCsvParser createWithHeaderRow(String filePath, String separator){
    return create(filePath, separator, true, false);
  }

  /**
   * Parser with separator provided and a header row but no header column
   *
   * @param filePath to use
   * @param separator to use
   * @param hasHeaderRow flag to indicate if it has a header row
   * @param hasHeaderColumn flag to indicate if it has a header col
   * @return created parser
   */
  public static SimpleCsvParser create(
          String filePath, String separator, boolean hasHeaderRow, boolean hasHeaderColumn){
    return new SimpleCsvParser(filePath, separator, hasHeaderRow, hasHeaderColumn);
  }

  /**
   * Parse the CSV. If no headers exist then the corresponding entry in the result will be null, e.g., first and or
   * second component of triple returned.
   *
   * @return triple containing
   * [header column entries, header row entries, value content: values as string array per row]
   */
  public Triple<List<String>, String[], List<String[]>> parse(){
    return FileUtils.wrapFileScannerWithResult(
            convertFileStringToFile(filePath), this::parseToMemory);
  }

  /**
   * Use this method when custom parsing of various aspects is required. Use callback interface implementation
   * to do so
   *
   * @param callbacks to use
   *
   */
  public void parseWithCallbacks(SimpleCsvParserCallBacks callbacks){
    FileUtils.wrapFileScanner(
            convertFileStringToFile(filePath),
            scanner -> parseWithCallbacks(scanner, callbacks));
  }

  // Getter/Setters

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public String getSeparator() {
    return separator;
  }

  public void setSeparator(String separator) {
    this.separator = separator;
  }

  public boolean hasHeaderRow() {
    return hasHeaderRow;
  }

  public void setHasHeaderRow(boolean hasHeaderRow) {
    this.hasHeaderRow = hasHeaderRow;
  }

  public boolean hasHeaderColumn() {
    return hasHeaderColumn;
  }

  public void setHasHeaderColumn(boolean hasHeaderColumn) {
    this.hasHeaderColumn = hasHeaderColumn;
  }
}
