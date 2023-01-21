package org.goplanit.utils.service.routed;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.network.layer.service.ServiceLegSegments;
import org.goplanit.utils.network.layer.service.ServiceNodes;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utilities for RelativeLegTimings
 *
 * @author markr
 */
public class RelativeLegTimingUtils {

  /**
   * Verify if leg timing is mapped to service network, i.e., its has service nodes that also exist on the service network
   * service nodes AND it has service leg segments that also exist on the service network leg segments
   *
   * @param relativeLegTiming to verify
   * @param legSegments of the service network
   * @param serviceNodes of the service network
   * @return true when mapped and references exist, false otherwise
   */
  public static boolean isLegTimingMappedToServiceNetwork(
          RelativeLegTiming relativeLegTiming, ServiceLegSegments legSegments, ServiceNodes serviceNodes) {

    return relativeLegTiming.hasParentLegSegment() && legSegments.hasServiceLegSegment(relativeLegTiming.getParentLegSegment())
            &&
            relativeLegTiming.getParentLegSegment().hasParent()
            &&
            relativeLegTiming.getParentLegSegment().getParent().hasVertices()
            &&
            serviceNodes.hasServiceNode(relativeLegTiming.getParentLegSegment().getUpstreamServiceNode())
            &&
            serviceNodes.hasServiceNode(relativeLegTiming.getParentLegSegment().getDownstreamServiceNode());
  }

  /**
   * find the first valid relative timing leg using a given offset index. Valid here means mapped to a service network AND
   * the reference to the service network used still exists in that service network (having a reference itself does not suffice)
   *
   * @param offsetLegIndex      offset, use this leg as starting point for search (inclusive), must be zero or higher
   * @param routedTripSchedule  containing the relative leg timings
   * @param routedServiceLayer to source service network from
   * @return found first next available timing index for which the service network contains the referenced leg(segment),
   * if no valid ones can be found the return value exceeds the size of the rel timings
   */
  public static int findNextTimingLegMappedToServiceNetwork(final int offsetLegIndex, final RoutedTripSchedule routedTripSchedule, RoutedServicesLayer routedServiceLayer) {
    final int relTimingSize = routedTripSchedule.getRelativeLegTimingsSize();
    if(offsetLegIndex < 0 || offsetLegIndex >= relTimingSize){
      throw new PlanItRunTimeException("provided invalid index for relative timing search offset");
    }

    var serviceNodes = routedServiceLayer.getParentLayer().getServiceNodes();
    var legSegments = routedServiceLayer.getParentLayer().getLegSegments();

    int currLegTimingIndex = offsetLegIndex -1;
    boolean isInvalid = true;
    RelativeLegTiming currLegtiming = null;
    while(isInvalid && ++currLegTimingIndex < relTimingSize) {
      currLegtiming = routedTripSchedule.getRelativeLegTiming(currLegTimingIndex);
      isInvalid = !RelativeLegTimingUtils.isLegTimingMappedToServiceNetwork(currLegtiming, legSegments, serviceNodes);
    }
    return currLegTimingIndex;
  }

  /**
   * find list of ordered indices of leg timings that are not (fully) mapped to the reference service network, or they are mapped
   * but the references do not exist in the related service network
   *
   * @param routedTripSchedule to collect relative leg timings from
   * @param routedServicesLayer to compare against, i.e., source the service network from
   * @return list of ascending indices of invalid relative leg timings
   */
  public static List<Integer> findLegTimingsNotMappedToServiceNetwork(
          RoutedTripSchedule routedTripSchedule, RoutedServicesLayer routedServicesLayer) {
    // find first valid service node from given reference point (including reference point check which is assumed valid)
    int currValidLegIndex = findNextTimingLegMappedToServiceNetwork(0, routedTripSchedule, routedServicesLayer);
    if(!routedTripSchedule.isValidRelativeLegTimingsIndex(currValidLegIndex)){
      return IntStream.range(0, routedTripSchedule.getLastRelativeLegTimingIndex()).boxed().collect(Collectors.toList());
    }

    /* at least some portion remains, remove portions in between/front/back where needed */
    List<Integer> toBeRemovedLegSegments = null;
    int nextValidLegIndex = -1;
    final int maxLegIndex = routedTripSchedule.getRelativeLegTimingsSize();
    do{

      // find last consecutive valid service node from given reference point (excluding reference point check which is assumed valid)
      nextValidLegIndex = findNextTimingLegMappedToServiceNetwork(currValidLegIndex+1, routedTripSchedule, routedServicesLayer);

      /* update to be removed leg segments based on invalid leg timings found in between valid ones */
      if(currValidLegIndex+1 != nextValidLegIndex) {
        var toBeRemovedLegSegmentPartialList =
                IntStream.range(currValidLegIndex + 1, Math.min(maxLegIndex,nextValidLegIndex - 1)).boxed().collect(Collectors.toList());
        if (toBeRemovedLegSegments == null) {
          toBeRemovedLegSegments = toBeRemovedLegSegmentPartialList;
        } else {
          toBeRemovedLegSegments.addAll(toBeRemovedLegSegmentPartialList);
        }
      }

      currValidLegIndex = nextValidLegIndex;
    }while(routedTripSchedule.isValidRelativeLegTimingsIndex(nextValidLegIndex));

    return toBeRemovedLegSegments;
  }
}
