package org.planit.utils.network.physical;

import org.planit.exceptions.PlanItException;
import org.planit.utils.network.Edge;

/**
 * Link interface which extends the Edge interface with a unique id (not all edges are links) as well as an external id
 * 
 * @author markr
 *
 */
public interface Link extends Edge {

	/**
	 * Register linkSegment. If there already exists a linkSegment for that
	 * direction it should be replaced and returned
	 * 
	 * @param linkSegment
	 *            the link segment to be registered
	 * @param directionAB
	 *            direction of travel
	 * @return the replaced LinkSegment
	 * @throws PlanItException
	 *             thrown if there is an error
	 */
	LinkSegment registerLinkSegment(LinkSegment linkSegment, boolean directionAB) throws PlanItException;

	/**
	 * get the link's unique id
	 * @return linkId
	 */
	long getLinkId();

	/**
	 * Set the external id
	 * @param externalId
	 */
	void setExternalId(long externalId);

	/**
	 * Collect the external id
	 * @return externalID
	 */
	long getExternalId();

}