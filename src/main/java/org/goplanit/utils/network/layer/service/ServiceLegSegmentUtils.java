package org.goplanit.utils.network.layer.service;

import org.goplanit.utils.network.layer.ServiceNetworkLayer;
import org.goplanit.utils.service.routed.RoutedServicesLayer;

/**
 * Utilities for RoutedTripFrequency class
 *
 * @author markr
 */
public class ServiceLegSegmentUtils {

  /**
   * Verify if service leg segment is present in the service network layer, i.e., it is present on the service network containers., and its nodes
   * are present on the service node contains.
   *
   * @param serviceLegSegment to verify
   * @param serviceNetworkLayer to check against
   * @return true when mapped service network references exist and are valid, false otherwise
   */
  public static boolean isMappedToServiceNetwork(ServiceLegSegment serviceLegSegment, ServiceNetworkLayer serviceNetworkLayer) {
    return  serviceNetworkLayer.getLegSegments().hasServiceLegSegment(serviceLegSegment)
            &&
            serviceLegSegment.hasParent()
            &&
            serviceLegSegment.getParent().hasVertices()
            &&
            serviceNetworkLayer.getServiceNodes().hasServiceNode(serviceLegSegment.getUpstreamServiceNode())
            &&
            serviceNetworkLayer.getServiceNodes().hasServiceNode(serviceLegSegment.getDownstreamServiceNode());
  }
}
