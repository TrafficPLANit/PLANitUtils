package org.goplanit.utils.network.layer.macroscopic;

import org.goplanit.utils.graph.Edge;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.physical.Link;
import org.goplanit.utils.network.layer.physical.Node;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * Utilities for macroscopic links
 *
 * @author markr
 *
 */
public class MacroscopicLinkUtils {

  /**
   * Verify whether a link has a segment, in either direction, that meets the condition. A link with a segment in one
   * direction only is checked on that segment.
   *
   * @param link to verify
   * @param condition a segment must meet
   * @return true when the segment in at least one direction meets the condition, false otherwise
   */
  public static boolean hasSegmentMatching(
      final MacroscopicLink link, final Predicate<? super MacroscopicLinkSegment> condition) {
    return (link.hasLinkSegmentAb() && condition.test(link.getLinkSegmentAb())) ||
        (link.hasLinkSegmentBa() && condition.test(link.getLinkSegmentBa()));
  }

  /**
   * Create a predicate selecting only those edges that are macroscopic links with a segment, in either direction,
   * permitting at least one motorised road mode, such as a car or a bus, see
   * {@link MacroscopicLinkSegmentUtils#permitsMotorisedRoadMode()}.
   * <p>
   * Shaped as a {@code Predicate<Edge>} so it can be handed straight to a vertex, e.g. to count the links at a node
   * that road traffic can use. Any edge that is not a macroscopic link is rejected.
   * </p>
   *
   * @return predicate to use
   */
  public static Predicate<Edge> permitsMotorisedRoadModeOnAnySegment() {
    return permitsModeMatchingOnAnySegment(Mode::isMotorisedRoadMode);
  }

  /**
   * Create a predicate selecting only those edges that are macroscopic links with a segment, in either direction,
   * allowing at least one mode that meets the condition. It is shaped as a {@code Predicate<Edge>} for the same reason
   * as {@link #permitsMotorisedRoadModeOnAnySegment()}. Any edge that is not a macroscopic link is rejected.
   *
   * @param condition a mode allowed on a segment must meet
   * @return predicate to use
   */
  public static Predicate<Edge> permitsModeMatchingOnAnySegment(final Predicate<? super Mode> condition) {
    return edge -> edge instanceof MacroscopicLink &&
        hasSegmentMatching((MacroscopicLink) edge, segment -> segment.isAnyModeAllowed(condition));
  }

  /**
   * Verify whether a tram or light rail track meets a road at a node. A link a motorised road mode may use meets a
   * track there. A track is a link a tram or light rail may use but no motorised road mode may use, see
   * {@link Mode#isTramOrLightRailMode()}. A tram allowed on a road link itself is no track meeting the road
   *
   * @param node to verify
   * @return true when a tram or light rail track meets a road there, false otherwise
   */
  public static boolean isWhereTramOrLightRailTrackMeetsRoad(final Node node) {
    var road = permitsMotorisedRoadModeOnAnySegment();
    var tramOrLightRailTrack = permitsModeMatchingOnAnySegment(Mode::isTramOrLightRailMode).and(road.negate());
    return node.getNumberOfEdges(road) > 0 && node.getNumberOfEdges(tramOrLightRailTrack) > 0;
  }

  /**
   * Verify whether no motorised road mode and no rail mode, such as a train, tram or light rail, uses a node: none of
   * its links allows one in either direction, such as a node where only pedestrians or cyclists go. A node without
   * links is used by none
   *
   * @param node to verify
   * @return true when none uses it, false otherwise
   */
  public static boolean isUsedByNoMotorisedRoadOrRailMode(final Node node) {
    return !isUsedByModeMatching(node, Mode::isMotorisedRoadOrRailMode);
  }

  /**
   * Verify whether a mode that meets the condition uses a node: one of its links allows such a mode in at least one
   * direction, see {@link #permitsModeMatchingOnAnySegment(Predicate)}. For example, a node a motorised road mode uses
   * lies on a road
   *
   * @param node to verify
   * @param condition a mode allowed on a link of the node must meet
   * @return true when such a mode uses it, false otherwise
   */
  public static boolean isUsedByModeMatching(final Node node, final Predicate<? super Mode> condition) {
    return node.getNumberOfEdges(permitsModeMatchingOnAnySegment(condition)) > 0;
  }

  /**
   * Verify whether only the given modes use a link: each of its segments allows at least one mode, and only modes
   * among the given ones
   *
   * @param link to verify
   * @param modes the only modes that may use it
   * @return true when used only by those modes, false otherwise
   */
  public static boolean isUsedOnlyByModes(final MacroscopicLink link, final Collection<Mode> modes) {
    return link.getLinkSegments().stream().map(MacroscopicLinkSegment.class::cast).allMatch(segment ->
        !segment.getAllowedModes().isEmpty() && modes.containsAll(segment.getAllowedModes()));
  }

  /**
   * Verify if two links belong to the same road: they share their external id, as the pieces of one road of the
   * source do once it is broken into several links, or they share a name that is not blank
   *
   * @param one link
   * @param other link
   * @return true when they share their external id or their name, false otherwise
   */
  public static boolean isSameExternalIdOrName(final Link one, final Link other) {
    if (one.getExternalId() != null && one.getExternalId().equals(other.getExternalId())) {
      return true;
    }
    return one.getName() != null && !one.getName().isBlank() && one.getName().equals(other.getName());
  }
}
