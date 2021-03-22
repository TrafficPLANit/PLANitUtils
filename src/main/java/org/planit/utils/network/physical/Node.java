package org.planit.utils.network.physical;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.planit.utils.graph.DirectedVertex;
import org.planit.utils.graph.Edge;
import org.planit.utils.graph.EdgeSegment;

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
  public long getNodeId();
  
  
  /** get the name of this node (if any)
   * 
   * @return name of the node
   */
  public String getName();
  
  /** set the name of the node
   * 
   * @param name of the node
   */
  public void setName(String name);
  
  /**
   * It is expected that nodes are used in conjunction with links. If so, this method will cast the edges of the node to a links collection
   * for readability when collecting a node's edges
   * 
   * @param <L> link type
   * @return edges cast as collection of links
   */
  @SuppressWarnings("unchecked")
  public default <L extends Edge> Collection<L> getLinks() {
    return (Collection<L>) getEdges();
  }
  
  /**
   * It is expected that nodes are used in conjunction with link segments. If so, this method will cast the edge segments of the node to link segments
   * for readability when collecting node's edge segments
   * 
   * @param <LS> edge segment type
   * @return edgeSegments as collection of linkSegments
   */
  @SuppressWarnings("unchecked")  
  public default <LS extends EdgeSegment> Set<LS> getEntryLinkSegments() {
    return (Set<LS>) getEntryEdgeSegments();
  }
  
  /**
   * It is expected that nodes are used in conjunction with link segments. If so, this method will cast the edge segments of the node to link segments
   * for readability when collecting node's edge segments
   * 
   * @param <LS> edge segment type
   * @return edgeSegments as collection of linkSegments
   */
  @SuppressWarnings("unchecked")  
  public default <LS extends EdgeSegment> Set<LS> getExitLinkSegments() {
    return (Set<LS>) getExitEdgeSegments();
  }


  /** collect the first edge segment corresponding to the provided end node
   * @param <LS> edge segment type
   * @param endNode to use
   * @return first edge segment matching this signature
   */
  @SuppressWarnings("unchecked")
  public default <LS extends EdgeSegment> LS getLinkSegment(Node endNode) {
    return (LS) getEdgeSegment(endNode);
  }


  /**
   * Collect the first available entry link segment using the iterator internally. It is assumed
   * at least one entry is available
   */
  @SuppressWarnings("unchecked")
  public default <LS extends EdgeSegment> LS getFirstEntryLinkSegment(){
    return (LS) getEntryLinkSegments().iterator().next();
  }
  
  /**
   * Collect the first available exit link segment using the iterator internally. It is assumed
   * at least one entry is available
   */
  @SuppressWarnings("unchecked")
  public default <LS extends EdgeSegment> LS getFirstExitLinkSegment(){
    return (LS) getExitLinkSegments().iterator().next();
  }  
  
}
