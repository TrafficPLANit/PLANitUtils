package org.goplanit.utils.network.layer.macroscopic;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.math.Precision;
import org.goplanit.utils.network.layer.physical.NodeUtils;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.mode.TrackModeType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Utilities for macroscopic link segments
 *
 * @author markr
 *
 */
public class MacroscopicLinkSegmentUtils {

  /**
   * Verify whether a link segment belongs exclusively to the network of a single track type, i.e. every mode it
   * grants access to propagates over that track type.
   * <p>
   * Exclusivity rather than mere presence is deliberate. A segment permitting both a road and a rail mode - a tram
   * embedded in a street being the typical case - is part of both networks at once, so it conforms to neither in
   * isolation. Requiring all modes to agree means such shared infrastructure matches no track type and is left
   * alone by any per track type processing, which is the safe outcome: claiming it for one network would allow
   * that network's pruning to remove infrastructure the other still depends on.
   * </p>
   *
   * @param linkSegment to verify, may be null
   * @param trackType the track type all allowed modes must propagate over
   * @return true when the segment has a type granting access to at least one mode and every such mode is of the
   *         given track type, false otherwise
   */
  public static boolean isExclusivelyOfTrackType(
      final MacroscopicLinkSegment linkSegment, final TrackModeType trackType) {
    if (linkSegment == null || !linkSegment.hasLinkSegmentType()) {
      /* without a type there is no mode information to judge the segment on */
      return false;
    }
    return isExclusivelyOfTrackType(linkSegment.getLinkSegmentType(), trackType);
  }

  /**
   * Verify whether a link segment type belongs exclusively to the network of a single track type, i.e. every mode
   * it grants access to propagates over that track type. See
   * {@link #isExclusivelyOfTrackType(MacroscopicLinkSegment, TrackModeType)} for why exclusivity is required
   * rather than mere presence.
   *
   * @param linkSegmentType to verify, may be null
   * @param trackType the track type all allowed modes must propagate over
   * @return true when the type grants access to at least one mode and every such mode is of the given track type,
   *         false otherwise
   */
  public static boolean isExclusivelyOfTrackType(
      final MacroscopicLinkSegmentType linkSegmentType, final TrackModeType trackType) {
    if (linkSegmentType == null) {
      return false;
    }

    var allowedModes = linkSegmentType.getAllowedModes();
    if (allowedModes == null || allowedModes.isEmpty()) {
      /* guard against an empty set matching every track type vacuously, which would make a type without any
       * mode access qualify for all of them at once */
      return false;
    }

    return allowedModes.stream().allMatch(
        mode -> mode.hasPhysicalFeatures() && mode.getPhysicalFeatures().getTrackType() == trackType);
  }

  /**
   * Create a predicate selecting only those edge segments that are macroscopic link segments belonging exclusively
   * to the network of the given track type, see
   * {@link #isExclusivelyOfTrackType(MacroscopicLinkSegment, TrackModeType)}.
   * <p>
   * Shaped as a {@code Predicate<EdgeSegment>} so it can be handed straight to the graph level utilities that
   * identify subgraphs, which operate on the untyped graph and therefore only know about edge segments. Any
   * segment that is not a macroscopic link segment is rejected.
   * </p>
   *
   * @param trackType the track type all allowed modes must propagate over
   * @return predicate to use
   */
  public static Predicate<EdgeSegment> exclusivelyOfTrackType(final TrackModeType trackType) {
    return edgeSegment -> edgeSegment instanceof MacroscopicLinkSegment &&
        isExclusivelyOfTrackType((MacroscopicLinkSegment) edgeSegment, trackType);
  }

  /**
   * Create a predicate selecting only those edge segments that are macroscopic link segments permitting the given
   * mode.
   * <p>
   * Shaped as a {@code Predicate<EdgeSegment>} for the same reason as
   * {@link #exclusivelyOfTrackType(TrackModeType)}: the graph level algorithms know nothing about modes, since an
   * edge segment carries no mode information at all. Mode access is a network layer concept, and a predicate is
   * the seam through which it reaches a graph algorithm without that algorithm having to learn about it.
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
    return edgeSegment -> edgeSegment instanceof MacroscopicLinkSegment &&
        ((MacroscopicLinkSegment) edgeSegment).isModeAllowed(mode);
  }

  /**
   * Create a predicate selecting only those edge segments that are macroscopic link segments permitting at least one
   * motorised road mode, such as a car or a bus, see {@link Mode#isMotorisedRoadMode()}. Shaped as a
   * {@code Predicate<EdgeSegment>} for the same reason as {@link #permitsMode(Mode)}.
   *
   * @return predicate to use
   */
  public static Predicate<EdgeSegment> permitsMotorisedRoadMode() {
    return edgeSegment -> edgeSegment instanceof MacroscopicLinkSegment &&
        ((MacroscopicLinkSegment) edgeSegment).isAnyModeAllowed(Mode::isMotorisedRoadMode);
  }

