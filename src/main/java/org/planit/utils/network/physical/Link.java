package org.planit.utils.network.physical;

import org.opengis.geometry.coordinate.LineString;
import org.planit.utils.graph.Edge;

/**
 * Link interface which extends the Edge interface with a unique id (not all edges are links) as
 * well as an external id
 * 
 * @author markr
 *
 */
public interface Link extends Edge {

  /**
   * Return id of this instance. This id is expected to be generated using the
   * org.planit.utils.misc.IdGenerator
   * 
   * @return linkId
   */
  long getLinkId();

  /**
   * Set the external id
   * 
   * @param externalId the external id to set
   */
  void setExternalId(final Object externalId);   

  /**
   * Collect the external id
   * 
   * @return externalID
   */
  Object getExternalId();

  /**
   * Returns whether the external Id has been set
   * 
   * @return true if the external Id has been set, false otherwise
   */
  boolean hasExternalId();
    
  
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
  default <N extends Node> Node getNodeA() {
    return (N) getVertexA();
  }
  
  /** collect vertex A as something extending node which is to be expected for any link. Convenience method
   * for readability
   * 
   * @param <N>
   * @return nodeA
   */
  @SuppressWarnings("unchecked")
  default <N extends Node> Node getNodeB() {
    return (N) getVertexB();
  }  

}
