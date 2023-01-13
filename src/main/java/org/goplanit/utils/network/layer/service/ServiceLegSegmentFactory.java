package org.goplanit.utils.network.layer.service;

import org.goplanit.utils.graph.GraphEntityFactory;
import org.goplanit.utils.network.layer.physical.Link;
import org.goplanit.utils.network.layer.physical.LinkSegment;
import org.goplanit.utils.network.layer.physical.LinkSegments;

import java.util.List;

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
   * @param registerOnServiceNodeAndLeg flag indicating whether or not to regsiter the created leg segment on node and leg
   * @return created segment
   */  
  ServiceLegSegment registerNew(ServiceLeg parentLeg, boolean directionAb, boolean registerOnServiceNodeAndLeg);


  /** create a new service leg segment and register it on the underlying container and allow the user to let the factory register
   * the newly create segment on both the parent leg and service nodes in the correct direction if desired
   *
   * @param parentLeg of the segment
   * @param directionAb direction of the segment
   * @param networkLayerLinkSegments  the underlying parent link segments that make up this leg
   * @param registerOnServiceNodeAndLeg flag indicating whether to register the created leg segment on node and leg
   * @return created segment
   */
  ServiceLegSegment registerNew(ServiceLeg parentLeg, boolean directionAb, final List<LinkSegment> networkLayerLinkSegments, boolean registerOnServiceNodeAndLeg);
}