  /**
   * Find the link segments a segment continues on beyond the node it arrives at. They leave that node, meet the
   * condition, and are not on the link arrived on, so going back is never a continuation. For example, arriving at a
   * node where a road splits in two, both roads ahead are continuations, but the opposite direction of the road arrived
   * along is not.
   *
   * @param arriving segment arriving at the node
   * @param condition a continuation must meet
   * @return the continuations, empty when there are none
   */
  public static List<MacroscopicLinkSegment> findContinuations(
      final MacroscopicLinkSegment arriving, final Predicate<? super MacroscopicLinkSegment> condition) {
    var continuations = new ArrayList<MacroscopicLinkSegment>(2);
    for (MacroscopicLinkSegment leaving : arriving.getDownstreamNode().<MacroscopicLinkSegment>getExitLinkSegments()) {
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
   * @param leaving segment leaving the node
   * @param condition a predecessor must meet
   * @return the predecessors, empty when there are none
   */
  public static List<MacroscopicLinkSegment> findPredecessors(
      final MacroscopicLinkSegment leaving, final Predicate<? super MacroscopicLinkSegment> condition) {
    var predecessors = new ArrayList<MacroscopicLinkSegment>(2);
    for (MacroscopicLinkSegment entering : leaving.getUpstreamNode().<MacroscopicLinkSegment>getEntryLinkSegments()) {
      if (entering.getParent() != leaving.getParent() && condition.test(entering)) {
        predecessors.add(entering);
      }
    }
    return predecessors;
  }

  /**
   * Find the link segments onward from a segment in a direction: its continuations with the traffic, see
   * {@link #findContinuations}, or its predecessors against it, see {@link #findPredecessors}
   *
   * @param segment to go on from
   * @param withTraffic true to follow the traffic, false to go back against it
   * @param condition a segment onward must meet
   * @return the segments onward, empty when there are none
   */
  public static List<MacroscopicLinkSegment> findOnward(final MacroscopicLinkSegment segment, boolean withTraffic,
      final Predicate<? super MacroscopicLinkSegment> condition) {
    return withTraffic ? findContinuations(segment, condition) : findPredecessors(segment, condition);
  }

  /**
   * Follow segments meeting a condition from a segment, through nodes where they simply continue: one segment onward
   * meets the condition, see {@link #findOnward}, and none comes back, other than on the link just followed. For
   * example, a road followed through the shape nodes of a long bend, up to the junction it reaches
   *
   * @param segment to start along
   * @param withTraffic true to follow the traffic, false to go back against it
   * @param maximumDistanceMeters furthest to follow, the first segment included
   * @param condition the segments followed must meet
   * @return the last segment followed, ending (or starting, against the traffic) at the node where the segments stop
   *         simply continuing; null when that node is not reached within the distance
   */
  public static MacroscopicLinkSegment followUntilItBranches(final MacroscopicLinkSegment segment,
      boolean withTraffic, double maximumDistanceMeters, final Predicate<? super MacroscopicLinkSegment> condition) {
    double meters = segment.getParent().getLengthMeters();
    var current = segment;
    while (meters <= maximumDistanceMeters + Precision.EPSILON_9) {
      var node = withTraffic ? current.getDownstreamNode() : current.getUpstreamNode();
      var currentLink = current.getParent();
      var onward = findOnward(current, withTraffic, condition);
      List<MacroscopicLinkSegment> back = withTraffic ?
          NodeUtils.<MacroscopicLinkSegment>findEntryLinkSegments(node, condition) :
          NodeUtils.<MacroscopicLinkSegment>findExitLinkSegments(node, condition);
      back.removeIf(previous -> previous.getParent() == currentLink);
      if (onward.size() != 1 || !back.isEmpty()) {
        return current;
      }
      current = onward.get(0);
      meters += current.getParent().getLengthMeters();
    }
    return null;
  }
}
