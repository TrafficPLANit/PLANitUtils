package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.geo.PlanitJtsCrsUtils;
import org.goplanit.utils.graph.Edge;
import org.goplanit.utils.mode.Mode;
import org.locationtech.jts.geom.Coordinate;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * Utilities for links
 *
 * @author markr
 *
 */
public class LinkUtils {

  /**
   * Verify whether a link has a segment, in either direction, that meets the condition. A link with a segment in one
   * direction only is checked on that segment.
   *
   * @param <LS> type of the link's segments
   * @param link to verify
   * @param condition a segment must meet
   * @return true when the segment in at least one direction meets the condition, false otherwise
   */
  @SuppressWarnings("unchecked")
  public static <LS extends LinkSegment> boolean hasSegmentMatching(
      final Link link, final Predicate<? super LS> condition) {
    return (link.hasLinkSegmentAb() && condition.test((LS) link.getLinkSegmentAb())) ||
        (link.hasLinkSegmentBa() && condition.test((LS) link.getLinkSegmentBa()));
  }

  /**
   * Create a predicate selecting only those edges that are links with a segment, in either direction, permitting at
   * least one motorised road mode, such as a car or a bus, see {@link LinkSegmentUtils#permitsMotorisedRoadMode()}.
   * <p>
   * Shaped as a {@code Predicate<Edge>} so it can be handed straight to a vertex, e.g. to count the links at a node
   * that road traffic can use. Any edge that is not a link is rejected.
   * </p>
   *
   * @return predicate to use
   */
  public static Predicate<Edge> permitsMotorisedRoadModeOnAnySegment() {
    return permitsModeMatchingOnAnySegment(Mode::isMotorisedRoadMode);
  }

  /**
   * Create a predicate selecting only those edges that are links with a segment, in either direction, allowing at least
   * one mode that meets the condition. It is shaped as a {@code Predicate<Edge>} for the same reason as
   * {@link #permitsMotorisedRoadModeOnAnySegment()}. Any edge that is not a link is rejected.
   *
   * @param condition a mode allowed on a segment must meet
   * @return predicate to use
   */
  public static Predicate<Edge> permitsModeMatchingOnAnySegment(final Predicate<? super Mode> condition) {
    return edge -> edge instanceof Link && ((Link) edge).isAnyModeAllowedOnAnySegment(condition);
  }

  /**
   * Create a predicate selecting only those edges that are tram or light rail tracks. A track is a link a tram or light
   * rail may use but no motorised road mode may use, see {@link Mode#isTramOrLightRailMode()}, so a road link a tram is
   * allowed on is no track. It is shaped as a {@code Predicate<Edge>} for the same reason as
   * {@link #permitsMotorisedRoadModeOnAnySegment()}. Any edge that is not a link is rejected.
   *
   * @return predicate to use
   */
  public static Predicate<Edge> isTramOrLightRailTrack() {
    return permitsModeMatchingOnAnySegment(Mode::isTramOrLightRailMode)
        .and(permitsMotorisedRoadModeOnAnySegment().negate());
  }

  /**
   * Verify whether a tram or light rail track meets a road at a node. A link a motorised road mode may use meets a
   * track there, see {@link #isTramOrLightRailTrack()}. A tram allowed on a road link itself is no track meeting the
   * road
   *
   * @param node to verify
   * @return true when a tram or light rail track meets a road there, false otherwise
   */
  public static boolean isWhereTramOrLightRailTrackMeetsRoad(final Node node) {
    return node.getNumberOfEdges(permitsMotorisedRoadModeOnAnySegment()) > 0 &&
        node.getNumberOfEdges(isTramOrLightRailTrack()) > 0;
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
  public static boolean isUsedOnlyByModes(final Link link, final Collection<Mode> modes) {
    return link.getLinkSegments().stream().allMatch(segment ->
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

  /**
   * The direction of travel along a link into one of its nodes, measured over the last stretch of the link before the
   * node: from the farthest shape node within reach of the node, measured along the link, or from the nearest shape
   * node when none lies within reach, or from the other node when the link has no shape nodes. For example, a road that
   * curves but runs straight north over its last 20 m arrives heading north within a reach of 25 m
   *
   * @param link to measure along
   * @param node one of the link's nodes, the one travelled into
   * @param reachMeters how far back from the node, along the link, shape nodes are considered, 0 or more
   * @param crsUtils to measure distances and directions with
   * @return the compass bearing of the direction of travel into the node, in degrees from 0 to 360, 0 north
   */
  public static double getAzimuthArrivingAtNodeInDegrees(
      final Link link, final Node node, double reachMeters, final PlanitJtsCrsUtils crsUtils) {
    var otherNode = (Node) (link.getVertexA() == node ? link.getVertexB() : link.getVertexA());
    Coordinate from = otherNode.getPosition().getCoordinate();
    Coordinate to = node.getPosition().getCoordinate();
    if (link.hasGeometry() && link.getGeometry().getNumPoints() > 2) {
      var coordinates = link.getGeometry().getCoordinates();
      boolean nodeAtGeometryEnd = (node == link.getVertexB()) == link.isGeometryInAbDirection();
      int step = nodeAtGeometryEnd ? -1 : 1;
      int nodeIndex = nodeAtGeometryEnd ? coordinates.length - 1 : 0;
      to = coordinates[nodeIndex];
      from = coordinates[nodeIndex + step];
      double distanceMeters = crsUtils.getDistanceInMetres(to, from);
      for (int shape = nodeIndex + 2 * step; shape > 0 && shape < coordinates.length - 1; shape += step) {
        distanceMeters += crsUtils.getDistanceInMetres(coordinates[shape - step], coordinates[shape]);
        if (distanceMeters > reachMeters) {
          break;
        }
        from = coordinates[shape];
      }
    }
    return crsUtils.getAzimuthInDegrees(from, to, true);
  }
}
