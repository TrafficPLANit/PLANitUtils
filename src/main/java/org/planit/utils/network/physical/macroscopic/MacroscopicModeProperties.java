package org.planit.utils.network.physical.macroscopic;

/** Contains the properties of a mode specific to a link segment type
 * @author markr
 *
 */
public interface MacroscopicModeProperties {

	/**
	 * Default max speed in km/h
	 */
	double DEFAULT_MAXIMUM_SPEED = 80;
	/**
	 * Default critical speed, i.e. speed at capacity in km/h
	 */
	double DEFAULT_CRITICAL_SPEED = 60;
	/**
	 * Epsilon margin when comparing speeds (km/h)
	 */
	double DEFAULT_SPEED_EPSILON = 0.000001;

	/** Collect the maximum speed in km/h
	 * @return the maximum speed in km/h
	 */
	double getMaxSpeed();

	/** Collect the critical speed in km/h
	 * @return the critical speed in km/h
	 */
	double getCriticalSpeed();

}