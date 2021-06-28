package org.planit.utils.network.service;

import org.planit.utils.graph.EdgeSegment;

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
  public abstract ServiceLeg getParentLeg();

}
