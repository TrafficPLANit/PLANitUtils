package org.goplanit.utils.misc.binning;

import org.goplanit.utils.exceptions.PlanItRunTimeException;

/**
 * Weights summed against a {@link BinningConfiguration}, i.e. how much was added in each of its bins.
 * <p>
 * Where {@link BinnedCount} counts whole occurrences, this adds any weight per value, such as a fractional demand or
 * the kilometres of the trips in each distance bin. As with a count, what falls outside the range covered is summed
 * apart rather than dropped, so the total always adds up to what was offered.
 * </p>
 *
 * @author markr
 *
 * @param <T> numeric type of the values being binned
 */
public class BinnedSum<T extends Number & Comparable<T>> {

  /** the bins summed against */
  private final BinningConfiguration<T> configuration;

  /** sum per bin, indexed as the configuration's bins are */
  private final double[] sums;

  /** sum of the weights of values that fell outside the range covered by the bins */
  private double outOfRangeSum = 0;

  /**
   * Constructor
   *
   * @param configuration to sum against
   */
  protected BinnedSum(BinningConfiguration<T> configuration) {
    PlanItRunTimeException.throwIfNull(configuration, "Binning configuration to sum against may not be null");
    this.configuration = configuration;
    this.sums = new double[configuration.size()];
  }

  /**
   * Factory method
   *
   * @param <T> numeric type of the values being binned
   * @param configuration to sum against
   * @return created binned sum
   */
  public static <T extends Number & Comparable<T>> BinnedSum<T> of(BinningConfiguration<T> configuration) {
    return new BinnedSum<>(configuration);
  }

  /**
   * Add the given weight to the bin the given value falls in
   *
   * @param value deciding the bin
   * @param weight to add
   */
  public void add(T value, double weight) {
    final int binIndex = configuration.binIndexOf(value);
    if (binIndex < 0) {
      outOfRangeSum += weight;
      return;
    }
    sums[binIndex] += weight;
  }

  /**
   * Sum in the bin at the given index
   *
   * @param binIndex of the bin
   * @return sum
   */
  public double getSum(int binIndex) {
    return sums[binIndex];
  }

  /**
   * Sum of the weights of values that fell outside the range covered by the bins
   *
   * @return sum
   */
  public double getOutOfRangeSum() {
    return outOfRangeSum;
  }

  /**
   * Total added, including what fell outside the bins
   *
   * @return total
   */
  public double getTotal() {
    double total = outOfRangeSum;
    for (var sum : sums) {
      total += sum;
    }
    return total;
  }

  /**
   * The bins summed against
   *
   * @return configuration
   */
  public BinningConfiguration<T> getConfiguration() {
    return configuration;
  }
}
