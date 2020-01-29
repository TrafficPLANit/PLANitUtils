package org.planit.utils.network.physical;

/** Interface to represent a mode
 * 
 * @author markr
 *
 */
public interface Mode extends Comparable<Mode>{

	
	/** Passenger car unit conversion factor for this mode
	 * @return pcu
	 */
	double getPcu();

	/** Name of this mode
	 * @return the name
	 */
	String getName();

	/** Return id of this instance. This id is expected to be generated using the org.planit.utils.misc.IdGenerator
	 * @return unique id
	 */
	long getId();

	/** External id of this mode (if any)
	 * @return externalId
	 */
	long getExternalId();

}