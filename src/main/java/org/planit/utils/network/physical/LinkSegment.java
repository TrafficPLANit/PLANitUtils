package org.planit.utils.network.physical;

import org.planit.utils.network.EdgeSegment;

public interface LinkSegment extends EdgeSegment{

	/**
	 * Default number of lanes
	 */
	short DEFAULT_NUMBER_OF_LANES = 1;
	/**
	 * Default maximum speed on a link segment
	 */
	double DEFAULT_MAX_SPEED = 130;

	/**
	 * Collect the unique link segment id
	 * @return link segment id
	 */
	long getLinkSegmentId();

	/**
	 * Collect the number of lanes of this segment
	 * @return number of lanes
	 */
	int getNumberOfLanes();

	/**
	 * Set the number of lanes
	 * @param numberOfLanes
	 */
	void setNumberOfLanes(int numberOfLanes);

	/**
	 * Return the maximum speed along this link for a specified mode
	 * 
	 * @param mode the specified mode
	 * @return maximum speed along this link for the specified mode
	 */
	double getMaximumSpeed(Mode mode);

	/**
	 * Set the maximum speed along this link for a specified mode
	 * 
	 * @param mode the specified mode
	 * @param maximumSpeed
	 *            maximum speed along this link for the specified mode
	 */
	void setMaximumSpeed(Mode mode, double maximumSpeed);

	/**
	 * Return the parent link of this link segment
	 * 
	 * @return Link object which is the parent of this link segment
	 */
	Link getParentLink();

}