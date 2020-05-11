package org.planit.utils.network.virtual;

import org.planit.exceptions.PlanItException;
import org.planit.utils.graph.Edge;

/** the connecting component between centroid and a first physical node in the network.
 * Note that all connectoids are edges but not all edges are connectoids
 * 
 * @author markr
 *
 */
public interface Connectoid extends Edge {

	/**
	 * Default connectoid length in km
	 */
	double DEFAULT_LENGTH_KM = 1.0;

	/**
	 * Register connectoidSegment.
	 * 
	 * If there already exists a connectoidSegment for that direction it is replaced
	 * and returned
	 * 
	 * @param connectoidSegment
	 *            connectoid segment to be registered
	 * @param directionAB
	 *            direction of travel
	 * @return replaced ConnectoidSegment
	 * @throws PlanItException
	 *             thrown if there is an error
	 */
	ConnectoidSegment registerConnectoidSegment(ConnectoidSegment connectoidSegment, boolean directionAB)
			throws PlanItException;

	/**
	 * 
	 * Return the unique id of this connectoid
	 * 
	 * @return id of this connectoid
	 */
	long getConnectoidId();

	/** Collect the external id of this connectoid
	 * @return external id
	 */
	Object getExternalId();

	/** 
	 * set the external id of this connectoid
	 * @param externalId
	 */
	void setExternalId(Object externalId);
	
	/**
	 * Returns true if this connectoid has its external Id set, false otherwise
	 * 
	 * @return true if this connectoid has its external Id set, false otherwise
	 */
	boolean hasExternalId();

}