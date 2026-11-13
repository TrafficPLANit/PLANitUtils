package org.goplanit.utils.math;

import java.util.function.DoubleSupplier;

/**
 * Rounding of values: to a number of decimals, and to multiples of a unit without bias, either each value on its own
 * at random or values in an order from one shared random start.
 * <p>
 * Rounding to the nearest multiple is biased whenever many values lie below half the unit. An OD matrix with 0.4 trips
 * in most of its cells loses all of them when each cell is rounded to whole trips, and a zone expecting 0.4 households
 * in a sample never keeps one. Rounding at random fixes that: a value goes to the multiple below or above it, up with
 * the chance its remainder over the lower multiple is of the unit, so its expected value is the value itself. 0.4 then
 * becomes 1 in four draws out of ten, and 0 in the other six.
 * </p>
 * <p>
 * The two ways differ in how the errors add up. Rounding each value on its own, {@link #roundStochastically}, is
 * simplest and needs no order, but the errors of n values add up like a random walk, about the square root of n units
 * off in total. Rounding values in an order, {@link #roundSystematically}, draws one start for all of them and lets a
 * running total decide, so any run of values next to each other in the order is within one unit of its sum, and so is
 * the whole. That needs an order in which neighbours belong together, and the rounded values of one draw are no
 * longer independent of each other. For places, sorting them along a Hilbert curve gives such an order, see
 * {@link org.goplanit.utils.geo.HilbertCurveUtils#sortAlongCurve(Iterable, java.util.function.Function)}: every run
 * of places next to each other in it covers one compact area, so each such area keeps its total within one unit, such
 * as the households a sample keeps in a few neighbouring zones.
 * </p>
 * <p>
 * Rounding each value on its own is stochastic rounding, as reviewed by Croci et al. (2022), here to multiples of any
 * unit, with the uniform numbers drawn by the caller. Rounding in an order is systematic sampling with unequal
 * probabilities (Madow 1949) applied to values: the values are laid end to end from the start, and each whole unit the
 * running total passes goes to the value it passes in. What differs from that sampling: a value of more than one unit
 * gets several units where a unit is drawn at most once, and the start is given by the caller, to draw at random.
 * </p>
 *
 * @see <a href="https://doi.org/10.1098/rsos.211631">Croci, M., Fasi, M., Higham, N. J., Mary, T. and Mikaitis, M.
 * (2022). Stochastic rounding: implementation, error analysis and applications. Royal Society Open Science 9,
 * 211631</a>
 * @see <a href="https://doi.org/10.1214/aoms/1177729988">Madow, W. G. (1949). On the theory of systematic sampling,
 * II. The Annals of Mathematical Statistics 20(3), 333-354</a>
 *
 * @author markr
 */
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

  /**
   * Round a value at random to the multiple of the unit below or above it, so that its expected value is the value
   * itself. The value goes up when a uniform number times the unit lies below the value's remainder over the multiple
   * below it. One number is drawn for every value, also for one already on a multiple, so the sequence of draws does
   * not depend on the values
   *
   * @param value to round
   * @param unit to round to a multiple of, above 0
   * @param uniform draws a number from 0, included, to 1, excluded, such as {@code random::nextDouble}
   * @return the multiple of the unit below or above the value
   * @throws IllegalArgumentException when the unit is not above 0
   */
  public static double roundStochastically(double value, double unit, DoubleSupplier uniform) {
    if (!(unit > 0)) {
      throw new IllegalArgumentException(String.format("Unit %s to round to a multiple of is not above 0", unit));
    }
    double lower = Math.floor(value / unit) * unit;
    double draw = uniform.getAsDouble() * unit;
    return draw < value - lower ? lower + unit : lower;
  }

  /**
   * Round each of the values at random to the multiple of the unit below or above it, see
   * {@link #roundStochastically(double, double, DoubleSupplier)}, drawing one number per value in their order
   *
   * @param values to round
   * @param unit to round to multiples of, above 0
   * @param uniform draws a number from 0, included, to 1, excluded, such as {@code random::nextDouble}
   * @return the rounded values, in the same order
   * @throws IllegalArgumentException when the unit is not above 0
   */
  public static double[] roundStochastically(double[] values, double unit, DoubleSupplier uniform) {
    var rounded = new double[values.length];
    for (int index = 0; index < values.length; ++index) {
      rounded[index] = roundStochastically(values[index], unit, uniform);
    }
    return rounded;
  }

  /**
   * Round values in an order to multiples of the unit from one shared start, so that any run of values next to each
   * other in the order is within one unit of its sum. A running total of the values, counted in units, begins at the
   * start, and each value is rounded to the whole units the total passes while it is added. Over starts drawn at
   * random, each value is rounded up as often as its part beyond a whole unit says, which keeps its expected value.
   * For example, with a unit of 1 and a start of 0.5, the values 1.3, 0.7 and 0.4 bring the total to 1.8, 2.5 and
   * 2.9. It passes 1 in the first and 2 in the second, so they round to 1, 1 and 0
   *
   * @param values to round, in their order, none below 0
   * @param unit to round to multiples of, above 0
   * @param start of the running total, from 0, included, to 1, excluded, drawn at random so each value keeps its
   *              expected value
   * @return the rounded values, in the same order
   * @throws IllegalArgumentException when a value is below 0, the unit not above 0 or the start outside 0 to 1
   */
  public static double[] roundSystematically(double[] values, double unit, double start) {
    if (!(unit > 0)) {
      throw new IllegalArgumentException(String.format("Unit %s to round to multiples of is not above 0", unit));
    }
    if (!(start >= 0 && start < 1)) {
      throw new IllegalArgumentException(String.format("Start %s of systematic rounding is not from 0 to 1", start));
    }
    var rounded = new double[values.length];
    double total = start;
    long unitsPassed = 0;
    for (int index = 0; index < values.length; ++index) {
      if (values[index] < 0) {
        throw new IllegalArgumentException(
            String.format("Value %s at %d is below 0, it cannot be rounded systematically", values[index], index));
      }
      total += values[index] / unit;
      /* the margin keeps a total that should be whole, such as ten times 0.4, from falling just short of it */
      long unitsPassedNow = (long) Math.floor(total + Precision.EPSILON_9);
      rounded[index] = (unitsPassedNow - unitsPassed) * unit;
      unitsPassed = unitsPassedNow;
    }
    return rounded;
  }

}
