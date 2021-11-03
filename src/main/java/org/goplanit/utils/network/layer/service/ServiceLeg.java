package org.goplanit.utils.network.layer.service;

import java.util.Collection;
import java.util.List;

import org.goplanit.utils.graph.directed.DirectedEdge;
import org.goplanit.utils.network.layer.physical.Link;

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
   * @param <N> service node type
   * @return service node A
   */
  @SuppressWarnings("unchecked")
  public default <N extends ServiceNode> N getServiceNodeA() {
    return (N) getVertexA();
  }
  
  /** collect vertex B as something extending service node which is to be expected for any service leg. Convenience method
   * for readability
   * 
   * @param <N> service node type
   * @return service node B
   */
  @SuppressWarnings("unchecked")
  public default <N extends ServiceNode> N getServiceNodeB() {
    return (N) getVertexB();
  }  
  
  /** collect edgeSegment as something extending LegSegment which is to be expected for any Leg. Convenience method
   * for readability
   * 
   * @param <LS> leg segment type
   * @param directionAb the direction
   * @return leg segment in given direction
   */
  @SuppressWarnings("unchecked")
  public default <LS extends ServiceLegSegment> LS getLegSegment(boolean directionAb) {
    return (LS) getEdgeSegment(directionAb);
  }   
  
  /** collect edgeSegment Ab as something extending LegSegment which is to be expected for any Leg. Convenience method
   * for readability
   * 
   * @param <LS> leg segment type
   * @return leg segment in given direction
   */
  public default <LS extends ServiceLegSegment> LS getLegSegmentAb() {
    return getLegSegment(true);
  }   
  
  /** verify if leg Segment Ab is present
   * 
   * @return true when leg segment is present, false otherwise
   */
  public default boolean hasLegSegmentAb() {
    return hasEdgeSegmentAb();
  }   
  
  /** collect edgeSegment Ba as something extending LegSegment which is to be expected for any link. Convenience method
   * for readability
   * 
   * @param <LS> leg segment type
   * @return leg  segment in given direction
   */
  public default <LS extends ServiceLegSegment> LS getLinkSegmentBa() {
    return getLegSegment(false);
  } 
  
  /** verify if LegSegment BA is present
   * 
   * @return true when leg segment is present, false otherwise
   */
  public default boolean hasLegSegmentBa() {
    return hasEdgeSegmentBa();
  }

  /** Verify if name is present on leg
   * 
   * @return true when present, false otherwise
   */
  public default boolean hasName() {
    return getName()!=null && !getName().isBlank();
  }

  /** Collect all available leg segments of this leg 
   * 
   * @param <LS> type of leg segment
   * @return available leg segments
   */
  @SuppressWarnings("unchecked")
  public default <LS extends ServiceLegSegment> Collection<LS> getLegSegments(){
    return (Collection<LS>) getEdgeSegments();
  }
  
  /** Collect the links that make up this leg ordered and in direction from A to B
   * 
   * @return parent links this leg represents
   */
  public abstract List<Link> getParentLinks();
  
  /** Collect the first parent link
   * 
   * @return first parent link
   */
  public default Link getFirstParentLink() {
    return getParentLinks().get(0);
  }
  
  /** Collect the last parent link
   * 
   * @return last parent link
   */
  public default Link getLastParentLink() {
    return getParentLinks().get(getParentLinks().size()-1);
  }  
  
}
