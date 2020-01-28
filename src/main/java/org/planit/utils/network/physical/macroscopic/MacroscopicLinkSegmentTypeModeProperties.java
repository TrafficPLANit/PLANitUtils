package org.planit.utils.network.physical.macroscopic;

import org.planit.utils.network.physical.Mode;

/** the mode specific properties for a link segment type are contained in this class
 * 
 * @author markr
 *
 */
public interface MacroscopicLinkSegmentTypeModeProperties {

	/**
	 * Add mode properties for a specific mode
	 * 
	 * @param mode
	 *            mode of this link segment type
	 * @param modeProperties
	 *            properties of this link segment type
	 * @return modeProperties that were overwritten (if any)
	 */
	MacroscopicModeProperties addProperties(Mode mode, MacroscopicModeProperties modeProperties);

	/**
	 * Get mode properties for a specific mode
	 * 
	 * @param mode
	 *            mode
	 * @return properties for specified mode
	 */
	MacroscopicModeProperties getProperties(Mode mode);

}