package org.planit.utils.network.physical;

/** Interface to represent a mode
 * 
 * @author markr
 *
 */
public interface Mode extends Comparable<Mode>{

	
	/** Passenger car unit conversion factor for this mode
	 * @return
	 */
	double getPcu();

	/** Name of this mode
	 * @return the name
	 */
	String getName();

	/** Unique id of this mode
	 * @return unique id
	 */
	long getId();

	/** External id of this mode (if any)
	 * @return externalId
	 */
	long getExternalId();

}