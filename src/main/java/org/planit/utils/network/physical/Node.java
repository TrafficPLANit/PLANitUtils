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
   * Collect the id of the node. Not all vertices need to be nodes, this node id is contiguous and unique to the nodes
   * in the network, but not necesssarily across all vertices in the network
   * 
   * @return node id
   */
  long getNodeId();  
  
}
