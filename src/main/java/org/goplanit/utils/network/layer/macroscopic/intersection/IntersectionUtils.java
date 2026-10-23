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
   * Verify if a link segment enters an intersection from outside without being one of its approaches: it ends at a
   * member node and starts at a node that is not one. Such a segment can become an approach
   *
   * @param intersection to verify it for
   * @param segment to verify
   * @return true when it enters from outside and is not an approach, false otherwise
   */
  public static boolean isSegmentEnteringFromOutsideThatIsNotAnApproach(
      Intersection intersection, MacroscopicLinkSegment segment) {
    return intersection.hasMemberNode(segment.getDownstreamVertex())
        && !intersection.hasMemberNode(segment.getUpstreamVertex()) && !intersection.isApproachSegment(segment);
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
          isSegmentEnteringFromOutsideThatIsNotAnApproach(intersection, segment) && condition.test(segment)));
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
   * Copy the values of one intersection segment instance to another, each with how it got its value: the saturation
   * flow factor, and the green ratio when both are signal segments. A value not set on the first is left as it is on
   * the second
   *
   * @param from the intersection segment to copy them from
   * @param to the intersection segment to copy them to
   */
  public static void copyGreenRatioAndSaturationFlowFactor(IntersectionSegment from, IntersectionSegment to) {
    if (from.getSaturationFlowFactorOrigin() != null) {
      to.setSaturationFlowFactor(from.getSaturationFlowFactor(), from.getSaturationFlowFactorOrigin());
    }
    if (from instanceof IntersectionSignalSegment && to instanceof IntersectionSignalSegment) {
      var fromSignal = (IntersectionSignalSegment) from;
      if (fromSignal.getGreenRatioOrigin() != null) {
        ((IntersectionSignalSegment) to).setGreenRatio(fromSignal.getGreenRatio(), fromSignal.getGreenRatioOrigin());
      }
    }
  }

  /**
   * Add to an intersection a copy of an approach of another intersection: a new approach instance for the same link
   * segment, with a signal of its own when the approach has one, and the approach's values copied to it. The approach
   * given stays with its own intersection. For example when intersections are joined into one, or a spatial
   * intersection is replaced by a non-spatial one
   *
   * @param to the intersection to add the copy to
   * @param approach of another intersection, to copy
   * @return the copy added, null when the intersection refuses its link segment as an approach segment
   */
  public static IntersectionSegment addCopyOfApproach(Intersection to, IntersectionSegment approach) {
    var added = to.addNewApproach(approach.getLinkSegment(), approach.hasSignal());
    if (added != null) {
      copyGreenRatioAndSaturationFlowFactor(approach, added);
    }
    return added;
  }

  /**
   * Verify a value can be a green ratio or a saturation flow factor: from 0 to 1, both included
   *
   * @param value to verify
   * @return true when from 0 to 1, false otherwise, also when it is not a number
   */
  public static boolean isValidCapacityFactor(double value) {
    return value >= 0 && value <= 1;
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
