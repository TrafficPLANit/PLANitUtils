package org.goplanit.utils.network.layer.macroscopic.intersection;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.goplanit.utils.graph.directed.BannedMovement;
import org.goplanit.utils.graph.directed.BannedMovements;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;
import org.goplanit.utils.network.layer.physical.Node;

/**
 * Utilities for intersections
 *
 * @author markr
 *
 */
public class IntersectionUtils {

  /** static utility class, not to be instantiated */
  private IntersectionUtils() {
  }

  /**
   * Find every node that is a member of more than one of the given intersections, which makes the layer invalid. Scans
   * all intersections rather than using the container's lookup, since that lookup holds only one intersection per node.
   *
   * @param intersections to check
   * @return intersections per node found in more than one, empty when none
   */
  public static Map<Node, List<Intersection>> findNodesInMoreThanOneIntersection(Intersections intersections){
    Map<Node, List<Intersection>> intersectionsByNode = new IdentityHashMap<>();
    for(var intersection : intersections){
      for(var node : intersection.getMemberNodes()){
        intersectionsByNode.computeIfAbsent(node, n -> new ArrayList<>(1)).add(intersection);
      }
    }
    intersectionsByNode.values().removeIf(list -> list.size() < 2);
    return intersectionsByNode;
  }

  /**
   * Find the banned movements of each approach segment: the bans whose from segment it is. Turns at an intersection are
   * restricted by the layer's banned movements only, so this is how the two are combined
   *
   * @param intersections to find the approach segments of
   * @param bannedMovements of the same layer
   * @return banned movements per approach segment, by identity; approach segments without bans are absent
   */
  public static Map<MacroscopicLinkSegment, List<BannedMovement>> findBannedMovementsByApproachSegment(
      Intersections intersections, BannedMovements bannedMovements){
    var bansByFromSegment = groupBansByFromSegment(bannedMovements);
    Map<MacroscopicLinkSegment, List<BannedMovement>> bansByApproachSegment = new IdentityHashMap<>();
    for(var intersection : intersections){
      for(var approachSegment : intersection.getApproachSegments()){
        var bans = bansByFromSegment.get(approachSegment);
        if(bans != null){
          bansByApproachSegment.put(approachSegment, new ArrayList<>(bans));
        }
      }
    }
    return bansByApproachSegment;
  }

  /**
   * Find the banned movements of each intersection: the bans of all its approach segments, as in
   * {@link #findBannedMovementsByApproachSegment(Intersections, BannedMovements)}
   *
   * @param intersections to find the bans of
   * @param bannedMovements of the same layer
   * @return banned movements per intersection, by identity; intersections without bans are absent
   */
  public static Map<Intersection, List<BannedMovement>> findBannedMovementsByIntersection(
      Intersections intersections, BannedMovements bannedMovements){
    var bansByFromSegment = groupBansByFromSegment(bannedMovements);
    Map<Intersection, List<BannedMovement>> bansByIntersection = new IdentityHashMap<>();
    for(var intersection : intersections){
      for(var approachSegment : intersection.getApproachSegments()){
        var bans = bansByFromSegment.get(approachSegment);
        if(bans != null){
          bansByIntersection.computeIfAbsent(intersection, i -> new ArrayList<>()).addAll(bans);
        }
      }
    }
    return bansByIntersection;
  }

  /**
   * Group banned movements by their from segment, by identity since segments hash by id and ids can be recreated
   *
   * @param bannedMovements to group
   * @return banned movements per from segment
   */
  private static Map<EdgeSegment, List<BannedMovement>> groupBansByFromSegment(BannedMovements bannedMovements){
    Map<EdgeSegment, List<BannedMovement>> bansByFromSegment = new IdentityHashMap<>();
    for(var ban : bannedMovements){
      if(ban.hasSegmentFrom()){
        bansByFromSegment.computeIfAbsent(ban.getSegmentFrom(), s -> new ArrayList<>(1)).add(ban);
      }
    }
    return bansByFromSegment;
  }
}
