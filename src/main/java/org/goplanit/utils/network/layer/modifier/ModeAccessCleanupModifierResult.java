package org.goplanit.utils.network.layer.modifier;

/**
 * What removing the infrastructure no mode can use any more came to, see
 * {@link MacroscopicNetworkLayerModifier#removeInfrastructureWithoutModeAccess()}, as a value so the caller decides
 * what to log.
 *
 * @author markr
 */
public class ModeAccessCleanupModifierResult {

  /** number of link segments removed because no mode could use them */
  private final int removedLinkSegments;

  /** number of links removed because they were left without any segment */
  private final int removedLinks;

  /** number of nodes removed because they were left without any edge */
  private final int removedNodes;

  /** number of link segment types removed because they granted no mode access */
  private final int removedLinkSegmentTypes;

  /** number of intersections removed because they were left without approaches or member nodes */
  private final int removedIntersections;

  /**
   * Constructor
   *
   * @param removedLinkSegments number of link segments removed because no mode could use them
   * @param removedLinks number of links removed because they were left without any segment
   * @param removedNodes number of nodes removed because they were left without any edge
   * @param removedLinkSegmentTypes number of link segment types removed because they granted no mode access
   * @param removedIntersections number of intersections removed because they were left without approaches or member
   *                             nodes
   */
  public ModeAccessCleanupModifierResult(
      int removedLinkSegments, int removedLinks, int removedNodes, int removedLinkSegmentTypes,
      int removedIntersections) {
    this.removedLinkSegments = removedLinkSegments;
    this.removedLinks = removedLinks;
    this.removedNodes = removedNodes;
    this.removedLinkSegmentTypes = removedLinkSegmentTypes;
    this.removedIntersections = removedIntersections;
  }

  /**
   * Number of link segments removed because no mode could use them
   *
   * @return count
   */
  public int getRemovedLinkSegments() {
    return removedLinkSegments;
  }

  /**
   * Number of links removed because they were left without any segment
   *
   * @return count
   */
  public int getRemovedLinks() {
    return removedLinks;
  }

  /**
   * Number of nodes removed because they were left without any edge
   *
   * @return count
   */
  public int getRemovedNodes() {
    return removedNodes;
  }

  /**
   * Number of link segment types removed because they granted no mode access
   *
   * @return count
   */
  public int getRemovedLinkSegmentTypes() {
    return removedLinkSegmentTypes;
  }

  /**
   * Number of intersections removed because they were left without approaches or member nodes
   *
   * @return count
   */
  public int getRemovedIntersections() {
    return removedIntersections;
  }

  /**
   * Verify whether anything was removed
   *
   * @return true when nothing was removed
   */
  public boolean isEmpty() {
    return removedLinkSegments == 0 && removedLinks == 0 && removedNodes == 0 && removedIntersections == 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return String.format(
        "removed %d link segments, %d links and %d nodes without any mode access (%d link segment types), and %d " +
        "intersections left without approaches or member nodes",
        removedLinkSegments, removedLinks, removedNodes, removedLinkSegmentTypes, removedIntersections);
  }
}
