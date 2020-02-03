package org.planit.utils.cumulatives;

import java.io.Serializable;

public interface CumulativePoint extends Serializable {

	/** Absolute time difference in seconds between this and passed in cumulative point
	 * @param otherCumulative
	 * @return difference in seconds
	 */
	double getAbsTimeDifferenceWith(CumulativePoint otherCumulative);

	/** Absolute cumulative difference between this and passed in cumulative point
	 * @param otherCumulative
	 * @return differece in cumulative units
	 */
	double getAbsCumulativeDifferenceWith(CumulativePoint otherCumulative);

	/** Compute the absolute flow rate in cumulative units/h
	 * @param adjacentCumulativePoint
	 * @return flow rate between adjacent points in cumulative units/h
	 */
	double computeAbsFlowRate(CumulativePoint adjacentCumulativePoint);

	/** Verify if this point occurs later (given epsilon) than the passed in other point
	 * @param otherCumulativePoint
	 * @param timeInSecondsEpsilon
	 * @return true if later
	 */
	boolean isLater(CumulativePoint otherCumulativePoint, double timeInSecondsEpsilon);

	/** Verify if this point occurs later or equal (given epsilon) than the passed in other point
	 * @param otherCumulativePoint
	 * @param timeInSecondsEpsilon
	 * @return true if later
	 */
	boolean isLaterOrEqual(CumulativePoint otherCumulativePoint, double timeInSecondsEpsilon);

	/** Verify if this point occurs earlier (given epsilon) than the passed in other point
	 * @param otherCumulativePoint
	 * @param timeInSecondsEpsilon
	 * @return true if later
	 */
	boolean isEarlier(CumulativePoint otherCumulativePoint, double timeInSecondsEpsilon);

	/** Verify if this point occurs earlier or equal (given epsilon) than the passed in other point
	 * @param otherCumulativePoint
	 * @param timeInSecondsEpsilon
	 * @return true if later
	 */
	boolean isEarlierOrEqual(CumulativePoint otherCumulativePoint, double timeInSecondsEpsilon);

	/**
	 * @return the referenceTimeInSeconds
	 */
	double getReferenceTimeInSeconds();

	/**
	 * @return the cumulativeCount
	 */
	double getCumulativeCount();

}