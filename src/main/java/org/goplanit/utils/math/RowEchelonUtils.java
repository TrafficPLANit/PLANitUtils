package org.goplanit.utils.math;

import org.goplanit.utils.arrays.ArrayUtils;

import java.util.Arrays;

/**
 * Gaussian elimination of a matrix to row echelon form, and a vector of its null space taken from that form, on a
 * matrix held row after row in one array the caller owns and reuses.
 * <p>
 * A vector of the null space is a change to the columns' values that leaves every row's total as it is: for the rows
 * 1, 1, 1 and 1, 2, 3, the vector 1, -2, 1. Finding one is the core of methods that move many values at once while
 * keeping a set of totals, such as balanced sampling, where it is repeated millions of times on matrices of a hundred
 * or so rows. That rules out a new object per call. The matrices are also often rank deficient, their rows depending
 * on each other, so the rank must come out right. Libraries fall short on one or the other: ojAlgo has no null space
 * call, and its LU and QR keep each pivot on the diagonal, so they count the rows 1, 1, 1 and 2, 2, 3 as rank 1 where
 * it is 2; its singular value decomposition gets the rank right but is an order of magnitude slower. EJML's reduced
 * row echelon form gets it right too, but is slower, as it also clears the rows above each pivot.
 * </p>
 * <p>
 * The elimination is Gaussian elimination with partial pivoting (Golub and Van Loan 2013, section 3.4) taken to row
 * echelon form: a column with no value above the tolerance in the rows still without a pivot gets none and is skipped,
 * so the number of pivots is the rank. The vector is the special solution of Strang (2016, section 3.2) for the first
 * column without a pivot: 1 there, 0 in the other columns without one, and the pivot columns by back substitution.
 * What differs: the tolerance is absolute and the caller's, so a caller with rows of very different sizes scales each
 * row first, which lets one tolerance suit them all.
 * </p>
 *
 * @see "Golub, G. H. and Van Loan, C. F. (2013). Matrix Computations, 4th edition. Johns Hopkins University Press,
 * Baltimore. Section 3.4, Pivoting"
 * @see "Strang, G. (2016). Introduction to Linear Algebra, 5th edition. Wellesley-Cambridge Press, Wellesley. Section
 * 3.2, the nullspace and its special solutions"
 *
 * @author markr
 */
public class RowEchelonUtils {

  /**
   * Bring a matrix to row echelon form by Gaussian elimination with partial pivoting, in place: per column, the row with
   * the largest value without sign, among the rows below the pivots so far, becomes the next pivot row, and the column
   * is cleared in the rows below it. A column with no value above the tolerance there gets no pivot
   * <p>
   * The columns without a pivot are those whose values can be chosen freely, the pivot columns making up for them, so
   * the matrix has a null space exactly when some column has none. Taking the largest value as the pivot keeps rounding
   * errors small, where a pivot close to 0 would blow them up. For example, the rows 1, 1, 1 and 2, 2, 3 become 2, 2, 3
   * and 0, 0, -0.5: pivots in the first and third columns, none in the second, rank 2.
   * </p>
   *
   * @param matrix the values row after row; its first rows times columns values are used and changed
   * @param rows number of rows
   * @param columns number of columns
   * @param tolerance a value at or below this, without sign, counts as 0 when looking for a pivot
   * @param pivotColumns filled with the column of each pivot row's pivot, at least as long as the smaller of rows and
   *                     columns
   * @return the rank, the number of pivots
   */
  public static int reduceToRowEchelonForm(double[] matrix, int rows, int columns, double tolerance,
      int[] pivotColumns) {
    int rank = 0;
    for (int column = 0; column < columns && rank < rows; ++column) {
      int pivotRow = -1;
      double largest = tolerance;
      for (int row = rank; row < rows; ++row) {
        double value = Math.abs(matrix[row * columns + column]);
        if (value > largest) {
          largest = value;
          pivotRow = row;
        }
      }
      if (pivotRow < 0) {
        continue;
      }
      if (pivotRow != rank) {
        for (int at = column; at < columns; ++at) {
          double swap = matrix[pivotRow * columns + at];
          matrix[pivotRow * columns + at] = matrix[rank * columns + at];
          matrix[rank * columns + at] = swap;
        }
      }
      final int pivotAt = rank * columns;
      final double pivot = matrix[pivotAt + column];
      for (int row = rank + 1; row < rows; ++row) {
        int at = row * columns;
        double factor = matrix[at + column] / pivot;
        if (factor == 0) {
          continue;
        }
        matrix[at + column] = 0;
        ArrayUtils.addTo(matrix, at + column + 1, matrix, pivotAt + column + 1, -factor, columns - column - 1);
      }
      pivotColumns[rank++] = column;
    }
    return rank;
  }

  /**
   * Find a vector of the null space of a matrix in row echelon form, as {@link #reduceToRowEchelonForm} leaves it: 1 in
   * the first column without a pivot, 0 in the other columns without one, and the pivot columns by back substitution
   * <p>
   * Only the columns up to that first free column take part, all later ones staying 0, so a caller that orders its
   * columns decides which of them a vector changes first. For the rows 1, 1, 1 and 1, 2, 3 the third column is the
   * first without a pivot, and the vector is 1, -2, 1.
   * </p>
   *
   * @param rowEchelonMatrix the matrix row after row, in row echelon form
   * @param columns number of columns
   * @param rank of the matrix, the number of pivots
   * @param pivotColumns the column of each pivot row's pivot
   * @param vector filled with the vector in its first columns values, at least columns long
   * @return true when found, false when every column has a pivot, so the null space holds only 0
   */
  public static boolean findNullSpaceVector(double[] rowEchelonMatrix, int columns, int rank, int[] pivotColumns,
      double[] vector) {
    if (rank == columns) {
      return false;
    }
    int free = 0;
    while (free < rank && pivotColumns[free] == free) {
      ++free;
    }
    Arrays.fill(vector, 0, columns, 0.0);
    vector[free] = 1;
    for (int row = rank - 1; row >= 0; --row) {
      final int pivotColumn = pivotColumns[row];
      final int pivotAt = row * columns + pivotColumn;
      double sum = ArrayUtils.dotProduct(rowEchelonMatrix, pivotAt + 1, vector, pivotColumn + 1,
          columns - pivotColumn - 1);
      vector[pivotColumn] = -sum / rowEchelonMatrix[pivotAt];
    }
    return true;
  }
}
