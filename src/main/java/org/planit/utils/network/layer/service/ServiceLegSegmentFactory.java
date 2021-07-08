package org.planit.utils.network.layer.service;

import org.planit.utils.graph.GraphEntityFactory;

/** Factory interface for creating service leg segment instances
 * 
 * @author markr
 *
 */
public interface ServiceLegSegmentFactory extends GraphEntityFactory<ServiceLegSegment> {

  /** create a new service leg segment and register it on the underlying container
   * 
   * @param parentLeg of the segment
   * @param directionAB direction of the segment
   * @return created segment
   */
  public abstract ServiceLegSegment registerNew(final ServiceLeg parentLeg, boolean directionAB);
  
}
