package org.goplanit.utils.misc.binning;

import org.goplanit.utils.exceptions.PlanItRunTimeException;

import java.util.Objects;

/**
 * A single bin of a numeric range, i.e. an interval with a lower and upper bound, each of which may be inclusive or
 * exclusive, and a label to report it by.
 * <p>
 * The bound type is required to be both a {@code Number} and {@code Comparable}, since without an ordering there is
 * no way to decide whether a value falls in the bin. This does mean a raw {@code Number} cannot be used, only the
 * concrete numeric types, which is what makes the comparison meaningful in the first place.
 * </p>
 * <p>
 * When no label is given one is inferred, deliberately in plain interval notation, e.g. {@code [2,5)}. Prettier
 * conventions such as {@code 2-5} or {@code &gt;200} depend on knowing the bins are integral and contiguous, which a
 * single bin does not know, so those are left to whoever constructs the bins, see {@link BinBuilder}.
 * </p>
 *
 * @author markr
 *
 * @param <T> numeric type of the bounds
 */
public class Bin<T extends Number & Comparable<T>> {

  /** lower bound of this bin */
  protected final T lowerBound;

  /** upper bound of this bin */
  protected final T upperBound;

  /** when true the lower bound itself falls within this bin */
  protected final boolean lowerInclusive;

  /** when true the upper bound itself falls within this bin */
  protected final boolean upperInclusive;

  /** label to report this bin by, when null it is inferred from the bounds */
  protected final String label;

  /**
   * Constructor
   *
   * @param lowerBound of the bin
   * @param upperBound of the bin
   * @param lowerInclusive when true the lower bound falls within the bin
   * @param upperInclusive when true the upper bound falls within the bin
   * @param label to report the bin by, may be null in which case it is inferred
   */
  protected Bin(T lowerBound, T upperBound, boolean lowerInclusive, boolean upperInclusive, String label) {
    PlanItRunTimeException.throwIfNull(lowerBound, "Lower bound of a bin may not be null");
    PlanItRunTimeException.throwIfNull(upperBound, "Upper bound of a bin may not be null");

    final int boundComparison = lowerBound.compareTo(upperBound);
    PlanItRunTimeException.throwIf(boundComparison > 0,
        "Lower bound %s of a bin may not exceed its upper bound %s", lowerBound, upperBound);
    /* a bin holding nothing at all is never intended, so it is rejected rather than silently never matching */
    PlanItRunTimeException.throwIf(boundComparison == 0 && !(lowerInclusive && upperInclusive),
        "Bin with equal bounds %s must have both bounds inclusive, otherwise it can contain nothing", lowerBound);

    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
    this.lowerInclusive = lowerInclusive;
    this.upperInclusive = upperInclusive;
    this.label = label;
  }

  /**
   * Factory method for a bin with both bounds inclusive and an inferred label
   *
   * @param <T> numeric type of the bounds
   * @param lowerBound of the bin
   * @param upperBound of the bin
   * @return created bin
   */
  public static <T extends Number & Comparable<T>> Bin<T> of(T lowerBound, T upperBound) {
    return new Bin<>(lowerBound, upperBound, true, true, null);
  }

  /**
   * Factory method for a bin with both bounds inclusive
   *
   * @param <T> numeric type of the bounds
   * @param lowerBound of the bin
   * @param upperBound of the bin
   * @param label to report the bin by
   * @return created bin
   */
  public static <T extends Number & Comparable<T>> Bin<T> of(T lowerBound, T upperBound, String label) {
    return new Bin<>(lowerBound, upperBound, true, true, label);
  }

  /**
   * Factory method
   *
   * @param <T> numeric type of the bounds
   * @param lowerBound of the bin
   * @param upperBound of the bin
   * @param lowerInclusive when true the lower bound falls within the bin
   * @param upperInclusive when true the upper bound falls within the bin
   * @param label to report the bin by, may be null in which case it is inferred
   * @return created bin
   */
  public static <T extends Number & Comparable<T>> Bin<T> of(
      T lowerBound, T upperBound, boolean lowerInclusive, boolean upperInclusive, String label) {
    return new Bin<>(lowerBound, upperBound, lowerInclusive, upperInclusive, label);
  }

  /**
   * Copy of this bin carrying the given label instead of its own
   *
   * @param label to use
   * @return copy with the given label
   */
  public Bin<T> withLabel(String label) {
    return new Bin<>(lowerBound, upperBound, lowerInclusive, upperInclusive, label);
  }

  /**
   * Verify whether the given value falls within this bin
   *
   * @param value to check
   * @return true when it falls within this bin, false otherwise
   */
  public boolean contains(T value) {
    PlanItRunTimeException.throwIfNull(value, "Value to bin may not be null");

    final int lowerComparison = value.compareTo(lowerBound);
    if (lowerComparison < 0 || (lowerComparison == 0 && !lowerInclusive)) {
      return false;
    }
    final int upperComparison = value.compareTo(upperBound);
    return upperComparison < 0 || (upperComparison == 0 && upperInclusive);
  }

  /**
   * Verify whether the given bin directly follows this one without leaving a gap or overlapping, i.e. they meet at
   * the same bound and exactly one of the two ends meeting there includes it.
   *
   * @param next bin to check against
   * @return true when the given bin directly follows this one
   */
  public boolean isContiguousWith(Bin<T> next) {
    if (next == null || upperBound.compareTo(next.getLowerBound()) != 0) {
      return false;
    }
    /* exactly one of the two claims the shared bound, so no value is counted twice or missed */
    return upperInclusive != next.isLowerInclusive();
  }

  /**
   * Lower bound of this bin
   *
   * @return lower bound
   */
  public T getLowerBound() {
    return lowerBound;
  }

  /**
   * Upper bound of this bin
   *
   * @return upper bound
   */
  public T getUpperBound() {
    return upperBound;
  }

  /**
   * Verify whether the lower bound itself falls within this bin
   *
   * @return true when inclusive
   */
  public boolean isLowerInclusive() {
    return lowerInclusive;
  }

  /**
   * Verify whether the upper bound itself falls within this bin
   *
   * @return true when inclusive
   */
  public boolean isUpperInclusive() {
    return upperInclusive;
  }

  /**
   * Verify whether an explicit label was set rather than it being inferred
   *
   * @return true when a label was set
   */
  public boolean hasLabel() {
    return label != null;
  }

  /**
   * Label to report this bin by, its own when set, otherwise inferred from its bounds
   *
   * @return label
   */
  public String getLabel() {
    if (hasLabel()) {
      return label;
    }
    if (lowerBound.compareTo(upperBound) == 0) {
      return lowerBound.toString();
    }
    return String.format("%s%s,%s%s",
        lowerInclusive ? "[" : "(", lowerBound, upperBound, upperInclusive ? "]" : ")");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof Bin)) {
      return false;
    }
    var otherBin = (Bin<?>) other;
    return lowerInclusive == otherBin.lowerInclusive
        && upperInclusive == otherBin.upperInclusive
        && lowerBound.equals(otherBin.lowerBound)
        && upperBound.equals(otherBin.upperBound)
        && Objects.equals(label, otherBin.label);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return Objects.hash(lowerBound, upperBound, lowerInclusive, upperInclusive, label);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return getLabel();
  }
}
