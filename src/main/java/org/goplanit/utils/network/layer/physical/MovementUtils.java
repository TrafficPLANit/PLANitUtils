package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.network.layers.UntypedPhysicalNetworkLayers;
import org.goplanit.utils.zoning.TransferZone;
import org.goplanit.utils.zoning.TransferZoneGroup;

import java.util.*;
import java.util.function.Function;

public class MovementUtils {

  /**
   * Builds a fully array-based compiled relation index:
   * incoming segment -> (outgoing segment + movement id) where we exclude banned movements and u-turns
   */
  public static CompiledRelationIndex createCompiledMovementIndices(
      UntypedPhysicalNetworkLayers<?> layers,
      Movements movements) {

    // ------------------------------------------------------------
    // Step 1: banned movements (original style, no streams)
    // ------------------------------------------------------------
    Map<EdgeSegment, List<EdgeSegment>> bannedByEntryExit = new HashMap<>();

    if (movements != null) {
      for (Movement m : movements) {

        if (!m.isBanned()) {
          continue;
        }

        EdgeSegment from = m.getSegmentFrom();
        EdgeSegment to = m.getSegmentTo();

        bannedByEntryExit
            .computeIfAbsent(from, k -> new java.util.ArrayList<>())
            .add(to);
      }
    }

    // ------------------------------------------------------------
    // PASS 1: find max segment id + count valid transitions
    // ------------------------------------------------------------
    int maxSegmentId = 0;
    for (var layer : layers) {
      for (var segment : layer.getLinkSegments()) {
        maxSegmentId = Math.max(maxSegmentId, (int) segment.getId());
      }
    }

    int[] numExitsPerIncomingSegment = new int[maxSegmentId + 1];
    for (var layer : layers) {
      for (var entry : layer.getLinkSegments()) {
        List<EdgeSegment> bannedOut = bannedByEntryExit.get(entry);
        for (var exit : entry.getDownstreamVertex().getExitEdgeSegments()) {
          if (entry.equals(exit)) {
            continue;
          }

          if (bannedOut != null && bannedOut.contains(exit)) {
            continue;
          }

          numExitsPerIncomingSegment[(int) entry.getId()]++;
        }
      }
    }

    // ------------------------------------------------------------
    // Allocate exact arrays
    // ------------------------------------------------------------
    long[][] outgoingByIn = new long[maxSegmentId + 1][];
    long[][] movementByIn = new long[maxSegmentId + 1][];
    for (int i = 0; i <= maxSegmentId; i++) {
      if (numExitsPerIncomingSegment[i] > 0) {
        outgoingByIn[i] = new long[numExitsPerIncomingSegment[i]];
        movementByIn[i] = new long[numExitsPerIncomingSegment[i]];
      }
    }

    // ------------------------------------------------------------
    // PASS 2: fill arrays
    // ------------------------------------------------------------
    int[] cursor = new int[maxSegmentId + 1];
    long nextMovementId = 0;

    for (var layer : layers) {
      for (var entry : layer.getLinkSegments()) {
        var inId = (int) entry.getId();
        List<EdgeSegment> bannedOut = bannedByEntryExit.get(entry);
        for (var exit : entry.getDownstreamVertex().getExitEdgeSegments()) {
          if (entry.equals(exit)) {
            continue;
          }

          if (bannedOut != null && bannedOut.contains(exit)) {
            continue;
          }

          int pos = cursor[inId]++;
          outgoingByIn[inId][pos] = exit.getId();
          movementByIn[inId][pos] = nextMovementId++;
        }
      }
    }

    return new CompiledRelationIndex(
        outgoingByIn,
        movementByIn,
        nextMovementId
    );
  }

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