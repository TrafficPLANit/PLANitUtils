package org.goplanit.utils;

import org.goplanit.utils.arrays.ArrayUtils;
import org.goplanit.utils.math.RowEchelonUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests of {@link RowEchelonUtils}: the rank found by Gaussian elimination to row echelon form, also when a column has
 * no pivot, and a vector of the null space taken from that form
 *
 * @author markr
 */
public class RowEchelonUtilsTest {

  /** a value at or below this counts as 0 when looking for a pivot */
  private static final double TOLERANCE = 1e-9;

  /**
   * Largest value without sign of the matrix times the vector, which is 0 for a vector of its null space
   *
   * @param matrix row after row
   * @param rows of the matrix
   * @param columns of the matrix
   * @param vector to multiply with
   * @return largest value of the product
   */
  private static double largestOfProduct(double[] matrix, int rows, int columns, double[] vector) {
    double largest = 0;
    for (int row = 0; row < rows; ++row) {
      largest = Math.max(largest, Math.abs(ArrayUtils.dotProduct(matrix, row * columns, vector, 0, columns)));
    }
    return largest;
  }

  /** A count row and a value row, 1, 1, 1 and 1, 2, 3, have rank 2, and the vector 1, -2, 1 keeps both totals */
  @Test
  public void testCountAndValueRows() {
    double[] matrix = {1, 1, 1, 1, 2, 3};
    double[] original = matrix.clone();
    int[] pivotColumns = new int[3];
    int rank = RowEchelonUtils.reduceToRowEchelonForm(matrix, 2, 3, TOLERANCE, pivotColumns);
    assertEquals(2, rank);
    assertArrayEquals(new int[] {0, 1}, Arrays.copyOf(pivotColumns, rank));

    double[] vector = new double[3];
    assertTrue(RowEchelonUtils.findNullSpaceVector(matrix, 3, rank, pivotColumns, vector));
    assertArrayEquals(new double[] {1, -2, 1}, vector, 1e-12);
    assertEquals(0, largestOfProduct(original, 2, 3, vector), 1e-12);
  }

  /**
   * For the rows 1, 1, 1 and 2, 2, 3 the second column has no pivot, so the rank is 2 with pivots in the first and third
   * columns, and the vector is -1, 1, 0. Elimination that keeps each pivot on the diagonal would count rank 1 here
   */
  @Test
  public void testColumnWithoutPivotSkipped() {
    double[] matrix = {1, 1, 1, 2, 2, 3};
    double[] original = matrix.clone();
    int[] pivotColumns = new int[3];
    int rank = RowEchelonUtils.reduceToRowEchelonForm(matrix, 2, 3, TOLERANCE, pivotColumns);
    assertEquals(2, rank);
    assertArrayEquals(new int[] {0, 2}, Arrays.copyOf(pivotColumns, rank));

    double[] vector = new double[3];
    assertTrue(RowEchelonUtils.findNullSpaceVector(matrix, 3, rank, pivotColumns, vector));
    assertArrayEquals(new double[] {-1, 1, 0}, vector, 1e-12);
    assertEquals(0, largestOfProduct(original, 2, 3, vector), 1e-12);
  }

  /** A row that is the sum of two others adds no pivot: rank 2 of 3 rows, and a vector that keeps all three */
  @Test
  public void testDependentRowAddsNoPivot() {
    double[] matrix = {1, 0, 1, 0, 0, 1, 1, 1, 1, 1, 2, 1};
    double[] original = matrix.clone();
    int[] pivotColumns = new int[4];
    int rank = RowEchelonUtils.reduceToRowEchelonForm(matrix, 3, 4, TOLERANCE, pivotColumns);
    assertEquals(2, rank);

    double[] vector = new double[4];
    assertTrue(RowEchelonUtils.findNullSpaceVector(matrix, 4, rank, pivotColumns, vector));
    assertArrayEquals(new double[] {-1, -1, 1, 0}, vector, 1e-12);
    assertEquals(0, largestOfProduct(original, 3, 4, vector), 1e-12);
  }

  /** A square matrix of full rank has a pivot in every column, so its null space holds no vector but 0 */
  @Test
  public void testFullRankHasNoNullSpaceVector() {
    double[] matrix = {1, 2, 3, 4};
    int[] pivotColumns = new int[2];
    int rank = RowEchelonUtils.reduceToRowEchelonForm(matrix, 2, 2, TOLERANCE, pivotColumns);
    assertEquals(2, rank);
    assertFalse(RowEchelonUtils.findNullSpaceVector(matrix, 2, rank, pivotColumns, new double[2]));
  }

  /**
   * A value within the tolerance of 0 counts as 0: the rows 1, 1 and 1, 1 + 10<sup>-12</sup> have rank 1 at a tolerance
   * of 10<sup>-9</sup>, with the vector -1, 1, and rank 2 at 10<sup>-15</sup>
   */
  @Test
  public void testToleranceDecidesRank() {
    double[] values = {1, 1, 1, 1 + 1e-12};
    int[] pivotColumns = new int[2];

    double[] matrix = values.clone();
    int rank = RowEchelonUtils.reduceToRowEchelonForm(matrix, 2, 2, 1e-9, pivotColumns);
    assertEquals(1, rank);
    double[] vector = new double[2];
    assertTrue(RowEchelonUtils.findNullSpaceVector(matrix, 2, rank, pivotColumns, vector));
    assertArrayEquals(new double[] {-1, 1}, vector, 1e-12);

    assertEquals(2, RowEchelonUtils.reduceToRowEchelonForm(values.clone(), 2, 2, 1e-15, pivotColumns));
  }

  /** The vector is 0 in every column after its first column without a pivot: for the single row 1, 1, 1 it is -1, 1, 0 */
  @Test
  public void testOnlyColumnsUpToTheFirstFreeOne() {
    double[] matrix = {1, 1, 1};
    int[] pivotColumns = new int[3];
    int rank = RowEchelonUtils.reduceToRowEchelonForm(matrix, 1, 3, TOLERANCE, pivotColumns);
    assertEquals(1, rank);

    double[] vector = new double[3];
    assertTrue(RowEchelonUtils.findNullSpaceVector(matrix, 3, rank, pivotColumns, vector));
    assertArrayEquals(new double[] {-1, 1, 0}, vector, 1e-12);
  }

  /**
   * Only the first rows times columns values of the array are read and changed, so one array kept from call to call
   * serves matrices of any size up to its length
   */
  @Test
  public void testMatrixAtTheStartOfALargerArray() {
    double[] matrix = new double[20];
    Arrays.fill(matrix, 99);
    System.arraycopy(new double[] {1, 1, 1, 1, 2, 3}, 0, matrix, 0, 6);
    int[] pivotColumns = new int[10];
    int rank = RowEchelonUtils.reduceToRowEchelonForm(matrix, 2, 3, TOLERANCE, pivotColumns);
    assertEquals(2, rank);
    for (int at = 6; at < matrix.length; ++at) {
      assertEquals(99, matrix[at]);
    }

    double[] vector = new double[10];
    Arrays.fill(vector, 99);
    assertTrue(RowEchelonUtils.findNullSpaceVector(matrix, 3, rank, pivotColumns, vector));
    assertArrayEquals(new double[] {1, -2, 1}, Arrays.copyOf(vector, 3), 1e-12);
    for (int at = 3; at < vector.length; ++at) {
      assertEquals(99, vector[at]);
    }
  }
}
