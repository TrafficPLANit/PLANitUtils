package org.goplanit.utils.network.layer.modifier;

import java.util.List;

import org.goplanit.utils.graph.directed.BannedTurn;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLink;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;
import org.goplanit.utils.network.layer.macroscopic.intersection.Intersection;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicBannedTurn;
import org.goplanit.utils.network.layer.macroscopic.SpatialMovement;
import org.goplanit.utils.network.layer.macroscopic.intersection.SpatialIntersection;
import org.goplanit.utils.network.layer.physical.Node;

/**
 * What consolidating the intersections of a layer did, see
 * {@link MacroscopicNetworkLayerModifier#consolidateIntersections}, as a value so the caller decides what to log.
 * Each entity is listed once.
 *
 * @author markr
 */
public class IntersectionConsolidationModifierResult {

  /**
   * A link segment entering a consolidated intersection that road traffic can only reach by driving through it, and
   * which was therefore made internal, together with the segments traced back over to the intersection
   */
  public static class LinkReachedOnlyThroughIntersection {

    /** the segment that entered the intersection from outside */
    private final MacroscopicLinkSegment enteringSegment;

    /** the member nodes of the intersection that feed it */
    private final List<Node> feedingNodes;

    /** the longest way back from the node it enters to a node feeding it, along the road, in metres */
    private final double distanceMeters;

    /** the segments made internal: the entering segment and every segment traced back over */
    private final List<MacroscopicLinkSegment> segmentsMadeInternal;

    /** true when a vehicle signal stands on one of those segments, which then became internal segments with a signal */
    private final boolean withSignal;

    /**
     * Constructor
     *
     * @param enteringSegment the segment that entered the intersection from outside
     * @param feedingNodes the member nodes of the intersection that feed it
     * @param distanceMeters the longest way back to a node feeding it, along the road, in metres
     * @param segmentsMadeInternal the entering segment and every segment traced back over
     * @param withSignal true when a vehicle signal stands on one of those segments
     */
    public LinkReachedOnlyThroughIntersection(
        MacroscopicLinkSegment enteringSegment, List<Node> feedingNodes, double distanceMeters,
        List<MacroscopicLinkSegment> segmentsMadeInternal, boolean withSignal) {
      this.enteringSegment = enteringSegment;
      this.feedingNodes = List.copyOf(feedingNodes);
      this.distanceMeters = distanceMeters;
      this.segmentsMadeInternal = List.copyOf(segmentsMadeInternal);
      this.withSignal = withSignal;
    }

    /**
     * The segment that entered the intersection from outside
     *
     * @return entering segment
     */
    public MacroscopicLinkSegment getEnteringSegment() {
      return enteringSegment;
    }

    /**
     * The member nodes of the intersection that feed the entering segment
     *
     * @return feeding nodes, unmodifiable
     */
    public List<Node> getFeedingNodes() {
      return feedingNodes;
    }

    /**
     * The longest way back from the node the segment enters to a node feeding it, along the road
     *
     * @return distance in metres
     */
    public double getDistanceMeters() {
      return distanceMeters;
    }

    /**
     * The segments made internal: the entering segment and every segment traced back over to the intersection
     *
     * @return segments made internal, unmodifiable
     */
    public List<MacroscopicLinkSegment> getSegmentsMadeInternal() {
      return segmentsMadeInternal;
    }

    /**
     * Verify if a vehicle signal stands on one of the segments made internal, which then became internal segments with
     * a signal
     *
     * @return true when with a signal, false otherwise
     */
    public boolean isWithSignal() {
      return withSignal;
    }
  }

  /** each intersection replaced, with the spatial intersection that replaced it */
  private final List<Pair<Intersection, SpatialIntersection>> replacedIntersections;

  /** intersection clusters with more parsed intersections than allowed, kept as they were */
  private final List<List<Intersection>> intersectionClustersTooLargeToSubdivide;

  /** nodes that became member nodes without a signal of their own */
  private final List<Node> joinedNodes;

  /** approach segments that became internal segments because they now start at a member node */
  private final List<MacroscopicLinkSegment> approachSegmentsMadeInternal;

  /** internal links longer than the warning length */
  private final List<MacroscopicLink> internalLinksLongerThanWarningLength;

  /** links made internal because road traffic only reaches them by driving through the intersection */
  private final List<LinkReachedOnlyThroughIntersection> linksReachedOnlyThroughIntersection;

