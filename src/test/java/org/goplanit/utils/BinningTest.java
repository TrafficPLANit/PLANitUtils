package org.goplanit.utils;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.misc.binning.Bin;
import org.goplanit.utils.misc.binning.BinBuilder;
import org.goplanit.utils.misc.binning.BinnedCount;
import org.goplanit.utils.misc.binning.BinningConfiguration;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the generic binning support
 *
 * @author markr
 */
public class BinningTest {

  @Test
  public void binBoundsAreHonouredAsGiven() {
    var inclusive = Bin.of(2, 5);
    assertFalse(inclusive.contains(1));
    assertTrue(inclusive.contains(2));
    assertTrue(inclusive.contains(5));
    assertFalse(inclusive.contains(6));

    var halfOpen = Bin.of(2, 5, true, false, null);
    assertTrue(halfOpen.contains(2));
    assertTrue(halfOpen.contains(4));
    assertFalse(halfOpen.contains(5));

    var lowerExclusive = Bin.of(2.0, 5.0, false, true, null);
    assertFalse(lowerExclusive.contains(2.0));
    assertTrue(lowerExclusive.contains(2.0001));
    assertTrue(lowerExclusive.contains(5.0));
  }

  @Test
  public void singleValueBinIsAllowedButAnEmptyOneIsNot() {
    var single = Bin.of(1, 1);
    assertTrue(single.contains(1));
    assertFalse(single.contains(2));
    assertEquals("1", single.getLabel());

    /* equal bounds with either end exclusive can never contain anything, so it is rejected rather than silently
     * never matching */
    assertThrows(PlanItRunTimeException.class, () -> Bin.of(1, 1, true, false, null));
    assertThrows(PlanItRunTimeException.class, () -> Bin.of(5, 2, true, true, null));
  }

  @Test
  public void labelIsInferredWhenNoneIsGiven() {
    assertEquals("[2,5]", Bin.of(2, 5).getLabel());
    assertEquals("[2,5)", Bin.of(2, 5, true, false, null).getLabel());
    assertEquals("(2,5]", Bin.of(2, 5, false, true, null).getLabel());
    assertEquals("(2,5)", Bin.of(2, 5, false, false, null).getLabel());
    assertEquals("2-5", Bin.of(2, 5, "2-5").getLabel());
    assertFalse(Bin.of(2, 5).hasLabel());
    assertTrue(Bin.of(2, 5, "2-5").hasLabel());
    assertEquals("2-5", Bin.of(2, 5).withLabel("2-5").getLabel());
  }

  @Test
  public void contiguityRequiresExactlyOneEndToClaimTheSharedBound() {
    /* meeting at 5 with only the second claiming it */
    assertTrue(Bin.of(2, 5, true, false, null).isContiguousWith(Bin.of(5, 9, true, false, null)));
    /* meeting at 5 with both claiming it, so 5 would be counted twice */
    assertFalse(Bin.of(2, 5, true, true, null).isContiguousWith(Bin.of(5, 9, true, false, null)));
    /* meeting at 5 with neither claiming it, so 5 would be lost */
    assertFalse(Bin.of(2, 5, true, false, null).isContiguousWith(Bin.of(5, 9, false, true, null)));
    /* not meeting at all */
    assertFalse(Bin.of(2, 5, true, false, null).isContiguousWith(Bin.of(6, 9, true, false, null)));
  }

  @Test
  public void configurationRejectsGapsOverlapsAndDisorder() {
    /* a gap between 5 and 6 */
    assertThrows(PlanItRunTimeException.class, () -> BinningConfiguration.of(
        Bin.of(1, 5, true, false, null), Bin.of(6, 9, true, false, null)));
    /* an overlap, both claiming 5 */
    assertThrows(PlanItRunTimeException.class, () -> BinningConfiguration.of(
        Bin.of(1, 5), Bin.of(5, 9)));
    /* out of order */
    assertThrows(PlanItRunTimeException.class, () -> BinningConfiguration.of(
        Bin.of(5, 9, true, false, null), Bin.of(1, 5, true, false, null)));
    /* nothing at all */
    assertThrows(PlanItRunTimeException.class, () -> BinningConfiguration.of(List.<Bin<Integer>>of()));
  }

