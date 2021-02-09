package org.planit.utils.graph;

import java.util.Set;

/**
 * Directed vertex representation connected to one or more edge segments that have direction. The vertex itself is of course not directional
 * 
 * @author markr
 *
 */
public interface DirectedVertex extends Vertex {

  /**
   * Test whether no exit edge segments have been registered
   * 
   * @return true if no exit edge segments have been registered, false otherwise
   */
  public boolean hasExitEdgeSegments();
  
  /**
   * Test whether no entry edge segments have been registered
   * 
   * @return true if no entry edge segments have been registered, false otherwise
   */
  public boolean hasEntryEdgeSegments();  
  
  /**
   * Add edgeSegment, do not invoke when parsing networks, this connection is
   * auto-populated before the assignment starts based on the edge segment
   * vertices that have been registered.
   * 
   * @param edgeSegment EdgeSegment object to be added
   * @return true when added, false when already present (and not added)
   */
  public boolean addEdgeSegment(EdgeSegment edgeSegment);

  /**
   * Remove edgeSegment
   * 
   * @param edgeSegment EdgeSegment object to be removed
   * @return true when removed, false when not present (and not removed)
   */
  public boolean removeEdgeSegment(EdgeSegment edgeSegment);
  
  /**
   * Collect the entry edge segments of this vertex
   * 
   * @return edgeSegments
   */
  public Set<EdgeSegment> getEntryEdgeSegments();

  /**
   * Collect the exit edge segments of this vertex
   * 
   * @return edgeSegments
   */
  public Set<EdgeSegment> getExitEdgeSegments();  
  
  /**
   * Collect the number of entry edge segments of this vertex
   * 
   * @return number of entry edge segments
   */
  public int getNumberOfEntryEdgeSegments();

  /**
   * Collect the number of exit edge segments of this vertex
   * 
   * @return number of exit edge segments
   */
  public int getNumberOfExitEdgeSegments();

  /** replace edge segment
   * @param edgeSegmentToReplace to replace
   * @param edgeSegmentToReplaceWith to replace with
   * @param forceInsert when edge segment to replace cannot be found, replacement is still inserted when true, when false not
   * @return true when replacement/insert was successful
   */
  default public boolean replace(EdgeSegment edgeSegmentToReplace, EdgeSegment edgeSegmentToReplaceWith, boolean forceInsert) {
    if(removeEdgeSegment(edgeSegmentToReplace) || forceInsert) {
      return addEdgeSegment(edgeSegmentToReplaceWith);
    }
    return false;
  }
  
  /** collect the first edge segment corresponding to the provided other vertex
   * 
   * @param otherVertex to use
   * @return first edge segment matching this signature
   */  
  default EdgeSegment getEdgeSegment(DirectedVertex otherVertex) {
    for(EdgeSegment edgeSegment : getExitEdgeSegments()) {
      if(edgeSegment.getDownstreamVertex().equals(otherVertex)) {
        return edgeSegment;
      }
    }
    for(EdgeSegment edgeSegment : getEntryEdgeSegments()) {
      if(edgeSegment.getUpstreamVertex().equals(otherVertex)) {
        return edgeSegment;
      }
    }
    return null;
  }

}