  /** crossing intersections that joined a junction along the road */
  private final List<Intersection> crossingsJoined;

  /** crossing intersections that joined a junction from one of its slip lanes */
  private final List<Intersection> crossingsJoinedFromSlipLane;

  /** segments that enter a signalised junction and allow a motorised road mode, but are not approaches: counted, not
   * added */
  private final List<MacroscopicLinkSegment> missingApproachSegments;

  /** signalised junctions where fewer than half of the road approaches have a signal of their own */
  private final List<Intersection> junctionsWithFewLitArms;

  /** movements stored on the spatial intersections */
  private final List<SpatialMovement> storedSpatialMovements;

  /** links used only by collapsed modes, moved from a member node to the centre node of their intersection */
  private final List<MacroscopicLink> linksMovedToCentreNode;

  /** links added from a centre node to a member node, each for one collapsed mode */
  private final List<MacroscopicLink> linksAddedToCentreNode;

  /** bans removed because their segments no longer meet once a link was moved, or left with no modes once the
   * collapsed modes were taken off */
  private final List<BannedTurn> removedBannedTurns;

  /** banned turns added on the turns collapse made, or extended, for the collapsed modes they hold */
  private final List<MacroscopicBannedTurn> bannedTurnsAddedOrExtendedForCollapsedModes;

  /** banned turns on internal segments that no longer name the collapsed modes */
  private final List<MacroscopicBannedTurn> bannedTurnsThatLostCollapsedModes;

  /** each movement and collapsed mode whose ban no new turn could hold without banning a movement that was allowed */
  private final List<Pair<SpatialMovement, Mode>> collapsedModeBansNotHeld;

  /** what was removed for lack of mode access after collapsing */
  private final ModeAccessCleanupModifierResult modeAccessCleanup;

  /**
   * Constructor
   *
   * @param replacedIntersections each intersection replaced, with the spatial intersection that replaced it
   * @param intersectionClustersTooLargeToSubdivide intersection clusters kept as they were because they are too large
   * @param joinedNodes nodes that became member nodes without a signal of their own
   * @param approachSegmentsMadeInternal approach segments that became internal segments
   * @param internalLinksLongerThanWarningLength internal links longer than the warning length
   * @param linksReachedOnlyThroughIntersection links made internal because they are reached only through the
   *                                            intersection
   * @param crossingsJoined crossing intersections that joined a junction along the road
   * @param crossingsJoinedFromSlipLane crossing intersections that joined a junction from one of its slip lanes
   * @param missingApproachSegments segments entering a signalised junction that are not approaches
   * @param junctionsWithFewLitArms signalised junctions where fewer than half of the road approaches have a signal
   * @param storedSpatialMovements movements stored on the spatial intersections
   * @param linksMovedToCentreNode links used only by collapsed modes, moved to a centre node
   * @param linksAddedToCentreNode links added from a centre node to a member node, each for one collapsed mode
   * @param removedBannedTurns bans removed because their segments no longer meet, or left with no modes
   * @param bannedTurnsAddedOrExtendedForCollapsedModes banned turns added on the turns collapse made, or extended
   * @param bannedTurnsThatLostCollapsedModes banned turns on internal segments that no longer name the collapsed modes
   * @param collapsedModeBansNotHeld each movement and collapsed mode whose ban no new turn could hold
   * @param modeAccessCleanup what was removed for lack of mode access after collapsing, empty when nothing collapsed
   */
  public IntersectionConsolidationModifierResult(
      List<Pair<Intersection, SpatialIntersection>> replacedIntersections,
      List<List<Intersection>> intersectionClustersTooLargeToSubdivide,
      List<Node> joinedNodes,
      List<MacroscopicLinkSegment> approachSegmentsMadeInternal,
      List<MacroscopicLink> internalLinksLongerThanWarningLength,
      List<LinkReachedOnlyThroughIntersection> linksReachedOnlyThroughIntersection,
      List<Intersection> crossingsJoined,
      List<Intersection> crossingsJoinedFromSlipLane,
      List<MacroscopicLinkSegment> missingApproachSegments,
      List<Intersection> junctionsWithFewLitArms,
      List<SpatialMovement> storedSpatialMovements,
      List<MacroscopicLink> linksMovedToCentreNode,
      List<MacroscopicLink> linksAddedToCentreNode,
      List<BannedTurn> removedBannedTurns,
      List<MacroscopicBannedTurn> bannedTurnsAddedOrExtendedForCollapsedModes,
      List<MacroscopicBannedTurn> bannedTurnsThatLostCollapsedModes,
      List<Pair<SpatialMovement, Mode>> collapsedModeBansNotHeld,
      ModeAccessCleanupModifierResult modeAccessCleanup) {
    this.replacedIntersections = List.copyOf(replacedIntersections);
    this.intersectionClustersTooLargeToSubdivide = List.copyOf(intersectionClustersTooLargeToSubdivide);
    this.joinedNodes = List.copyOf(joinedNodes);
    this.approachSegmentsMadeInternal = List.copyOf(approachSegmentsMadeInternal);
    this.internalLinksLongerThanWarningLength = List.copyOf(internalLinksLongerThanWarningLength);
    this.linksReachedOnlyThroughIntersection = List.copyOf(linksReachedOnlyThroughIntersection);
    this.crossingsJoined = List.copyOf(crossingsJoined);
    this.crossingsJoinedFromSlipLane = List.copyOf(crossingsJoinedFromSlipLane);
    this.missingApproachSegments = List.copyOf(missingApproachSegments);
    this.junctionsWithFewLitArms = List.copyOf(junctionsWithFewLitArms);
    this.storedSpatialMovements = List.copyOf(storedSpatialMovements);
    this.linksMovedToCentreNode = List.copyOf(linksMovedToCentreNode);
    this.linksAddedToCentreNode = List.copyOf(linksAddedToCentreNode);
    this.removedBannedTurns = List.copyOf(removedBannedTurns);
    this.bannedTurnsAddedOrExtendedForCollapsedModes = List.copyOf(bannedTurnsAddedOrExtendedForCollapsedModes);
    this.bannedTurnsThatLostCollapsedModes = List.copyOf(bannedTurnsThatLostCollapsedModes);
    this.collapsedModeBansNotHeld = List.copyOf(collapsedModeBansNotHeld);
    this.modeAccessCleanup = modeAccessCleanup;
  }

