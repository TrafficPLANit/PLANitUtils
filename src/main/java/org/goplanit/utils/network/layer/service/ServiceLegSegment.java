package org.goplanit.utils.network.layer.service;

import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.graph.directed.EdgeSegment;

/**
 * Interface for directed ServiceLegSegment part of non-directional ServiceLeg.
 * 
 * @author markr
 *
 */
public interface ServiceLegSegment extends EdgeSegment {
  
  /**
   * Return the parent leg of this leg segment
   * 
   * @return leg instance which is the parent of this leg segment
   */
  @Override
  public ServiceLeg getParent();

  /**
   * Get the segment's upstream service node
   *
   * @return upstream vertex
   */
  public default ServiceNode getUpstreamServiceNode() {
    return isDirectionAb() ? getParent().getServiceNodeA() : getParent().getServiceNodeB();
  }

  /**
   * Get the segment's downstream vertex
   *
   * @return downstream service node
   */
  public default ServiceNode getDownstreamServiceNode() {
    return isDirectionAb() ? getParent().getServiceNodeB() : getParent().getServiceNodeA();
  }

}
