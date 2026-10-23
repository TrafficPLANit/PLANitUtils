package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.directed.BannedTurn;
import org.goplanit.utils.graph.directed.BannedTurns;
import org.goplanit.utils.graph.directed.DirectedEdge;

import java.util.*;
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
}