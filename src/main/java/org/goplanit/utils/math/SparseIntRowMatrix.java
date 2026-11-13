package org.goplanit.utils.math;

import java.util.Arrays;

/**
 * A matrix of whole numbers that keeps only the values other than 0, filled one row at a time and read back a row at
 * a time, with the total of each column over all rows
 * <p>
 * Many tables are wide while each row has a value in only a few columns: a household counted in a hundred categories
 * falls in a few dozen of them. Keeping only those takes a fraction of a full row each, which for millions of rows is
 * the difference of gigabytes. A row is filled column by column, the values given for one column added up, and
 * finished before the next one is started, so the values of all rows sit one after the other in two flat arrays, one
 * of columns and one of values, with where each row starts in a third, the layout known as compressed sparse rows.
 * Neither the number of rows nor the number of columns is needed up front.
 * </p>
 *
 * @author markr
 */
public final class SparseIntRowMatrix {

  /** entries and rows there is room for at the start */
  private static final int INITIAL_LENGTH = 1024;

  /** the first entry of each row, and one past the last entry of the last row */
  private int[] rowStarts = new int[INITIAL_LENGTH];

  /** number of rows finished */
  private int numberOfRows;

  /** column of each entry */
  private int[] columns = new int[INITIAL_LENGTH];

  /** value of each entry */
  private int[] values = new int[INITIAL_LENGTH];

  /** number of entries of the rows finished */
  private int numberOfEntries;

  /** value of each column of the row being filled, 0 for a column it has no value for yet */
  private int[] fillingValueByColumn = new int[0];

  /** per column, true when the row being filled has been given a value in it, which may add up to 0 */
  private boolean[] fillingHasColumn = new boolean[0];

  /** the columns the row being filled has a value for, in the order first given */
  private int[] fillingColumns = new int[0];

  /** number of columns the row being filled has a value for */
  private int numberOfFillingColumns;

  /** total of each column over the rows finished */
  private long[] totalByColumn = new long[0];

  /**
   * Make room for at least the given number of entries, growing by half so adding a row at a time copies rarely
   *
   * @param count to make room for
   */
  private void makeRoomForEntries(int count) {
    if (count > columns.length) {
      int length = Math.max(count, columns.length + (columns.length >> 1));
      columns = Arrays.copyOf(columns, length);
      values = Arrays.copyOf(values, length);
    }
  }

  /**
   * Make room for the start of at least the given number of rows, growing by half
   *
   * @param count to make room for
   */
  private void makeRoomForRows(int count) {
    if (count > rowStarts.length) {
      rowStarts = Arrays.copyOf(rowStarts, Math.max(count, rowStarts.length + (rowStarts.length >> 1)));
    }
  }

  /**
   * Add to the value of a column of the row being filled
   *
   * @param column from 0
   * @param value to add, any whole number, 0 adding nothing
   * @throws IllegalArgumentException when the column is below 0
   */
  public void add(int column, int value) {
    if (column < 0) {
      throw new IllegalArgumentException(String.format("A column of a sparse matrix must be 0 or more, was %d", column));
    }
    if (value == 0) {
      return;
    }
    if (column >= fillingValueByColumn.length) {
      fillingValueByColumn = Arrays.copyOf(fillingValueByColumn, column + 1);
      fillingHasColumn = Arrays.copyOf(fillingHasColumn, column + 1);
      fillingColumns = Arrays.copyOf(fillingColumns, column + 1);
      totalByColumn = Arrays.copyOf(totalByColumn, column + 1);
    }
    if (!fillingHasColumn[column]) {
      fillingHasColumn[column] = true;
      fillingColumns[numberOfFillingColumns++] = column;
    }
    fillingValueByColumn[column] += value;
  }

  /** Finish the row being filled, keeping its values other than 0, so the next one can be filled */
  public void finishRow() {
    makeRoomForEntries(numberOfEntries + numberOfFillingColumns);
    for (int index = 0; index < numberOfFillingColumns; ++index) {
      int column = fillingColumns[index];
      fillingHasColumn[column] = false;
      if (fillingValueByColumn[column] != 0) {
        columns[numberOfEntries] = column;
        values[numberOfEntries] = fillingValueByColumn[column];
        totalByColumn[column] += fillingValueByColumn[column];
        ++numberOfEntries;
      }
      fillingValueByColumn[column] = 0;
    }
    numberOfFillingColumns = 0;
    makeRoomForRows(numberOfRows + 2);
    rowStarts[++numberOfRows] = numberOfEntries;
  }

  /**
   * Number of rows finished
   *
   * @return the number
   */
  public int getNumberOfRows() {
    return numberOfRows;
  }

  /**
   * Add the values of a row to a dense array, each at the position of its own column
   *
   * @param row finished, from 0
   * @param target to add to, longer than the highest column given a value
   */
  public void addRowTo(int row, double[] target) {
    for (int entry = rowStarts[row]; entry < rowStarts[row + 1]; ++entry) {
      target[columns[entry]] += values[entry];
    }
  }

  /**
   * Add the values of a row to a dense array, each at the position the mapping gives its column, so a caller reads
   * the columns it wants in the order it wants them
   *
   * @param row finished, from 0
   * @param positionOfColumn position in the target of each column given a value, -1 for a column left out
   * @param target to add to
   */
  public void addRowTo(int row, int[] positionOfColumn, double[] target) {
    for (int entry = rowStarts[row]; entry < rowStarts[row + 1]; ++entry) {
      int position = positionOfColumn[columns[entry]];
      if (position >= 0) {
        target[position] += values[entry];
      }
    }
  }

  /**
   * The total of a column over the rows finished
   *
   * @param column from 0
   * @return the total, 0 for a column no row has a value for
   */
  public long getColumnTotal(int column) {
    return column < totalByColumn.length ? totalByColumn[column] : 0;
  }
}
