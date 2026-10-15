package org.goplanit.utils.network.layer.macroscopic;

import org.goplanit.utils.graph.Edge;

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
    var permitsMotorisedRoadMode = MacroscopicLinkSegmentUtils.permitsMotorisedRoadMode();
    return edge -> edge instanceof MacroscopicLink &&
        hasSegmentMatching((MacroscopicLink) edge, permitsMotorisedRoadMode);
  }
}
