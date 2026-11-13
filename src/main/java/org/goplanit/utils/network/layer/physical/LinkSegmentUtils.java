package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.geo.PlanitJtsCrsUtils;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.math.Precision;
import org.goplanit.utils.mode.Mode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * Utilities for link segments
 *
 * @author markr
 *
 */
public class LinkSegmentUtils {

  /**
   * Create a predicate selecting only those edge segments that are link segments permitting the given mode.
   * <p>
   * Shaped as a {@code Predicate<EdgeSegment>} so it can be handed straight to the graph level algorithms, which know
   * nothing about modes, since an edge segment carries no mode information at all. Mode access is a network layer
   * concept, and a predicate is the seam through which it reaches a graph algorithm without that algorithm having to
   * learn about it. Any segment that is not a link segment is rejected.
   * </p>
   * <p>
   * Useful for asking connectivity questions per mode, e.g. whether the car network is strongly connected, which
   * is a different question from whether the infrastructure carrying it is.
   * </p>
   *
   * @param mode the mode that must be permitted
   * @return predicate to use
   */
  public static Predicate<EdgeSegment> permitsMode(final Mode mode) {
    return edgeSegment -> edgeSegment instanceof LinkSegment && ((LinkSegment) edgeSegment).isModeAllowed(mode);
  }

  /**
   * Create a predicate selecting only those edge segments that are link segments permitting at least one motorised
   * road mode, such as a car or a bus, see {@link Mode#isMotorisedRoadMode()}. Shaped as a
   * {@code Predicate<EdgeSegment>} for the same reason as {@link #permitsMode(Mode)}.
   *
   * @return predicate to use
   */
  public static Predicate<EdgeSegment> permitsMotorisedRoadMode() {
    return edgeSegment -> edgeSegment instanceof LinkSegment &&
        ((LinkSegment) edgeSegment).isAnyModeAllowed(Mode::isMotorisedRoadMode);
  }

  /**
   * Find the link segments a segment continues on beyond the node it arrives at. They leave that node, meet the
   * condition, and are not on the link arrived on, so going back is never a continuation. For example, arriving at a
   * node where a road splits in two, both roads ahead are continuations, but the opposite direction of the road arrived
   * along is not.
   *
   * @param <LS> type of the link segments
   * @param arriving segment arriving at the node
   * @param condition a continuation must meet
   * @return the continuations, empty when there are none
   */
  public static <LS extends LinkSegment> List<LS> findContinuations(
      final LS arriving, final Predicate<? super LS> condition) {
    var continuations = new ArrayList<LS>(2);
    for (LS leaving : arriving.getDownstreamNode().<LS>getExitLinkSegments()) {
      if (leaving.getParent() != arriving.getParent() && condition.test(leaving)) {
        continuations.add(leaving);
      }
    }
    return continuations;
  }

  /**
   * Find the link segments a segment can be reached from before the node it leaves. They enter that node, meet the
   * condition, and are not on the link it leaves along, so coming back along it is never a predecessor. For example,
   * leaving a node where two roads merge, both roads behind are predecessors, but the opposite direction of the road
   * left along is not.
   *
   * @param <LS> type of the link segments
   * @param leaving segment leaving the node
   * @param condition a predecessor must meet
   * @return the predecessors, empty when there are none
   */
  public static <LS extends LinkSegment> List<LS> findPredecessors(
      final LS leaving, final Predicate<? super LS> condition) {
    var predecessors = new ArrayList<LS>(2);
    for (LS entering : leaving.getUpstreamNode().<LS>getEntryLinkSegments()) {
      if (entering.getParent() != leaving.getParent() && condition.test(entering)) {
        predecessors.add(entering);
      }
    }
    return predecessors;
  }

  /**
   * Find the link segments onward from a segment in a direction: its continuations in the traffic direction, see
   * {@link #findContinuations}, or its predecessors against it, see {@link #findPredecessors}
   *
   * @param <LS> type of the link segments
   * @param segment to go on from
   * @param inTrafficDirection true to go in the traffic direction, false to go back against it
   * @param condition a segment onward must meet
   * @return the segments onward, empty when there are none
   */
  public static <LS extends LinkSegment> List<LS> findOnward(final LS segment,
      boolean inTrafficDirection, final Predicate<? super LS> condition) {
    return inTrafficDirection ? findContinuations(segment, condition) : findPredecessors(segment, condition);
  }

  /**
   * Find the only link segment onward from a segment in a direction, see {@link #findOnward}
   *
   * @param <LS> type of the link segments
   * @param segment to go on from
   * @param inTrafficDirection true to go in the traffic direction, false to go back against it
   * @param condition a segment onward must meet
   * @return the only segment onward, null when there are several or none
   */
  public static <LS extends LinkSegment> LS findOnlySegmentOnward(final LS segment,
      boolean inTrafficDirection, final Predicate<? super LS> condition) {
    var onward = findOnward(segment, inTrafficDirection, condition);
    return onward.size() == 1 ? onward.get(0) : null;
  }