  /**
   * Each intersection replaced, with the spatial intersection that replaced it. Every intersection a spatial
   * intersection is made from is replaced, so joining four gives four pairs
   *
   * @return pairs of replaced intersection and the spatial intersection replacing it, unmodifiable
   */
  public List<Pair<Intersection, SpatialIntersection>> getReplacedIntersections() {
    return replacedIntersections;
  }

  /**
   * Intersection clusters with more parsed intersections than allowed, kept as they were
   *
   * @return the parsed intersections of each such cluster, unmodifiable
   */
  public List<List<Intersection>> getIntersectionClustersTooLargeToSubdivide() {
    return intersectionClustersTooLargeToSubdivide;
  }

  /**
   * Nodes that became member nodes without a signal of their own, such as the far carriageway node of a divided road
   *
   * @return joined nodes, unmodifiable
   */
  public List<Node> getJoinedNodes() {
    return joinedNodes;
  }

  /**
   * Approach segments that became internal segments because they now start at a member node, such as a stop line in a
   * median. Each became an internal segment with a signal when a vehicle signal stands on it, as the caller told
   * consolidation, and a plain one otherwise
   *
   * @return approach segments made internal, unmodifiable
   */
  public List<MacroscopicLinkSegment> getApproachSegmentsMadeInternal() {
    return approachSegmentsMadeInternal;
  }

  /**
   * Internal links longer than the warning length
   *
   * @return internal links longer than the warning length, unmodifiable
   */
  public List<MacroscopicLink> getInternalLinksLongerThanWarningLength() {
    return internalLinksLongerThanWarningLength;
  }

  /**
   * Links made internal because road traffic only reaches them by driving through the intersection
   *
   * @return each case, unmodifiable
   */
  public List<LinkReachedOnlyThroughIntersection> getLinksReachedOnlyThroughIntersection() {
    return linksReachedOnlyThroughIntersection;
  }

  /**
   * Crossing intersections that joined a junction along the road
   *
   * @return crossings joined, unmodifiable
   */
  public List<Intersection> getCrossingsJoined() {
    return crossingsJoined;
  }

  /**
   * Crossing intersections that joined a junction from one of its slip lanes
   *
   * @return crossings joined from a slip lane, unmodifiable
   */
  public List<Intersection> getCrossingsJoinedFromSlipLane() {
    return crossingsJoinedFromSlipLane;
  }

