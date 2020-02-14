package org.planit.utils.network.physical.macroscopic;

import java.util.Map;

import org.planit.utils.network.physical.Mode;

/**
 * The macroscopic link segment type characteristics are contained in this class
 * 
 * @author markr
 *
 */
public interface MacroscopicLinkSegmentType {

	/**
	 * Default maximum density per lane (veh/km)
	 */
	double DEFAULT_MAXIMUM_DENSITY_LANE = 180;
	/**
	 * Default capacity per lane (veh/h)
	 */
	double DEFAULT_CAPACITY_LANE = 1800.0f;

	/** Collect the unique id of this macroscopic link segment type
	 * @return the id
	 */
	int getId();

	/** Collect the name of this macroscopic link segment type
	 * @return the name
	 */
	String getName();

	/** Set the name of this macroscopic link segment type
	 * @param name the name
	 */
	void setName(String name);

	/** Collect the capacity per lane of this macroscopic link segment type
	 * @return capacity per lane in pcu/h/lane
	 */
	double getCapacityPerLane();

	/** Collect the maximum density per lane for this macroscopic link segment type
	 * @return the maximum density per lane in pcu/km/lane
	 */
	double getMaximumDensityPerLane();

	/** Collect the external id of this macroscopic link segment type
	 * @return the external id
	 */
	long getExternalId();

	/**
	 * Set the mode properties for a specified mode for this link
	 * 
	 * @param mode the specified mode
	 * @param modeProperties the mode properties for this link
	 */
	void setModeProperties(Map<Mode, MacroscopicModeProperties> modeProperties);
	
	/**
	 * Returns the mode properties for a specified mode along this link
	 * 
	 * @param mode the specified mode
	 * @return the mode properties for this link and mode
	 */
	MacroscopicModeProperties getModeProperties(Mode mode);

}