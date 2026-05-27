package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.network.layers.UntypedPhysicalNetworkLayers;
import org.goplanit.utils.zoning.TransferZone;
import org.goplanit.utils.zoning.TransferZoneGroup;

import java.util.*;
import java.util.function.Function;

public class MovementUtils {

  /**
   * For the given mapping replace all segment from and to references on movements based on the new mapping
   * @param movements to update
   * @param segmentToSegmentMapping mapping to use
   * @param removeMissingMappings when true remove movement from container if no mapping exists
   * @param <T> type of segment
   */
  public static <T extends EdgeSegment> void updateMovementSegmentMapping(
      Movements movements, Function<T, T> segmentToSegmentMapping, boolean removeMissingMappings) {

    Set<Movement> toRemove = new TreeSet<>();
    for(var movement :  movements){
      if(movement.hasSegmentFrom()) {
        var newSegment = segmentToSegmentMapping.apply((T) movement.getSegmentFrom());
        if(newSegment != null) {
          movement.setSegmentFrom(newSegment);
        }else{
          toRemove.add(movement);
        }
      }
      if(movement.hasSegmentTo()) {
        var newSegment = segmentToSegmentMapping.apply((T) movement.getSegmentTo());
        if(newSegment != null) {
          movement.setSegmentTo(newSegment);
        }else{
          toRemove.add(movement);
        }
      }
    }

    toRemove.forEach(movements::remove);
  }
}