  @Test
  public void compositeIntegerConfigurationBinsEveryValueExactlyOnce() {
    /* the arrangement subnetwork sizes are reported by */
    var configuration = BinBuilder.ofInclusiveIntegerUpperBounds(1, 1, 5, 20, 50, 200, Integer.MAX_VALUE);

    assertEquals(List.of("1", "2-5", "6-20", "21-50", "51-200", ">=201"), configuration.getLabels());
    assertEquals(6, configuration.size());
    assertEquals(1, (int) configuration.getLowerBound());
    assertEquals(Integer.MAX_VALUE, (int) configuration.getUpperBound());

    assertEquals(0, configuration.binIndexOf(1));
    assertEquals(1, configuration.binIndexOf(2));
    assertEquals(1, configuration.binIndexOf(5));
    assertEquals(2, configuration.binIndexOf(6));
    assertEquals(2, configuration.binIndexOf(20));
    assertEquals(3, configuration.binIndexOf(21));
    assertEquals(4, configuration.binIndexOf(51));
    assertEquals(4, configuration.binIndexOf(200));
    assertEquals(5, configuration.binIndexOf(201));
    assertEquals(5, configuration.binIndexOf(Integer.MAX_VALUE));

    /* below the range covered */
    assertEquals(-1, configuration.binIndexOf(0));
    assertFalse(configuration.contains(0));

    /* every value in the lower part of the range lands in exactly one bin, which is what contiguity is for */
    for (int value = 1; value <= 500; ++value) {
      final int index = configuration.binIndexOf(value);
      assertTrue(index >= 0, "value " + value + " landed in no bin");
      for (int otherIndex = 0; otherIndex < configuration.size(); ++otherIndex) {
        if (otherIndex != index) {
          assertFalse(configuration.getBin(otherIndex).contains(value),
              "value " + value + " landed in more than one bin");
        }
      }
    }
  }

  @Test
  public void lowerBoundsPutTheRoundNumberAtTheStartOfTheUnboundedBin() {
    /* the arrangement subnetwork sizes are reported by, stated as the point each bin starts at so the open ended
     * tail begins at 200 rather than at 201 */
    var configuration = BinBuilder.ofInclusiveIntegerLowerBounds(1, 2, 5, 20, 50, 200);

    assertEquals(List.of("[1,2)", "[2,5)", "[5,20)", "[20,50)", "[50,200)", ">=200"), configuration.getLabels());
    assertEquals(1, (int) configuration.getLowerBound());
    assertEquals(Integer.MAX_VALUE, (int) configuration.getUpperBound());

    assertEquals(4, configuration.binIndexOf(199));
    assertEquals(5, configuration.binIndexOf(200));
    assertEquals(5, configuration.binIndexOf(Integer.MAX_VALUE));
    assertEquals(-1, configuration.binIndexOf(0));

    for (int value = 1; value <= 500; ++value) {
      final int index = configuration.binIndexOf(value);
      assertTrue(index >= 0, "value " + value + " landed in no bin");
      for (int otherIndex = 0; otherIndex < configuration.size(); ++otherIndex) {
        if (otherIndex != index) {
          assertFalse(configuration.getBin(otherIndex).contains(value),
              "value " + value + " landed in more than one bin");
        }
      }
    }

    /* a single lower bound is simply everything from there on */
    assertEquals(List.of(">=1"), BinBuilder.ofInclusiveIntegerLowerBounds(1).getLabels());

    assertThrows(PlanItRunTimeException.class, () -> BinBuilder.ofInclusiveIntegerLowerBounds(1, 6, 6));
    assertThrows(PlanItRunTimeException.class, () -> BinBuilder.ofInclusiveIntegerLowerBounds(1, 21, 6));
    assertThrows(PlanItRunTimeException.class, BinBuilder::ofInclusiveIntegerLowerBounds);
  }

