package org.goplanit.utils.math;

public class RoundUtils {

  /**
   * Force a precision on a double by rounding it to a number of decimals
   *
   * @param value to round, e.g., 1.1111111111
   * @param decimals to apply, e.g., 2
   * @return rounded value, e.g. 1.11
   */
  public static double simpleDoubleRound(double value, int decimals ){
    return Math.round(value * Math.pow(10, decimals)) / Math.pow(10, decimals);
  }

}
