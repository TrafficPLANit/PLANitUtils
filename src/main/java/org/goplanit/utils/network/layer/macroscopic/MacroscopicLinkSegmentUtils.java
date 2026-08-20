package org.goplanit.utils.network.layer.macroscopic;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.mode.TrackModeType;

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

    var allowedModes = linkSegment.getLinkSegmentType().getAllowedModes();
    if (allowedModes == null || allowedModes.isEmpty()) {
      /* guard against an empty set matching every track type vacuously, which would make a segment without any
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
}