  @Test
  public void integerUpperBoundsAreValidated() {
    /* not in increasing order */
    assertThrows(PlanItRunTimeException.class,
        () -> BinBuilder.ofInclusiveIntegerUpperBounds(1, 5, 3));
    /* below the lower bound */
    assertThrows(PlanItRunTimeException.class,
        () -> BinBuilder.ofInclusiveIntegerUpperBounds(10, 5));
    /* unbounded is only allowed last */
    assertThrows(PlanItRunTimeException.class,
        () -> BinBuilder.ofInclusiveIntegerUpperBounds(1, Integer.MAX_VALUE, 5));
    /* no bounds at all */
    assertThrows(PlanItRunTimeException.class,
        () -> BinBuilder.ofInclusiveIntegerUpperBounds(1));
  }

  @Test
  public void equalWidthDoubleBinsCoverTheRangeInFull() {
    var configuration = BinBuilder.ofEqualWidthDoubleBins(0.0, 100.0, 4);

    assertEquals(4, configuration.size());
    assertEquals(0.0, (double) configuration.getLowerBound());
    assertEquals(100.0, (double) configuration.getUpperBound());

    assertEquals(0, configuration.binIndexOf(0.0));
    assertEquals(0, configuration.binIndexOf(24.9));
    assertEquals(1, configuration.binIndexOf(25.0));
    assertEquals(3, configuration.binIndexOf(99.9));
    /* only the last bin includes its upper bound, otherwise the range's own end would fall outside it */
    assertEquals(3, configuration.binIndexOf(100.0));
    assertEquals(-1, configuration.binIndexOf(100.1));
    assertEquals(-1, configuration.binIndexOf(-0.1));

    assertThrows(PlanItRunTimeException.class, () -> BinBuilder.ofEqualWidthDoubleBins(0.0, 100.0, 0));
    assertThrows(PlanItRunTimeException.class, () -> BinBuilder.ofEqualWidthDoubleBins(100.0, 0.0, 4));
  }

  @Test
  public void builderComposesArbitraryContiguousBins() {
    var configuration = BinBuilder.<Long>create()
        .addBin(0L, 10L, true, false, "small")
        .addBin(10L, 100L, true, false, "medium")
        .addBin(100L, Long.MAX_VALUE, true, true, "large")
        .build();

    assertEquals(List.of("small", "medium", "large"), configuration.getLabels());
    assertEquals("small", configuration.binFor(9L).getLabel());
    assertEquals("medium", configuration.binFor(10L).getLabel());
    assertEquals("large", configuration.binFor(Long.MAX_VALUE).getLabel());
    assertNull(configuration.binFor(-1L));
  }

  @Test
  public void countsAreCollectedPerBinWithOutOfRangeKeptApart() {
    var configuration = BinBuilder.ofInclusiveIntegerUpperBounds(1, 1, 5, 20, Integer.MAX_VALUE);
    var counts = BinnedCount.of(configuration);

    assertTrue(counts.isEmpty());

    counts.increment(1);
    counts.increment(1);
    counts.increment(4);
    counts.add(30, 7);
    /* outside the range, since the bins start at 1 */
    counts.increment(0);

    assertFalse(counts.isEmpty());
    assertEquals(2, counts.getCount(0));
    assertEquals(1, counts.getCount(1));
    assertEquals(0, counts.getCount(2));
    assertEquals(7, counts.getCount(3));
    assertEquals(1, counts.getOutOfRangeCount());
    /* the out of range values are part of the total, so it always adds up to what was offered */
    assertEquals(11, counts.getTotal());

    /* the bin a value falls in is found through the configuration, the count is then read by that index */
    assertEquals(7, counts.getCount(configuration.binIndexOf(1000)));

    var nonEmpty = counts.getCountsByLabel(true);
    assertEquals(List.of("1", "2-5", ">=21"), List.copyOf(nonEmpty.keySet()));
    assertEquals(List.of(2L, 1L, 7L), List.copyOf(nonEmpty.values()));

    var all = counts.getCountsByLabel(false);
    assertEquals(List.of("1", "2-5", "6-20", ">=21"), List.copyOf(all.keySet()));
    assertEquals(0L, all.get("6-20").longValue());
  }
}
