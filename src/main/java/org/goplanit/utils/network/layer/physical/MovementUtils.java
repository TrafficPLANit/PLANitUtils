package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.network.layers.UntypedPhysicalNetworkLayers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builds a fully array-based compiled relation index:
 * incoming segment -> (outgoing segment + movement id)
 */
public class MovementUtils {

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
        Math.max(maxSegmentId, (int) segment.getId());
      }
    }

    int[] numExitsPerIncomingSegment = new int[maxSegmentId + 1];
    for (var layer : layers) {
      for (var node : layer.getNodes()) {
        for (var in : node.getEntryLinkSegments()) {

          int inId = (int) in.getId();
          List<EdgeSegment> bannedOut = bannedByEntryExit.get(in);

          for (var out : node.getExitLinkSegments()) {

            if (in.equals(out)) {
              continue;
            }

            if (bannedOut != null && bannedOut.contains(out)) {
              continue;
            }

            numExitsPerIncomingSegment[inId]++;
          }
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
      for (var node : layer.getNodes()) {

        for (var in : node.getEntryLinkSegments()) {

          int inId = (int) in.getId();
          List<EdgeSegment> bannedOut = bannedByEntryExit.get(in);

          for (var out : node.getExitLinkSegments()) {

            if (in.equals(out)) {
              continue;
            }

            if (bannedOut != null && bannedOut.contains(out)) {
              continue;
            }

            int pos = cursor[inId]++;
            outgoingByIn[inId][pos] = out.getId();
            movementByIn[inId][pos] = nextMovementId++;
          }
        }
      }
    }

    return new CompiledRelationIndex(
        outgoingByIn,
        movementByIn,
        nextMovementId
    );
  }
}