package org.goplanit.utils.misc.binning;

import org.goplanit.utils.exceptions.PlanItRunTimeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * An ordered set of {@link Bin}s forming a single contiguous range, i.e. covering everything between the lower bound
 * of the first bin and the upper bound of the last without gaps or overlaps.
 * <p>
 * Contiguity is enforced rather than assumed, because the point of binning is that every value in range lands in
 * exactly one bin. A gap would silently lose values and an overlap would silently count them twice, and either would
 * show up only as a total that does not add up. Bins meeting at a shared bound must therefore have exactly one of the
 * two ends claiming it.
 * </p>
 * <p>
 * All bins share the same numeric type by construction, which is what the generic buys us over a loose collection of
 * bins.
 * </p>
 *
 * @author markr
 *
 * @param <T> numeric type of the bounds
 */
public class BinningConfiguration<T extends Number & Comparable<T>> {

  /** the bins in increasing order, immutable */
  protected final List<Bin<T>> bins;

  /**
   * Constructor
   *
   * @param bins in increasing order, expected to be contiguous
   */
  protected BinningConfiguration(List<Bin<T>> bins) {
    PlanItRunTimeException.throwIfNull(bins, "Bins of a binning configuration may not be null");
    PlanItRunTimeException.throwIf(bins.isEmpty(), "A binning configuration requires at least a single bin");

    for (int index = 0; index < bins.size() - 1; ++index) {
      var current = bins.get(index);
      var next = bins.get(index + 1);
      PlanItRunTimeException.throwIf(!current.isContiguousWith(next),
          "Bins %s and %s of a binning configuration are not contiguous, they either leave a gap, overlap, or are " +
              "not in increasing order", current.getLabel(), next.getLabel());
    }
    this.bins = Collections.unmodifiableList(new ArrayList<>(bins));
  }

  /**
   * Factory method
   *
   * @param <T> numeric type of the bounds
   * @param bins in increasing order, expected to be contiguous
   * @return created configuration
   */
  public static <T extends Number & Comparable<T>> BinningConfiguration<T> of(List<Bin<T>> bins) {
    return new BinningConfiguration<>(bins);
  }

  /**
   * Factory method
   *
   * @param <T> numeric type of the bounds
   * @param bins in increasing order, expected to be contiguous
   * @return created configuration
   */
  @SafeVarargs
  public static <T extends Number & Comparable<T>> BinningConfiguration<T> of(Bin<T>... bins) {
    return new BinningConfiguration<>(Arrays.asList(bins));
  }

  /**
   * Index of the bin the given value falls in.
   * <p>
   * A linear scan, since binning configurations are expected to hold a handful of bins and are typically consulted
   * with values that cluster in the lower ones.
   * </p>
   *
   * @param value to find the bin for
   * @return index of the bin, -1 when the value falls outside the range covered
   */
  public int binIndexOf(T value) {
    for (int index = 0; index < bins.size(); ++index) {
      if (bins.get(index).contains(value)) {
        return index;
      }
    }
    return -1;
  }

  /**
   * The bin the given value falls in
   *
   * @param value to find the bin for
   * @return the bin, null when the value falls outside the range covered
   */
  public Bin<T> binFor(T value) {
    final int index = binIndexOf(value);
    return index < 0 ? null : bins.get(index);
  }

  /**
   * Verify whether the given value falls within the range covered by this configuration
   *
   * @param value to check
   * @return true when it falls in one of the bins
   */
  public boolean contains(T value) {
    return binIndexOf(value) >= 0;
  }

  /**
   * The bin at the given index
   *
   * @param index of the bin
   * @return the bin
   */
  public Bin<T> getBin(int index) {
    return bins.get(index);
  }

  /**
   * The bins, in increasing order and immutable
   *
   * @return bins
   */
  public List<Bin<T>> getBins() {
    return bins;
  }

  /**
   * The labels of the bins, in increasing order
   *
   * @return labels
   */
  public List<String> getLabels() {
    return bins.stream().map(Bin::getLabel).collect(Collectors.toList());
  }

  /**
   * Number of bins
   *
   * @return number of bins
   */
  public int size() {
    return bins.size();
  }

  /**
   * Lower bound of the range covered, i.e. of the first bin
   *
   * @return lower bound
   */
  public T getLowerBound() {
    return bins.get(0).getLowerBound();
  }

  /**
   * Upper bound of the range covered, i.e. of the last bin
   *
   * @return upper bound
   */
  public T getUpperBound() {
    return bins.get(bins.size() - 1).getUpperBound();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof BinningConfiguration)) {
      return false;
    }
    return bins.equals(((BinningConfiguration<?>) other).bins);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(bins);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return String.join(", ", getLabels());
  }
}
