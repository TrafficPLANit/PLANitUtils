package org.goplanit.utils.misc.binning;

import org.goplanit.utils.exceptions.PlanItRunTimeException;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for a {@link BinningConfiguration}, both freely bin by bin and via factory methods for the arrangements
 * most often wanted.
 * <p>
 * Bins meeting at a shared bound is what makes a configuration contiguous, so bins covering whole numbers are built
 * half open, e.g. {@code [2,6)} rather than {@code [2,5]}, even where the caller thinks in inclusive terms. The
 * inclusive form is what the integer factory methods accept and what their labels report, e.g. {@code 2-5}, so the
 * half open representation stays an implementation detail of the range rather than something a caller has to
 * translate.
 * </p>
 *
 * @author markr
 *
 * @param <T> numeric type of the bounds
 */
public class BinBuilder<T extends Number & Comparable<T>> {

  /** bins collected so far, in the order they were added */
  protected final List<Bin<T>> bins = new ArrayList<>();

  /**
   * Constructor
   */
  protected BinBuilder() {
  }

  /**
   * The label an integral bin covering the given inclusive range is reported by
   *
   * @param lowerBound of the range, inclusive
   * @param upperBound of the range, inclusive
   * @return label
   */
  protected static String integralLabel(int lowerBound, int upperBound) {
    return lowerBound == upperBound ? Integer.toString(lowerBound) : lowerBound + "-" + upperBound;
  }

  /**
   * Factory method for an empty builder to add bins to
   *
   * @param <T> numeric type of the bounds
   * @return created builder
   */
  public static <T extends Number & Comparable<T>> BinBuilder<T> create() {
    return new BinBuilder<>();
  }

  /**
   * Factory method for bins over whole numbers given the inclusive upper bound of each, i.e. the arrangement one
   * reaches for when reporting a distribution over sizes or counts.
   * <p>
   * For example {@code ofInclusiveIntegerUpperBounds(1, 1, 5, 20, Integer.MAX_VALUE)} yields bins labelled
   * {@code 1}, {@code 2-5}, {@code 6-20} and {@code &gt;=21}. An upper bound of {@code Integer.MAX_VALUE} is taken to
   * mean unbounded and is therefore only allowed as the last one.
   * </p>
   *
   * @param lowerBound of the first bin, inclusive
   * @param inclusiveUpperBounds upper bound of each bin in increasing order, inclusive
   * @return created configuration
   */
  public static BinningConfiguration<Integer> ofInclusiveIntegerUpperBounds(
      int lowerBound, int... inclusiveUpperBounds) {
    PlanItRunTimeException.throwIf(inclusiveUpperBounds == null || inclusiveUpperBounds.length == 0,
        "At least a single upper bound is required to construct integer bins");

    var builder = BinBuilder.<Integer>create();
    int currentLowerBound = lowerBound;
    for (int index = 0; index < inclusiveUpperBounds.length; ++index) {
      final int upperBound = inclusiveUpperBounds[index];
      PlanItRunTimeException.throwIf(upperBound < currentLowerBound,
          "Integer bin upper bounds must be in increasing order and at or above the lower bound %d, found %d",
          currentLowerBound, upperBound);

      if (upperBound == Integer.MAX_VALUE) {
        /* the type's maximum stands for unbounded, so nothing can follow it, and it is the one bin whose upper
         * bound is inclusive since there is no next bound to hand it to */
        PlanItRunTimeException.throwIf(index != inclusiveUpperBounds.length - 1,
            "An unbounded integer bin may only be the last one");
        builder.addBin(currentLowerBound, Integer.MAX_VALUE, true, true, ">=" + currentLowerBound);
        break;
      }

      builder.addBin(
          currentLowerBound, upperBound + 1, true, false, integralLabel(currentLowerBound, upperBound));
      currentLowerBound = upperBound + 1;
    }
    return builder.build();
  }

