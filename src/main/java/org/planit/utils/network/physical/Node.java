package org.planit.utils.network.physical;

import org.planit.utils.network.Vertex;

/** Node is a vertex but not all vertices are nodes.
 * Nodes represent locations in the physical network where links intersect, usually 
 * representing junctions or intersections
 * 
 * @author markr
 *
 */
public interface Node extends Vertex {

	/** Collect the external id of the node
	 * @return external id
	 */
	long getExternalId();

	/** Set the external id of the node
	 * @param externalId
	 */
	void setExternalId(long externalId);

}