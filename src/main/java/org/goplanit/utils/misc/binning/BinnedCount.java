package org.goplanit.utils.misc.binning;

import org.goplanit.utils.exceptions.PlanItRunTimeException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Counts collected against a {@link BinningConfiguration}, i.e. how many values fell in each of its bins.
 * <p>
 * Values falling outside the range covered are counted separately rather than discarded or rejected. Collecting a
 * distribution is usually done while something else is going on, e.g. a network parse, and aborting it over a value
 * the bins did not anticipate would be out of proportion; equally, silently dropping such values would leave the
 * total quietly wrong. Counting them apart keeps the totals honest either way.
 * </p>
 *
 * @author markr
 *
 * @param <T> numeric type of the values being counted
 */
public class BinnedCount<T extends Number & Comparable<T>> {

  /** the bins counted against */
  protected final BinningConfiguration<T> configuration;

  /** count per bin, indexed as the configuration's bins are */
  protected final long[] counts;

  /** count of values that fell outside the range covered by the bins */
  protected long outOfRangeCount = 0;

  /**
   * Constructor
   *
   * @param configuration to count against
   */
  protected BinnedCount(BinningConfiguration<T> configuration) {
    PlanItRunTimeException.throwIfNull(configuration, "Binning configuration to count against may not be null");
    this.configuration = configuration;
    this.counts = new long[configuration.size()];
  }

  /**
   * Factory method
   *
   * @param <T> numeric type of the values being counted
   * @param configuration to count against
   * @return created binned count
   */
  public static <T extends Number & Comparable<T>> BinnedCount<T> of(BinningConfiguration<T> configuration) {
    return new BinnedCount<>(configuration);
  }

  /**
   * Register a single occurrence of the given value
   *
   * @param value to count
   */
  public void increment(T value) {
    add(value, 1);
  }

  /**
   * Register the given number of occurrences of the given value
   *
   * @param value to count
   * @param amount to add
   */
  public void add(T value, long amount) {
    final int binIndex = configuration.binIndexOf(value);
    if (binIndex < 0) {
      outOfRangeCount += amount;
      return;
    }
    counts[binIndex] += amount;
  }

  /**
   * Count in the bin at the given index
   *
   * @param binIndex of the bin
   * @return count
   */
  public long getCount(int binIndex) {
    return counts[binIndex];
  }

  /**
   * Count of values that fell outside the range covered by the bins
   *
   * @return count
   */
  public long getOutOfRangeCount() {
    return outOfRangeCount;
  }

  /**
   * Total counted, including what fell outside the bins
   *
   * @return total
   */
  public long getTotal() {
    long total = outOfRangeCount;
    for (var count : counts) {
      total += count;
    }
    return total;
  }

  /**
   * Verify whether nothing was counted at all
   *
   * @return true when nothing was counted
   */
  public boolean isEmpty() {
    return getTotal() == 0;
  }

  /**
   * The counts by bin label, in increasing bin order
   *
   * @param onlyNonEmpty when true bins without anything counted in them are left out
   * @return counts by label
   */
  public Map<String, Long> getCountsByLabel(boolean onlyNonEmpty) {
    var countsByLabel = new LinkedHashMap<String, Long>();
    for (int index = 0; index < counts.length; ++index) {
      if (!onlyNonEmpty || counts[index] > 0) {
        countsByLabel.put(configuration.getBin(index).getLabel(), counts[index]);
      }
    }
    return countsByLabel;
  }

  /**
   * The bins counted against
   *
   * @return configuration
   */
  public BinningConfiguration<T> getConfiguration() {
    return configuration;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return getCountsByLabel(true).entrySet().stream()
        .map(entry -> entry.getKey() + ": " + entry.getValue())
        .collect(Collectors.joining(", "));
  }
}
