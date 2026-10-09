package org.goplanit.utils.network.layer.modifier;

import java.util.List;

import org.goplanit.utils.network.layer.macroscopic.MacroscopicLink;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegmentType;
import org.goplanit.utils.network.layer.macroscopic.intersection.Intersection;
import org.goplanit.utils.network.layer.physical.Node;

/**
 * What removing the infrastructure no mode can use any more came to, see
 * {@link MacroscopicNetworkLayerModifier#removeInfrastructureWithoutModeAccess()}, as a value so the caller decides
 * what to log.
 *
 * @author markr
 */
public class ModeAccessCleanupModifierResult {

  /** link segments removed because no mode could use them */
  private final List<MacroscopicLinkSegment> removedLinkSegments;

  /** links removed because they were left without any segment */
  private final List<MacroscopicLink> removedLinks;

  /** nodes removed because they were left without any edge */
  private final List<Node> removedNodes;

  /** link segment types removed because they granted no mode access */
  private final List<MacroscopicLinkSegmentType> removedLinkSegmentTypes;

  /** intersections removed because they were left without approaches or member nodes */
  private final List<Intersection> removedIntersections;

  /**
   * Constructor
   *
   * @param removedLinkSegments link segments removed because no mode could use them
   * @param removedLinks links removed because they were left without any segment
   * @param removedNodes nodes removed because they were left without any edge
   * @param removedLinkSegmentTypes link segment types removed because they granted no mode access
   * @param removedIntersections intersections removed because they were left without approaches or member nodes
   */
  public ModeAccessCleanupModifierResult(
      List<MacroscopicLinkSegment> removedLinkSegments, List<MacroscopicLink> removedLinks, List<Node> removedNodes,
      List<MacroscopicLinkSegmentType> removedLinkSegmentTypes, List<Intersection> removedIntersections) {
    this.removedLinkSegments = List.copyOf(removedLinkSegments);
    this.removedLinks = List.copyOf(removedLinks);
    this.removedNodes = List.copyOf(removedNodes);
    this.removedLinkSegmentTypes = List.copyOf(removedLinkSegmentTypes);
    this.removedIntersections = List.copyOf(removedIntersections);
  }

  /**
   * Link segments removed because no mode could use them
   *
   * @return removed link segments, unmodifiable
   */
  public List<MacroscopicLinkSegment> getRemovedLinkSegments() {
    return removedLinkSegments;
  }

  /**
   * Links removed because they were left without any segment
   *
   * @return removed links, unmodifiable
   */
  public List<MacroscopicLink> getRemovedLinks() {
    return removedLinks;
  }

  /**
   * Nodes removed because they were left without any edge
   *
   * @return removed nodes, unmodifiable
   */
  public List<Node> getRemovedNodes() {
    return removedNodes;
  }

  /**
   * Link segment types removed because they granted no mode access
   *
   * @return removed link segment types, unmodifiable
   */
  public List<MacroscopicLinkSegmentType> getRemovedLinkSegmentTypes() {
    return removedLinkSegmentTypes;
  }

  /**
   * Intersections removed because they were left without approaches or member nodes
   *
   * @return removed intersections, unmodifiable
   */
  public List<Intersection> getRemovedIntersections() {
    return removedIntersections;
  }

  /**
   * Verify whether anything was removed
   *
   * @return true when nothing was removed
   */
  public boolean isEmpty() {
    return removedLinkSegments.isEmpty() && removedLinks.isEmpty() && removedNodes.isEmpty() &&
        removedIntersections.isEmpty();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return String.format(
        "removed %d link segments, %d links and %d nodes without any mode access (%d link segment types), and %d " +
        "intersections left without approaches or member nodes",
        removedLinkSegments.size(), removedLinks.size(), removedNodes.size(), removedLinkSegmentTypes.size(),
        removedIntersections.size());
  }
}