  /**
   * Follow segments meeting a condition from a segment, through nodes where they simply continue: one segment onward
   * meets the condition, see {@link #findOnward}, and none comes back, other than on the link just followed. For
   * example, a road followed through the shape nodes of a long bend, up to the junction it reaches
   *
   * @param <LS> type of the link segments
   * @param segment to start along
   * @param inTrafficDirection true to go in the traffic direction, false to go back against it
   * @param maximumDistanceMeters furthest to follow, the first segment included
   * @param condition the segments followed must meet
   * @return the last segment followed, ending (or starting, against the traffic direction) at the node where the
   *         segments stop simply continuing; null when that node is not reached within the distance
   */
  public static <LS extends LinkSegment> LS followUntilItBranches(final LS segment,
      boolean inTrafficDirection, double maximumDistanceMeters, final Predicate<? super LS> condition) {
    return followOnlySegmentsOnwardUntilAnswered(segment, inTrafficDirection, segment.getParent().getLengthMeters(),
        maximumDistanceMeters, condition, (current, meters) -> {
          var node = inTrafficDirection ? current.getDownstreamNode() : current.getUpstreamNode();
          List<LS> back = inTrafficDirection ?
              NodeUtils.<LS>findEntryLinkSegments(node, condition) :
              NodeUtils.<LS>findExitLinkSegments(node, condition);
          back.removeIf(previous -> previous.getParent() == current.getParent());
          return findOnlySegmentOnward(current, inTrafficDirection, condition) == null || !back.isEmpty() ?
              current : null;
        });
  }

  /**
   * Follow segments from a segment one after the other, in a direction, until a rule gives an answer at a node reached.
   * Each segment followed reaches a node: its downstream node in the traffic direction, its upstream node against it.
   * At each, the rule is asked for an answer, and when it gives none, the next segment is asked for. It stops without
   * an answer when there is no next segment, when the node reached lies beyond the distance, or when it gets back to a
   * node already reached, as round a loop. For example, a road followed through the nodes where it simply continues,
   * with the answer the junction it reaches
   *
   * @param <LS> type of the link segments
   * @param <R> type of the answer
   * @param start segment to start along
   * @param inTrafficDirection true to go in the traffic direction, false to go back against it
   * @param metersToFirstNodeReached distance to the node the start segment reaches: its length, or less when starting
   *                                 part way along it
   * @param maximumDistanceMeters furthest a node reached may lie
   * @param answerAt given a segment followed and the distance to the node it reaches, the answer, null to go on
   * @param nextSegment given a segment followed without an answer, the segment to follow next, null when there is none
   * @return the first answer given, null when none was
   */
  public static <LS extends LinkSegment, R> R followSegmentsUntilAnswered(final LS start, boolean inTrafficDirection,
      double metersToFirstNodeReached, double maximumDistanceMeters,
      final BiFunction<? super LS, Double, ? extends R> answerAt, final UnaryOperator<LS> nextSegment) {
    var nodesReached = new HashSet<Node>();
    double meters = metersToFirstNodeReached;
    var current = start;
    while (current != null && meters <= maximumDistanceMeters + Precision.EPSILON_9) {
      if (!nodesReached.add(inTrafficDirection ? current.getDownstreamNode() : current.getUpstreamNode())) {
        return null;
      }
      R answer = answerAt.apply(current, meters);
      if (answer != null) {
        return answer;
      }
      current = nextSegment.apply(current);
      if (current != null) {
        meters += current.getParent().getLengthMeters();
      }
    }
    return null;
  }

  /**
   * Follow segments from a segment one after the other, in a direction, until a rule gives an answer at a node reached,
   * see {@link #followSegmentsUntilAnswered(LinkSegment, boolean, double, double, BiFunction, UnaryOperator)}. The
   * segment followed next is the only one onward that meets the condition, see {@link #findOnlySegmentOnward}, so it
   * also stops where none or more than one does
   *
   * @param <LS> type of the link segments
   * @param <R> type of the answer
   * @param start segment to start along
   * @param inTrafficDirection true to go in the traffic direction, false to go back against it
   * @param metersToFirstNodeReached distance to the node the start segment reaches: its length, or less when starting
   *                                 part way along it
   * @param maximumDistanceMeters furthest a node reached may lie
   * @param condition the segments followed after the start segment must meet
   * @param answerAt given a segment followed and the distance to the node it reaches, the answer, null to go on
   * @return the first answer given, null when none was
   */
  public static <LS extends LinkSegment, R> R followOnlySegmentsOnwardUntilAnswered(final LS start,
      boolean inTrafficDirection, double metersToFirstNodeReached, double maximumDistanceMeters,
      final Predicate<? super LS> condition, final BiFunction<? super LS, Double, ? extends R> answerAt) {
    return followSegmentsUntilAnswered(start, inTrafficDirection, metersToFirstNodeReached, maximumDistanceMeters,
        answerAt, current -> findOnlySegmentOnward(current, inTrafficDirection, condition));
  }

  /**
   * The direction of travel along a link segment into its downstream node, over the last stretch before the node, see
   * {@link LinkUtils#getAzimuthArrivingAtNodeInDegrees(Link, Node, double, PlanitJtsCrsUtils)}
   *
   * @param segment to measure along
   * @param reachMeters how far back from its downstream node, along its link, shape nodes are considered, 0 or more
   * @param crsUtils to measure distances and directions with
   * @return the compass bearing of the direction of travel into its downstream node, in degrees from 0 to 360, 0 north
   */
  public static double getAzimuthArrivingAtDownstreamNodeInDegrees(
      final LinkSegment segment, double reachMeters, final PlanitJtsCrsUtils crsUtils) {
    return LinkUtils.getAzimuthArrivingAtNodeInDegrees(
        segment.getParent(), segment.getDownstreamNode(), reachMeters, crsUtils);
  }
}
