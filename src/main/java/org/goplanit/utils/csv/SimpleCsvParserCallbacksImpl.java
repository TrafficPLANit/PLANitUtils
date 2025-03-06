package org.goplanit.utils.csv;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of SimpleCsvParserCallBacks. to be used in Conjunction with
 * SimpleCsvParser
 *
 * @author markr
 */
public class SimpleCsvParserCallbacksImpl implements SimpleCsvParserCallBacks{

  private String[] headerRow;
  private List<String> headerColumn = null;
  private  List<String[]> content = new ArrayList<>(100);

  @Override
  public void numColumnsCallback(int numColumns) {
    // do nothing
  }

  @Override
  public void headerRowCallback(String[] headerRow) {
    this.headerRow = headerRow;
  }

  @Override
  public void headerColEntryCallback(String headerColEntry) {
    if(headerColumn == null){
      headerColumn = new ArrayList<>(100);
    }
    headerColumn.add(headerColEntry);
  }

  @Override
  public void contentRowCallback(String[] contentRow) {
    content.add(contentRow);
  }

  public String[] getParsedHeaderRow() {
    return headerRow;
  }

  public List<String> getParsedHeaderColumn() {
    return headerColumn;
  }

  public List<String[]> getParsedContent() {
    return content;
  }
}
