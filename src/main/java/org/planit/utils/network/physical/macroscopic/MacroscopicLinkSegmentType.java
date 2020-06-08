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
	 * Default capacity per lane (veh/h)
	 */
	double DEFAULT_CAPACITY_LANE = 1800.0f;

	/** 
	 * Return the unique id of this macroscopic link segment type
	 * 
	 * @return the id
	 */
	long getId();

	/** 
	 * Return the name of this macroscopic link segment type
	 * 
	 * @return the name
	 */
	String getName();

	/** 
	 * Set the name of this macroscopic link segment type
	 * 
	 * @param name the name
	 */
	void setName(String name);

	/** 
	 * Return the capacity per lane of this macroscopic link segment type
	 * 
	 * @return capacity per lane in pcu/h/lane
	 */
	double getCapacityPerLane();

	/** 
	 * Return the maximum density per lane for this macroscopic link segment type
	 * 
	 * @return the maximum density per lane in pcu/km/lane
	 */
	double getMaximumDensityPerLane();

	/** 
	 * Return the external id of this macroscopic link segment type
	 * 
	 * @return the external id
	 */
	Object getExternalId();

	/**
	 * Return whether this link segment type has an external Id
	 * 
	 * return true if this link segment type has an external Id, false otherwise
	 */
	boolean hasExternalId();
	
	/**
	 * Set the map of mode properties for this link
	 * 
	 * @param mode the specified mode
	 * @param modeProperties map of mode properties for this link
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