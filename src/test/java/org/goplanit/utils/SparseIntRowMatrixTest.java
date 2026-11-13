package org.goplanit.utils;

import org.goplanit.utils.math.SparseIntRowMatrix;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests of {@link SparseIntRowMatrix}: rows filled one at a time, values of one column in a row added up, read back a
 * row at a time through a mapping of columns, and the total of each column
 *
 * @author markr
 */
public class SparseIntRowMatrixTest {

  /**
   * Three rows: the first adds 3 and 4 to column 2 and 1 to column 5, the second nothing, the third 2 to column 0 and
   * 0 to column 5, which adds nothing
   *
   * @return the matrix
   */
  private static SparseIntRowMatrix createThreeRows() {
    var matrix = new SparseIntRowMatrix();
    matrix.add(2, 3);
    matrix.add(5, 1);
    matrix.add(2, 4);
    matrix.finishRow();
    matrix.finishRow();
    matrix.add(0, 2);
    matrix.add(5, 0);
    matrix.finishRow();
    return matrix;
  }

  /**
   * Each row reads back its own values at their own columns, a column's values added up; reading a row twice adds it
   * twice
   */
  @Test
  public void testRowsFilledAndRead() {
    var matrix = createThreeRows();
    assertEquals(3, matrix.getNumberOfRows());

    var first = new double[6];
    matrix.addRowTo(0, first);
    assertArrayEquals(new double[]{0, 0, 7, 0, 0, 1}, first);
    matrix.addRowTo(0, first);
    assertArrayEquals(new double[]{0, 0, 14, 0, 0, 2}, first);

    var empty = new double[6];
    matrix.addRowTo(1, empty);
    assertArrayEquals(new double[6], empty);

    var third = new double[6];
    matrix.addRowTo(2, third);
    assertArrayEquals(new double[]{2, 0, 0, 0, 0, 0}, third);
  }

  /** A row is read into the positions the mapping gives its columns, a column mapped to -1 left out */
  @Test
  public void testColumnsMapped() {
    var matrix = createThreeRows();
    var target = new double[2];
    matrix.addRowTo(0, new int[]{-1, -1, 1, -1, -1, -1}, target);
    assertArrayEquals(new double[]{0, 7}, target);
  }

  /** Each column's total over the rows finished, 0 for a column never given a value */
  @Test
  public void testColumnTotals() {
    var matrix = createThreeRows();
    assertEquals(2, matrix.getColumnTotal(0));
    assertEquals(7, matrix.getColumnTotal(2));
    assertEquals(1, matrix.getColumnTotal(5));
    assertEquals(0, matrix.getColumnTotal(4));
    assertEquals(0, matrix.getColumnTotal(99));
  }

  /** Rows and values well beyond the room made at the start: 3,000 rows of two columns each */
  @Test
  public void testManyRows() {
    var matrix = new SparseIntRowMatrix();
    for (int row = 0; row < 3000; ++row) {
      matrix.add(row % 7, 1);
      matrix.add(1500 + row % 3, row);
      matrix.finishRow();
    }
    assertEquals(3000, matrix.getNumberOfRows());
    assertEquals(IntStream.range(0, 3000).filter(row -> row % 7 == 6).count(), matrix.getColumnTotal(6));
    assertEquals(IntStream.range(0, 3000).filter(row -> row % 3 == 2).asLongStream().sum(),
        matrix.getColumnTotal(1502));

    var last = new double[1503];
    matrix.addRowTo(2999, last);
    assertEquals(1, last[2999 % 7]);
    assertEquals(2999, last[1500 + 2999 % 3]);
  }

  /**
   * Values below 0 are kept as any other: 5 and -2 in column 1 read 3, and -7 in column 3 reads -7; 4 and -4 in
   * column 2 add up to 0, which is left out
   */
  @Test
  public void testNegativeValues() {
    var matrix = new SparseIntRowMatrix();
    matrix.add(1, 5);
    matrix.add(2, 4);
    matrix.add(1, -2);
    matrix.add(2, -4);
    matrix.add(3, -7);
    matrix.finishRow();
    matrix.add(2, 1);
    matrix.finishRow();

    var first = new double[4];
    matrix.addRowTo(0, IntStream.range(0, 4).toArray(), first);
    assertArrayEquals(new double[]{0, 3, 0, -7}, first);
    var second = new double[4];
    matrix.addRowTo(1, IntStream.range(0, 4).toArray(), second);
    assertArrayEquals(new double[]{0, 0, 1, 0}, second);
    assertEquals(3, matrix.getColumnTotal(1));
    assertEquals(1, matrix.getColumnTotal(2));
    assertEquals(-7, matrix.getColumnTotal(3));
  }

  /** A column below 0 is refused, leaving the row being filled as it was */
  @Test
  public void testRefusals() {
    var matrix = new SparseIntRowMatrix();
    matrix.add(1, 2);
    assertThrows(IllegalArgumentException.class, () -> matrix.add(-1, 1));
    matrix.finishRow();
    assertEquals(2, matrix.getColumnTotal(1));
  }
}
