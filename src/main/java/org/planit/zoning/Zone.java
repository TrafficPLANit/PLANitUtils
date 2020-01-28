package org.planit.zoning;

import org.planit.utils.network.virtual.Centroid;

/** Zone represent a geographical area with a centroid which in turn represent the
 * single point of departure of all traffic in the zone
 * 
 * @author markr
 *
 */
public interface Zone {

	/**
	 * Returns the unique id of this zone
	 * 
	 * @return id of this zone
	 */
	long getId();
	
	/** Collect the external id of this zone
	 * @return the external id
	 */
	long getExternalId();

	/**
	 * Returns the centroid of this zone
	 * 
	 * @return centroid of this zone
	 */
	Centroid getCentroid();

}