package org.goplanit.utils.network.layer.service;

import org.goplanit.utils.graph.GraphEntityFactory;

/** Factory interface for creating service leg segment instances
 * 
 * @author markr
 *
 */
public interface ServiceLegSegmentFactory extends GraphEntityFactory<ServiceLegSegment> {

  /** create a new service leg segment and register it on the underlying container (without registering on service node and leg)
   * 
   * @param parentLeg of the segment
   * @param directionAb direction of the segment
   * @return created segment
   */
  public default ServiceLegSegment registerNew(final ServiceLeg parentLeg, boolean directionAb) {
    return registerNew(parentLeg, directionAb, false);
  }

  /** create a new service leg segment and register it on the underlying container and allow the user to let the factory register
   * the newly create segment on both the parent leg and service nodes in the correct direction if desired 
   * 
   * @param parentLeg of the segment
   * @param directionAb direction of the segment
   * @return created segment
   */  
  ServiceLegSegment registerNew(ServiceLeg parentLeg, boolean directionAb, boolean registerOnServiceNodeAndLeg);
  
}
