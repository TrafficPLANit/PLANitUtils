package org.planit.utils.cumulatives;

/**
 * Represents a cumulative point in time
 * 
 * @author markr
 *
 */
public class CumulativePointImpl implements CumulativePoint {

  /** generated UID */
  private static final long serialVersionUID = 630928063482940795L;

  /** the cumulative count */
  final double cumulativeCount;

  /** the reference time of this point in seconds */
  final double referenceTimeInSeconds;

  /**
   * Constructor
   * 
   * @param cumulativeCount the count
   * @param referenceTimeInSeconds the reference time
   */
  public CumulativePointImpl(final double cumulativeCount, final double referenceTimeInSeconds) {
    this.cumulativeCount = cumulativeCount;
    this.referenceTimeInSeconds = referenceTimeInSeconds;
  }

  /**
   * Absolute time difference in seconds between this and passed in cumulative point
   * 
   * @param otherCumulative to compare with
   * @return difference in seconds
   */
  @Override
  public double getAbsTimeDifferenceWith(CumulativePoint otherCumulative) {
    return Math.abs(otherCumulative.getReferenceTimeInSeconds() - this.getReferenceTimeInSeconds());
  }

  /**
   * Absolute cumulative difference between this and passed in cumulative point
   * 
   * @param otherCumulative to compare with
   * @return difference in cumulative units
   */
  @Override
  public double getAbsCumulativeDifferenceWith(CumulativePoint otherCumulative) {
    return Math.abs(otherCumulative.getCumulativeCount() - this.getCumulativeCount());
  }

  /**
   * Compute the absolute flow rate in cumulative units/h
   * 
   * @param adjacentCumulativePoint to use
   * @return flow rate between adjacent points in cumulative units/h
   */
  @Override
  public double computeAbsFlowRate(CumulativePoint adjacentCumulativePoint) {
    return getAbsCumulativeDifferenceWith(adjacentCumulativePoint)
        /
        getAbsTimeDifferenceWith(adjacentCumulativePoint);
  }

  /**
   * Verify if this point occurs later (given epsilon) than the passed in other point
   * 
   * @param otherCumulativePoint to compare with
   * @param timeInSecondsEpsilon epsilon
   * @return true if later
   */
  @Override
  public boolean isLater(CumulativePoint otherCumulativePoint, double timeInSecondsEpsilon) {
    return (this.getReferenceTimeInSeconds() - timeInSecondsEpsilon) > otherCumulativePoint.getReferenceTimeInSeconds();
  }

  /**
   * Verify if this point occurs later or equal (given epsilon) than the passed in other point
   * 
   * @param otherCumulativePoint to compare with
   * @param timeInSecondsEpsilon epsilon
   * @return true if later
   */
  @Override
  public boolean isLaterOrEqual(CumulativePoint otherCumulativePoint, double timeInSecondsEpsilon) {
    return (this.getReferenceTimeInSeconds() - timeInSecondsEpsilon) >= otherCumulativePoint
        .getReferenceTimeInSeconds();
  }

  /**
   * Verify if this point occurs earlier (given epsilon) than the passed in other point
   * 
   * @param otherCumulativePoint to compare with
   * @param timeInSecondsEpsilon epsilon
   * @return true if later
   */
  @Override
  public boolean isEarlier(CumulativePoint otherCumulativePoint, double timeInSecondsEpsilon) {
    return (this.getReferenceTimeInSeconds() + timeInSecondsEpsilon) < otherCumulativePoint.getReferenceTimeInSeconds();
  }

  /**
   * Verify if this point occurs earlier or equal (given epsilon) than the passed in other point
   * 
   * @param otherCumulativePoint to compare with
   * @param timeInSecondsEpsilon epsilon
   * @return true if later
   */
  @Override
  public boolean isEarlierOrEqual(CumulativePoint otherCumulativePoint, double timeInSecondsEpsilon) {
    return (this.getReferenceTimeInSeconds() + timeInSecondsEpsilon) <= otherCumulativePoint
        .getReferenceTimeInSeconds();
  }

  // getters - setters

  /**
   * @return the referenceTimeInSeconds
   */
  @Override
  public double getReferenceTimeInSeconds() {
    return referenceTimeInSeconds;
  }

  /**
   * @return the cumulativeCount
   */
  @Override
  public double getCumulativeCount() {
    return cumulativeCount;
  }

}
