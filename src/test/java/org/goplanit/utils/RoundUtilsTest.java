package org.goplanit.utils;

import org.goplanit.utils.math.RoundUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests of {@link RoundUtils}: rounding each value on its own at random, and rounding values in an order from one
 * shared random start, both keeping each value's expected value
 *
 * @author markr
 */
public class RoundUtilsTest {

  /** A value already on a multiple of the unit keeps its value, whatever is drawn */
  @Test
  public void testStochasticValueOnAMultipleUnchanged() {
    assertEquals(3.0, RoundUtils.roundStochastically(3.0, 1.0, () -> 0.0));
    assertEquals(3.0, RoundUtils.roundStochastically(3.0, 1.0, () -> 0.999));
    assertEquals(4.0, RoundUtils.roundStochastically(4.0, 2.0, () -> 0.5));
  }

  /**
   * A value goes up to the next multiple when the draw times the unit lies below its remainder over the multiple
   * under it, and down otherwise: 2.25 with a unit of 1 goes up for a draw of 0.2 and down for a draw of 0.3
   */
  @Test
  public void testStochasticDrawDecides() {
    assertEquals(3.0, RoundUtils.roundStochastically(2.25, 1.0, () -> 0.2));
    assertEquals(2.0, RoundUtils.roundStochastically(2.25, 1.0, () -> 0.3));
    assertEquals(2.0, RoundUtils.roundStochastically(0.4, 2.0, () -> 0.1));
    assertEquals(0.0, RoundUtils.roundStochastically(0.4, 2.0, () -> 0.3));
  }

  /**
   * Over 100,000 draws, 0.25 rounds to 0 or 1 and to 0.25 on average, and 0.4 with a unit of 2 to 0 or 2 and to 0.4
   * on average, each to within four times the spread of the mean
   */
  @Test
  public void testStochasticKeepsTheExpectedValue() {
    var random = new SplittableRandom(42);
    final int draws = 100000;
    double sumQuarter = 0;
    double sumWithUnitTwo = 0;
    for (int draw = 0; draw < draws; ++draw) {
      double quarter = RoundUtils.roundStochastically(0.25, 1.0, random::nextDouble);
      double withUnitTwo = RoundUtils.roundStochastically(0.4, 2.0, random::nextDouble);
      assertTrue(quarter == 0.0 || quarter == 1.0);
      assertTrue(withUnitTwo == 0.0 || withUnitTwo == 2.0);
      sumQuarter += quarter;
      sumWithUnitTwo += withUnitTwo;
    }
    assertEquals(0.25, sumQuarter / draws, 4 * Math.sqrt(0.25 * 0.75 / draws));
    assertEquals(0.4, sumWithUnitTwo / draws, 4 * 2 * Math.sqrt(0.2 * 0.8 / draws));
  }

  /** Rounding an array gives what rounding its values one by one gives from the same random sequence */
  @Test
  public void testStochasticArrayRoundsLikeEachValue() {
    double[] values = new SplittableRandom(7).doubles(100, 0, 5).toArray();
    var one = new SplittableRandom(42);
    double[] oneByOne = Arrays.stream(values).map(value -> RoundUtils.roundStochastically(value, 2.0, one::nextDouble))
        .toArray();
    var all = new SplittableRandom(42);
    assertArrayEquals(oneByOne, RoundUtils.roundStochastically(values, 2.0, all::nextDouble));
  }

  /** A unit of 0 or below is refused */
  @Test
  public void testStochasticUnitOfZeroRefused() {
    assertThrows(IllegalArgumentException.class, () -> RoundUtils.roundStochastically(1.5, 0.0, () -> 0.5));
  }

  /**
   * From a start of 0.5 the values 1.3, 0.7 and 0.4 bring the running total to 1.8, 2.5 and 2.9. It passes 1 in the
   * first value and 2 in the second, so they round to 1, 1 and 0
   */
  @Test
  public void testSystematicRunningTotalFromStart() {
    assertArrayEquals(new double[]{1, 1, 0}, RoundUtils.roundSystematically(new double[]{1.3, 0.7, 0.4}, 1.0, 0.5));
  }

  /** Ten values of 0.4 round to 0 or 1 each and to exactly 4 in all, from any start */
  @Test
  public void testSystematicSmallValuesAddUpToTheirSum() {
    double[] values = new double[10];
    Arrays.fill(values, 0.4);
    for (int step = 0; step < 1000; ++step) {
      double[] rounded = RoundUtils.roundSystematically(values, 1.0, step / 1000.0);
      assertEquals(4.0, Arrays.stream(rounded).sum());
      assertTrue(Arrays.stream(rounded).allMatch(count -> count == 0.0 || count == 1.0));
    }
  }

  /**
   * Over 10,000 starts spread evenly between 0 and 1, a value of 3.7 is rounded up for exactly 7,000 of them, as the
   * part after its decimal point says
   */
  @Test
  public void testSystematicRoundedUpAsOftenAsItsFractionSays() {
    int roundedUp = 0;
    for (int step = 0; step < 10000; ++step) {
      double[] rounded = RoundUtils.roundSystematically(new double[]{3.7}, 1.0, step / 10000.0);
      assertTrue(rounded[0] == 3.0 || rounded[0] == 4.0);
      roundedUp += (int) (rounded[0] - 3);
    }
    assertEquals(7000, roundedUp);
  }

  /**
   * Any run of values next to each other rounds to within one unit of its sum, for values and starts drawn at random
   * and a unit of 2
   */
  @Test
  public void testSystematicAnyRunWithinOneUnitOfItsSum() {
    var random = new SplittableRandom(42);
    final double unit = 2.0;
    for (int draw = 0; draw < 20; ++draw) {
      double[] values = random.doubles(50, 0, 10).toArray();
      double[] rounded = RoundUtils.roundSystematically(values, unit, random.nextDouble());
      for (int first = 0; first < values.length; ++first) {
        double sum = 0;
        double roundedSum = 0;
        for (int last = first; last < values.length; ++last) {
          sum += values[last];
          roundedSum += rounded[last];
          assertEquals(0.0, Math.IEEEremainder(rounded[last], unit), 1e-9);
          assertTrue(Math.abs(roundedSum - sum) < unit + 1e-9);
        }
      }
    }
  }

  /** A start outside 0 to 1, 1 itself included, a negative value and a unit of 0 are refused */
  @Test
  public void testSystematicStartOutOfRangeNegativeValueAndUnitOfZeroRefused() {
    assertThrows(IllegalArgumentException.class, () -> RoundUtils.roundSystematically(new double[]{1.0}, 1.0, 1.0));
    assertThrows(IllegalArgumentException.class, () -> RoundUtils.roundSystematically(new double[]{1.0}, 1.0, -0.1));
    assertThrows(IllegalArgumentException.class, () -> RoundUtils.roundSystematically(new double[]{-1.0}, 1.0, 0.5));
    assertThrows(IllegalArgumentException.class, () -> RoundUtils.roundSystematically(new double[]{1.0}, 0.0, 0.5));
  }
}
