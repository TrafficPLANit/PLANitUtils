package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.directed.BannedTurn;
import org.goplanit.utils.graph.directed.BannedTurns;
import org.goplanit.utils.graph.directed.DirectedEdge;
import org.goplanit.utils.graph.directed.EdgeSegment;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utilities for banned turns
 *
 * @author markr
 */
public class BannedTurnUtils {

  /** static utility class, not to be instantiated */
  private BannedTurnUtils() {}

  /**
   * Collect the banned turns starting or ending at one of the edge's segments, through the container's lookup by
   * segment
   *
   * @param bannedTurns to look in
   * @param edge whose segments to look up
   * @return banned turns on the edge's segments, each once
   */
  public static List<BannedTurn> findBannedTurnsOnEdge(BannedTurns bannedTurns, DirectedEdge edge) {
    if (!edge.hasEdgeSegment()) {
      return List.of();
    }
    return edge.getEdgeSegments().stream().flatMap(segment -> bannedTurns.getBySegment(segment).stream())
        .distinct().collect(Collectors.toList());
  }

  /**
   * For the given mapping replace all segment from and to references on banned turns based on the new mapping
   * @param bannedTurns to update
   * @param segmentToSegmentMapping mapping to use
   * @param removeMissingMappings when true remove banned turn from container if no mapping exists
   * @param <T> type of segment
   */
  public static <T extends EdgeSegment> void updateBannedTurnSegmentMapping(
      BannedTurns bannedTurns, Function<T, T> segmentToSegmentMapping, boolean removeMissingMappings) {

    Set<BannedTurn> toRemove = new TreeSet<>();
    for(var bannedTurn : bannedTurns){
      if(bannedTurn.hasSegmentFrom()) {
        var newSegment = segmentToSegmentMapping.apply((T) bannedTurn.getSegmentFrom());
        if(newSegment != null) {
          bannedTurn.setSegmentFrom(newSegment);
        }else{
          toRemove.add(bannedTurn);
        }
      }
      if(bannedTurn.hasSegmentTo()) {
        var newSegment = segmentToSegmentMapping.apply((T) bannedTurn.getSegmentTo());
        if(newSegment != null) {
          bannedTurn.setSegmentTo(newSegment);
        }else{
          toRemove.add(bannedTurn);
        }
      }
    }

    toRemove.forEach(bannedTurns::remove);
  }
}