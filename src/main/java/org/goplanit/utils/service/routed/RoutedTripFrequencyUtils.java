package org.goplanit.utils.service.routed;

import org.goplanit.utils.network.layer.ServiceNetworkLayer;
import org.goplanit.utils.network.layer.service.ServiceLegSegment;
import org.goplanit.utils.network.layer.service.ServiceLegSegmentUtils;
import org.goplanit.utils.network.layer.service.ServiceLegSegments;
import org.goplanit.utils.network.layer.service.ServiceNodes;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilities for RoutedTripFrequency class
 *
 * @author markr
 */
public class RoutedTripFrequencyUtils {

  /**
   * Verify if all the routed frequency's leg segments are mapped to the service network, i.e., its service leg segments and their service nodes
   * also exist on the service network containers. Can be used to identify invalidated routed trip frequensies in
   * case service network has been modified and routed trip frequencies were not simultaneously updated (for some reason).
   *
   * @param routedTripFrequency to verify
   * @param serviceNetworkLayer the service network to check against
   * @return true when mapped service network references exist and are valid, false otherwise
   */
  public static boolean isRoutedTripFrequencyMappedToServiceNetwork(
          RoutedTripFrequency routedTripFrequency, ServiceNetworkLayer serviceNetworkLayer) {

    boolean isMapped = routedTripFrequency.hasLegSegments();
    if (!isMapped) {
      return false;
    }

    for (var serviceLegSegment : routedTripFrequency) {
      isMapped = ServiceLegSegmentUtils.isMappedToServiceNetwork(serviceLegSegment, serviceNetworkLayer);
      if (!isMapped) {
        break;
      }
    }

    return isMapped;
  }

  /**
   * Identify which service leg segments that make up the routed trip frequency are in fact not valid anymore due to
   * being absent in the provided service network
   *
   * @param routedTripFrequency to check
   * @param serviceNetworkLayer to use for cross-referencing validity of mapping
   * @return ascending ordered list of service leg segment indices that are not present in the service network
   */
  public static List<Integer> findServiceLegSegmentsNotMappedToServiceNetwork(RoutedTripFrequency routedTripFrequency, ServiceNetworkLayer serviceNetworkLayer) {
    List result = null;
    for(int index = 0 ; index < routedTripFrequency.getNumberOfLegSegments() ; ++index){
      if(!ServiceLegSegmentUtils.isMappedToServiceNetwork(routedTripFrequency.getLegSegment(index), serviceNetworkLayer)){
        if(result == null){
          result = new ArrayList<ServiceLegSegment>(5);
        }
        result.add(index);
      }
    }
    return result;
  }
}