  /**
   * Factory method for bins over whole numbers given the inclusive lower bound of each, the last of which is
   * unbounded.
   * <p>
   * For example {@code ofInclusiveIntegerLowerBounds(1, 2, 5, 20, 200)} yields bins labelled {@code [1,2)},
   * {@code [2,5)}, {@code [5,20)}, {@code [20,200)} and {@code &gt;=200}. Preferable over
   * {@link #ofInclusiveIntegerUpperBounds(int, int...)} whenever it is the point at which a bin starts that carries
   * the meaning, in particular where the open ended tail should begin at a round number: stating the bounds as upper
   * ones would put that number at the top of the preceding bin instead, leaving the tail to start at an awkward one
   * above it.
   * </p>
   * <p>
   * The bins are labelled in interval notation rather than by the whole numbers they contain, since here it is the
   * bounds themselves that were chosen and reporting them is what lets a reader see where the next bin picks up.
   * Only the unbounded one is labelled explicitly, having no upper bound worth printing.
   * </p>
   *
   * @param inclusiveLowerBounds lower bound of each bin in increasing order, inclusive, the last one opening the
   *          unbounded bin
   * @return created configuration
   */
  public static BinningConfiguration<Integer> ofInclusiveIntegerLowerBounds(int... inclusiveLowerBounds) {
    PlanItRunTimeException.throwIf(inclusiveLowerBounds == null || inclusiveLowerBounds.length == 0,
        "At least a single lower bound is required to construct integer bins");

    var builder = BinBuilder.<Integer>create();
    for (int index = 0; index < inclusiveLowerBounds.length; ++index) {
      final int lowerBound = inclusiveLowerBounds[index];
      if (index == inclusiveLowerBounds.length - 1) {
        builder.addBin(lowerBound, Integer.MAX_VALUE, true, true, ">=" + lowerBound);
        break;
      }
      final int nextLowerBound = inclusiveLowerBounds[index + 1];
      PlanItRunTimeException.throwIf(nextLowerBound <= lowerBound,
          "Integer bin lower bounds must be in strictly increasing order, found %d after %d",
          nextLowerBound, lowerBound);
      builder.addBin(lowerBound, nextLowerBound, true, false, null);
    }
    return builder.build();
  }

  /**
   * Factory method for a number of equally wide bins spanning the given range, each half open except the last which
   * includes the upper bound so the range is covered in full
   *
   * @param lowerBound of the range, inclusive
   * @param upperBound of the range, inclusive
   * @param binCount number of bins to divide the range in
   * @return created configuration
   */
  public static BinningConfiguration<Double> ofEqualWidthDoubleBins(
      double lowerBound, double upperBound, int binCount) {
    PlanItRunTimeException.throwIf(binCount < 1, "At least a single bin is required, found %d", binCount);
    PlanItRunTimeException.throwIf(upperBound <= lowerBound,
        "Upper bound %s must exceed lower bound %s to divide the range in bins", upperBound, lowerBound);

    var builder = BinBuilder.<Double>create();
    final double width = (upperBound - lowerBound) / binCount;
    for (int index = 0; index < binCount; ++index) {
      final boolean last = index == binCount - 1;
      /* the upper bound of the range itself is computed rather than accumulated, so rounding cannot make the last
       * bin fall short of it */
      final double binLowerBound = lowerBound + index * width;
      final double binUpperBound = last ? upperBound : lowerBound + (index + 1) * width;
      builder.addBin(binLowerBound, binUpperBound, true, last, null);
    }
    return builder.build();
  }

  /**
   * Add a bin with both bounds inclusive and an inferred label
   *
   * @param lowerBound of the bin
   * @param upperBound of the bin
   * @return this builder for chaining
   */
  public BinBuilder<T> addBin(T lowerBound, T upperBound) {
    return addBin(lowerBound, upperBound, true, true, null);
  }

  /**
   * Add a bin
   *
   * @param lowerBound of the bin
   * @param upperBound of the bin
   * @param lowerInclusive when true the lower bound falls within the bin
   * @param upperInclusive when true the upper bound falls within the bin
   * @param label to report the bin by, may be null in which case it is inferred
   * @return this builder for chaining
   */
  public BinBuilder<T> addBin(
      T lowerBound, T upperBound, boolean lowerInclusive, boolean upperInclusive, String label) {
    bins.add(Bin.of(lowerBound, upperBound, lowerInclusive, upperInclusive, label));
    return this;
  }

  /**
   * Add an already constructed bin
   *
   * @param bin to add
   * @return this builder for chaining
   */
  public BinBuilder<T> addBin(Bin<T> bin) {
    bins.add(bin);
    return this;
  }

  /**
   * Build the configuration from the bins added so far, which is where contiguity is verified
   *
   * @return created configuration
   */
  public BinningConfiguration<T> build() {
    return BinningConfiguration.of(bins);
  }
}
