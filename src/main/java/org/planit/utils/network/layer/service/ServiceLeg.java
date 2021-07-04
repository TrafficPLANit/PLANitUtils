package org.planit.utils.network.layer.service;

import java.util.Collection;

import org.planit.utils.graph.directed.DirectedEdge;

/**
 * Service leg interface which extends the Edge interface. A service leg comprises of one or more underlying physical links
 * and represents an uninterrupted service route between two service nodes.
 * 
 * @author markr
 *
 */
public interface ServiceLeg extends DirectedEdge {  
    
  /** collect vertex A as something extending service node which is to be expected for any service leg. Convenience method
   * for readability
   * 
   * @param <N> node type
   * @return nodeA
   */
  @SuppressWarnings("unchecked")
  public default <N extends ServiceNode> N getServiceNodeA() {
    return (N) getVertexA();
  }
  
  /** collect vertex A as something extending service node which is to be expected for any service leg. Convenience method
   * for readability
   * 
   * @param <N> node type
   * @return nodeA
   */
  @SuppressWarnings("unchecked")
  public default <N extends ServiceNode> N getServiceNodeB() {
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
  public default <LS extends ServiceLegSegment> LS getLinkSegment(boolean directionAb) {
    return (LS) getEdgeSegment(directionAb);
  }   
  
  /** collect edgeSegment Ab as something extending LinkSegment which is to be expected for any link. Convenience method
   * for readability
   * 
   * @param <LS> link segment type
   * @return link segment in given direction
   */
  public default <LS extends ServiceLegSegment> LS getLinkSegmentAb() {
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
  public default <LS extends ServiceLegSegment> LS getLinkSegmentBa() {
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
  public default <LS extends ServiceLegSegment> Collection<LS> getLinkSegments(){
    return (Collection<LS>) getEdgeSegments();
  }
  
}
