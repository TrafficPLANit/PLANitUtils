package org.planit.utils.network.physical;

import java.util.Collection;
import java.util.Set;

import org.planit.utils.graph.DirectedVertex;
import org.planit.utils.graph.Edge;
import org.planit.utils.graph.EdgeSegment;
import org.planit.utils.network.physical.macroscopic.MacroscopicLinkSegment;

/**
 * Node is a vertex but not all vertices are nodes.
 * Nodes represent locations in the physical network where links intersect, usually
 * representing junctions or intersections
 * 
 * @author markr
 *
 */
public interface Node extends DirectedVertex {
 
  /**
   * Collect the id of the node. Not all vertices need to be nodes, this node id is contiguous and unique to the nodes
   * in the network, but not necessarily across all vertices in the network
   * 
   * @return node id
   */
  long getNodeId();
  
  
  /** get the name of this node (if any)
   * 
   * @return name of the node
   */
  String getName();
  
  /** set the name of the node
   * 
   * @param name of the node
   */
  void setName(String name);
  
  /**
   * It is expected that nodes are used in conjunction with links. If so, this method will cast the edges of the node to a links collection
   * for readability when collecting a node's edges
   * 
   * @param <L>
   * @return edges cast as collection of links
   */
  @SuppressWarnings("unchecked")
  default <L extends Edge> Collection<L> getLinks() {
    return (Collection<L>) getEdges();
  }
  
  /**
   * It is expected that nodes are used in conjunction with link segments. If so, this method will cast the edge segments of the node to link segments
   * for readability when collecting node's edge segments
   * 
   * @param <LS>
   * @return edgeSegments as collection of linkSegments
   */
  @SuppressWarnings("unchecked")  
  default <LS extends EdgeSegment> Set<LS> getEntryLinkSegments() {
    return (Set<LS>) getEntryEdgeSegments();
  }
  
  /**
   * It is expected that nodes are used in conjunction with link segments. If so, this method will cast the edge segments of the node to link segments
   * for readability when collecting node's edge segments
   * 
   * @param <LS>
   * @return edgeSegments as collection of linkSegments
   */
  @SuppressWarnings("unchecked")  
  default <LS extends EdgeSegment> Set<LS> getExitLinkSegments() {
    return (Set<LS>) getExitEdgeSegments();
  }


  /** collect the first edge segment corresponding to the provided end node
   * @param <LS>
   * @param endNode to use
   * @return first edge segment matching this signature
   */
  default <LS extends EdgeSegment> LS getLinkSegment(Node endNode) {
    return (LS) getEdgeSegment(endNode);
  }
  
}
