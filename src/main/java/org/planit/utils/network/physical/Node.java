package org.planit.utils.network.physical;

import org.planit.utils.graph.Vertex;

/**
 * Node is a vertex but not all vertices are nodes.
 * Nodes represent locations in the physical network where links intersect, usually
 * representing junctions or intersections
 * 
 * @author markr
 *
 */
public interface Node extends Vertex {

  /**
   * Collect the external id of the node
   * 
   * @return external id
   */
  Object getExternalId();

  /**
   * Set the external id of the node
   * 
   * @param externalId the external id to set
   */
  void setExternalId(Object externalId);

  /**
   * Returns whether the node has an external Id set
   * 
   * @return true if the node has an external Id, false otherwise
   */
  boolean hasExternalId();

}