  /**
   * Segments that enter a signalised junction and allow a motorised road mode, but are not approaches. They are
   * counted, not added as approaches
   *
   * @return missing approach segments, unmodifiable
   */
  public List<MacroscopicLinkSegment> getMissingApproachSegments() {
    return missingApproachSegments;
  }

  /**
   * Signalised junctions where fewer than half of the approaches that allow a motorised road mode have a signal of
   * their own
   *
   * @return junctions with few lit arms, unmodifiable
   */
  public List<Intersection> getJunctionsWithFewLitArms() {
    return junctionsWithFewLitArms;
  }

  /**
   * Movements stored on the spatial intersections, each from an approach to an exit
   *
   * @return stored movements, unmodifiable
   */
  public List<SpatialMovement> getStoredSpatialMovements() {
    return storedSpatialMovements;
  }

  /**
   * Links used only by collapsed modes, moved from a member node to the centre node of their intersection
   *
   * @return moved links, unmodifiable
   */
  public List<MacroscopicLink> getLinksMovedToCentreNode() {
    return linksMovedToCentreNode;
  }

  /**
   * Links added from a centre node to a member node, each for one collapsed mode still allowed on a road kept at that
   * member node
   *
   * @return added links, unmodifiable
   */
  public List<MacroscopicLink> getLinksAddedToCentreNode() {
    return linksAddedToCentreNode;
  }

  /**
   * Bans removed because their segments no longer meet once a link was moved to a centre node, or because a banned
   * turn on an internal segment was left with no modes once the collapsed modes were taken off
   *
   * @return removed bans, unmodifiable
   */
  public List<BannedTurn> getRemovedBannedTurns() {
    return removedBannedTurns;
  }

  /**
   * Banned turns added on the turns collapse made, at the centre node or at a member node, or an existing banned turn
   * on the same segments that gained the collapsed modes it holds
   *
   * @return added or extended banned turns, unmodifiable
   */
  public List<MacroscopicBannedTurn> getBannedTurnsAddedOrExtendedForCollapsedModes() {
    return bannedTurnsAddedOrExtendedForCollapsedModes;
  }

  /**
   * Banned turns on internal segments that no longer name the collapsed modes, since those modes no longer use the
   * internal segments. A banned turn left with no modes is removed instead, and listed with the removed bans
   *
   * @return banned turns that lost collapsed modes, unmodifiable
   */
  public List<MacroscopicBannedTurn> getBannedTurnsThatLostCollapsedModes() {
    return bannedTurnsThatLostCollapsedModes;
  }

  /**
   * Each movement and collapsed mode whose ban no new turn could hold without also banning a movement that was
   * allowed, so the ban was dropped. This only happens for a movement with an arm that stays on its member node
   *
   * @return dropped bans as movement and mode, unmodifiable
   */
  public List<Pair<SpatialMovement, Mode>> getCollapsedModeBansNotHeld() {
    return collapsedModeBansNotHeld;
  }

  /**
   * What was removed for lack of mode access after collapsing, such as the inner links no mode can use any more
   *
   * @return the mode access cleanup result, empty when nothing was collapsed
   */
  public ModeAccessCleanupModifierResult getModeAccessCleanup() {
    return modeAccessCleanup;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return String.format(
        "replaced %d intersections, joined %d nodes and %d crossings (%d from a slip lane), made %d approaches " +
        "internal and %d links reached only through the intersection; %d intersection clusters too large to " +
        "subdivide, %d internal links longer than the warning length, %d missing approaches, %d junctions with " +
        "few lit arms; stored %d movements; moved %d links to and added %d links to centre nodes, removed %d bans, " +
        "added or extended %d and reduced %d banned turns for collapsed modes, dropped %d bans of collapsed modes",
        replacedIntersections.size(), joinedNodes.size(), crossingsJoined.size(), crossingsJoinedFromSlipLane.size(),
        approachSegmentsMadeInternal.size(), linksReachedOnlyThroughIntersection.size(),
        intersectionClustersTooLargeToSubdivide.size(), internalLinksLongerThanWarningLength.size(),
        missingApproachSegments.size(), junctionsWithFewLitArms.size(), storedSpatialMovements.size(),
        linksMovedToCentreNode.size(), linksAddedToCentreNode.size(), removedBannedTurns.size(),
        bannedTurnsAddedOrExtendedForCollapsedModes.size(), bannedTurnsThatLostCollapsedModes.size(),
        collapsedModeBansNotHeld.size());
  }
}
