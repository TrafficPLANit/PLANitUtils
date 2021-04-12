package org.planit.utils.network.physical;

import java.util.Collection;

import org.planit.utils.graph.DirectedEdge;

/**
 * Link interface which extends the Edge interface with a unique id (not all edges are links) as
 * well as an external id
 * 
 * @author markr
 *
 */
public interface Link extends DirectedEdge {

  /**
   * Return id of this instance. This id is expected to be generated using the
   * org.planit.utils.misc.IdGenerator
   * 
   * @return linkId
   */
  public abstract long getLinkId();   
    
  /** collect vertex A as something extending node which is to be expected for any link. Convenience method
   * for readability
   * 
   * @param <N> node type
   * @return nodeA
   */
  @SuppressWarnings("unchecked")
  public default <N extends Node> N getNodeA() {
    return (N) getVertexA();
  }
  
  /** collect vertex A as something extending node which is to be expected for any link. Convenience method
   * for readability
   * 
   * @param <N> node type
   * @return nodeA
   */
  @SuppressWarnings("unchecked")
  public default <N extends Node> N getNodeB() {
    return (N) getVertexB();
  }  
  
  /** collect edgeSegment as something extending LinkSegment which is to be expected for any link. Convenience method
   * for readability
   * 
   * @param <LS> link segment type
   * @param directionAb the direction
   * @return link segment in given direction
   */
  @SuppressWarnings("unchecked")
  public default <LS extends LinkSegment> LS getLinkSegment(boolean directionAb) {
    return (LS) getEdgeSegment(directionAb);
  }   
  
  /** collect edgeSegment Ab as something extending LinkSegment which is to be expected for any link. Convenience method
   * for readability
   * 
   * @param <LS> link segment type
   * @return link segment in given direction
   */
  public default <LS extends LinkSegment> LS getLinkSegmentAb() {
    return getLinkSegment(true);
  }   
  
  /** verify if linkSegment Ab is present
   * 
   * @return true when link segment is present, false otherwise
   */
  public default boolean hasLinkSegmentAb() {
    return hasEdgeSegmentAb();
  }   
  
  /** collect edgeSegment Ba as something extending LinkSegment which is to be expected for any link. Convenience method
   * for readability
   * 
   * @param <LS> link segment type
   * @return link segment in given direction
   */
  public default <LS extends LinkSegment> LS getLinkSegmentBa() {
    return getLinkSegment(false);
  } 
  
  /** verify if linkSegment Ba is present
   * 
   * @return true when link segment is present, false otherwise
   */
  public default boolean hasLinkSegmentBa() {
    return hasEdgeSegmentBa();
  }

  /** verify if name is present on link
   * @return true when present, false otherwise
   */
  public default boolean hasName() {
    return getName()!=null && !getName().isBlank();
  }

  /** collect all available link segments of this link
   * @param <LS> type of link segment
   * @return available link segments
   */
  @SuppressWarnings("unchecked")
  public default <LS extends LinkSegment> Collection<LS> getLinkSegments(){
    return (Collection<LS>) getEdgeSegments();
  }
  
}
