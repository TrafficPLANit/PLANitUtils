package org.goplanit.utils.network.layer.macroscopic.intersection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.goplanit.utils.graph.directed.BannedTurn;
import org.goplanit.utils.graph.directed.BannedTurns;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;
import org.goplanit.utils.network.layer.physical.Node;
import org.goplanit.utils.network.layer.physical.NodeUtils;

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
   * The internal segments of an intersection of either kind: those of a spatial intersection, none for a
   * non-spatial one, which is a single node
   *
   * @param intersection to collect them of
   * @return its internal segments, unmodifiable, empty for a non-spatial intersection
   */
  public static List<MacroscopicLinkSegment> getInternalSegmentsOf(Intersection intersection){
    return intersection instanceof SpatialIntersection ?
        ((SpatialIntersection) intersection).getInternalSegments() : List.of();
  }

  /**
   * Find the link segments that enter an intersection from outside, ending at a member node and starting at a node that
   * is not one, without being one of its approaches, and that meet a condition
   *
   * @param intersection to find them for
   * @param condition to meet, such as allowing a motorised road mode
   * @return the segments found, by member node in the order of the member nodes, empty when there are none
   */
  public static List<MacroscopicLinkSegment> findSegmentsEnteringFromOutsideThatAreNotApproaches(
      Intersection intersection, Predicate<MacroscopicLinkSegment> condition) {
    var entering = new ArrayList<MacroscopicLinkSegment>();
    for (var memberNode : intersection.getMemberNodes()) {
      entering.addAll(NodeUtils.<MacroscopicLinkSegment>findEntryLinkSegments(memberNode, segment ->
          !intersection.hasMemberNode(segment.getUpstreamVertex()) && !intersection.isApproachSegment(segment) &&
              condition.test(segment)));
    }
    return entering;
  }

  /**
   * Collect the member nodes of the given intersections
   *
   * @param intersections to collect the member nodes of
   * @return their member nodes, each once
   */
  public static Set<Node> collectMemberNodes(Collection<? extends Intersection> intersections){
    return intersections.stream().flatMap(intersection -> intersection.getMemberNodes().stream())
        .collect(Collectors.toSet());
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
   * Find the banned turns of each approach segment: the bans whose from segment it is. Turns at an intersection are
   * restricted by the layer's banned turns only, so this is how the two are combined
   *
   * @param intersections to find the approach segments of
   * @param bannedTurns of the same layer
   * @return banned turns per approach segment, by identity; approach segments without bans are absent
   */
  public static Map<MacroscopicLinkSegment, List<BannedTurn>> findBannedTurnsByApproachSegment(
      Intersections intersections, BannedTurns bannedTurns){
    var bansByFromSegment = groupBansByFromSegment(bannedTurns);
    Map<MacroscopicLinkSegment, List<BannedTurn>> bansByApproachSegment = new IdentityHashMap<>();
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
   * Find the banned turns of each intersection: the bans of all its approach segments, as in
   * {@link #findBannedTurnsByApproachSegment(Intersections, BannedTurns)}
   *
   * @param intersections to find the bans of
   * @param bannedTurns of the same layer
   * @return banned turns per intersection, by identity; intersections without bans are absent
   */
  public static Map<Intersection, List<BannedTurn>> findBannedTurnsByIntersection(
      Intersections intersections, BannedTurns bannedTurns){
    var bansByFromSegment = groupBansByFromSegment(bannedTurns);
    Map<Intersection, List<BannedTurn>> bansByIntersection = new IdentityHashMap<>();
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
   * Group banned turns by their from segment, by identity since segments hash by id and ids can be recreated
   *
   * @param bannedTurns to group
   * @return banned turns per from segment
   */
  private static Map<EdgeSegment, List<BannedTurn>> groupBansByFromSegment(BannedTurns bannedTurns){
    Map<EdgeSegment, List<BannedTurn>> bansByFromSegment = new IdentityHashMap<>();
    for(var ban : bannedTurns){
      if(ban.hasSegmentFrom()){
        bansByFromSegment.computeIfAbsent(ban.getSegmentFrom(), s -> new ArrayList<>(1)).add(ban);
      }
    }
    return bansByFromSegment;
  }
}
