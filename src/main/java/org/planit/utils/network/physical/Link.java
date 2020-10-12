package org.planit.utils.network.physical;

import org.planit.utils.graph.DirectedEdge;

import com.vividsolutions.jts.geom.LineString;

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
  long getLinkId();   
  
  /**
   * Collect the geometry of this line
   * @return lineString
   */
  LineString getGeometry();
  
  /**
   * set the geometry of this link as a line string
   * @param lineString
   */
  void setGeometry(LineString lineString);
  
  /** collect vertex A as something extending node which is to be expected for any link. Convenience method
   * for readability
   * 
   * @param <N>
   * @return nodeA
   */
  @SuppressWarnings("unchecked")
  default <N extends Node> N getNodeA() {
    return (N) getVertexA();
  }
  
  /** collect vertex A as something extending node which is to be expected for any link. Convenience method
   * for readability
   * 
   * @param <N>
   * @return nodeA
   */
  @SuppressWarnings("unchecked")
  default <N extends Node> N getNodeB() {
    return (N) getVertexB();
  }  
  
  /** collect edgeSegment as something extending LinkSegment which is to be expected for any link. Convenience method
   * for readability
   * 
   * @param <LS> 
   * @param directionAb the direction
   * @return link segment in given direction
   */
  @SuppressWarnings("unchecked")
  default <LS extends LinkSegment> LS getLinkSegment(boolean directionAb) {
    return (LS) getEdgeSegment(directionAb);
  }   
  
  /** collect edgeSegment Ab as something extending LinkSegment which is to be expected for any link. Convenience method
   * for readability
   * 
   * @param <LS> 
   * @return link segment in given direction
   */
  default <LS extends LinkSegment> LS getLinkSegmentAb() {
    return getLinkSegment(true);
  }   
  
  /** collect edgeSegment Ba as something extending LinkSegment which is to be expected for any link. Convenience method
   * for readability
   * 
   * @param <LS> 
   * @return link segment in given direction
   */
  default <LS extends LinkSegment> LS getLinkSegmentBa() {
    return getLinkSegment(false);
  }   

}
