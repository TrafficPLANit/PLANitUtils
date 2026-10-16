package org.goplanit.utils.network.layer.modifier.consolidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLink;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;
import org.goplanit.utils.network.layer.macroscopic.intersection.Intersection;
import org.goplanit.utils.network.layer.macroscopic.SpatialMovement;
import org.goplanit.utils.network.layer.macroscopic.intersection.SpatialIntersection;
import org.goplanit.utils.network.layer.modifier.MacroscopicNetworkLayerModifier;
import org.goplanit.utils.network.layer.physical.Node;

/**
 * What consolidating the intersections of a layer did, see
 * {@link MacroscopicNetworkLayerModifier#consolidateIntersections}, as a value so the caller decides what to log.
 * Consolidation fills it as it makes each change; the caller only reads it. Each entity is listed once.
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
  private final List<Pair<Intersection, SpatialIntersection>> replacedIntersections = new ArrayList<>();

  /** intersection clusters with more parsed intersections than allowed, kept as they were */
  private final List<List<Intersection>> intersectionClustersTooLargeToSubdivide = new ArrayList<>();

  /** nodes that became member nodes without a signal of their own, each once */
  private final Set<Node> joinedNodes = new LinkedHashSet<>();

  /** approach segments that became internal segments because they now start at a member node, each once */
  private final Set<MacroscopicLinkSegment> approachSegmentsMadeInternal = new LinkedHashSet<>();

  /** internal links longer than the warning length, each once */
  private final Set<MacroscopicLink> internalLinksLongerThanWarningLength = new LinkedHashSet<>();

  /** links made internal because road traffic only reaches them by driving through the intersection */
  private final List<LinkReachedOnlyThroughIntersection> linksReachedOnlyThroughIntersection = new ArrayList<>();

  /** crossing intersections that joined a junction along the road */
  private final List<Intersection> crossingsJoined = new ArrayList<>();

  /** crossing intersections that joined a junction from one of its slip lanes */
  private final List<Intersection> crossingsJoinedFromSlipLane = new ArrayList<>();

  /** segments that enter a signalised junction and allow a motorised road mode, but are not approaches: counted, not
   * added, each once */
  private final Set<MacroscopicLinkSegment> missingApproachSegments = new LinkedHashSet<>();

  /** signalised junctions where fewer than half of the road approaches have a signal of their own */
  private final List<Intersection> junctionsWithFewLitArms = new ArrayList<>();

  /** movements stored on the spatial intersections */
  private final List<SpatialMovement> storedSpatialMovements = new ArrayList<>();

  /**
   * Constructor of an empty result, which consolidation fills as it makes each change
   */
  public IntersectionConsolidationModifierResult() {
  }

  /**
   * Record an intersection replaced, with the spatial intersection that replaced it
   *
   * @param replaced the intersection replaced
   * @param replacedBy the spatial intersection replacing it
   */
  public void addReplacedIntersection(Intersection replaced, SpatialIntersection replacedBy) {
    replacedIntersections.add(Pair.of(replaced, replacedBy));
  }

  /**
   * Record an intersection cluster kept as it was because it has more parsed intersections than allowed
   *
   * @param parsedIntersections of the cluster
   */
  public void addIntersectionClusterTooLargeToSubdivide(List<Intersection> parsedIntersections) {
    intersectionClustersTooLargeToSubdivide.add(List.copyOf(parsedIntersections));
  }

  /**
   * Record a node that became a member node without a signal of its own
   *
   * @param node joined
   */
  public void addJoinedNode(Node node) {
    joinedNodes.add(node);
  }

  /**
   * Record an approach segment that became an internal segment because it now starts at a member node
   *
   * @param approachSegment made internal
   */
  public void addApproachSegmentMadeInternal(MacroscopicLinkSegment approachSegment) {
    approachSegmentsMadeInternal.add(approachSegment);
  }

  /**
   * Record an internal link longer than the warning length
   *
   * @param link longer than the warning length
   */
  public void addInternalLinkLongerThanWarningLength(MacroscopicLink link) {
    internalLinksLongerThanWarningLength.add(link);
  }

  /**
   * Record a link made internal because road traffic only reaches it by driving through the intersection
   *
   * @param reachedOnlyThrough the case
   */
  public void addLinkReachedOnlyThroughIntersection(LinkReachedOnlyThroughIntersection reachedOnlyThrough) {
    linksReachedOnlyThroughIntersection.add(reachedOnlyThrough);
  }

  /**
   * Record a crossing intersection that joined a junction along the road
   *
   * @param crossing joined
   */
  public void addCrossingJoined(Intersection crossing) {
    crossingsJoined.add(crossing);
  }

  /**
   * Record a crossing intersection that joined a junction from one of its slip lanes
   *
   * @param crossing joined
   */
  public void addCrossingJoinedFromSlipLane(Intersection crossing) {
    crossingsJoinedFromSlipLane.add(crossing);
  }

  /**
   * Record a segment that enters a signalised junction and allows a motorised road mode, but is not an approach
   *
   * @param segment missing as approach
   */
  public void addMissingApproachSegment(MacroscopicLinkSegment segment) {
    missingApproachSegments.add(segment);
  }

  /**
   * Record a signalised junction where fewer than half of the road approaches have a signal of their own
   *
   * @param junction with few lit arms
   */
  public void addJunctionWithFewLitArms(Intersection junction) {
    junctionsWithFewLitArms.add(junction);
  }

  /**
   * Record a movement stored on a spatial intersection
   *
   * @param movement stored
   */
  public void addStoredSpatialMovement(SpatialMovement movement) {
    storedSpatialMovements.add(movement);
  }

  /**
   * Each intersection replaced, with the spatial intersection that replaced it. Every intersection a spatial
   * intersection is made from is replaced, so joining four gives four pairs
   *
   * @return pairs of replaced intersection and the spatial intersection replacing it, unmodifiable
   */
  public List<Pair<Intersection, SpatialIntersection>> getReplacedIntersections() {
    return Collections.unmodifiableList(replacedIntersections);
  }

  /**
   * Intersection clusters with more parsed intersections than allowed, kept as they were
   *
   * @return the parsed intersections of each such cluster, unmodifiable
   */
  public List<List<Intersection>> getIntersectionClustersTooLargeToSubdivide() {
    return Collections.unmodifiableList(intersectionClustersTooLargeToSubdivide);
  }

  /**
   * Nodes that became member nodes without a signal of their own, such as the far carriageway node of a divided road
   *
   * @return joined nodes, unmodifiable
   */
  public List<Node> getJoinedNodes() {
    return List.copyOf(joinedNodes);
  }

  /**
   * Approach segments that became internal segments because they now start at a member node. Each became an internal
   * segment with a signal when a vehicle signal stands on it, as the caller told consolidation, and a plain one
   * otherwise
   *
   * @return approach segments made internal, unmodifiable
   */
  public List<MacroscopicLinkSegment> getApproachSegmentsMadeInternal() {
    return List.copyOf(approachSegmentsMadeInternal);
  }

  /**
   * Internal links longer than the warning length
   *
   * @return internal links longer than the warning length, unmodifiable
   */
  public List<MacroscopicLink> getInternalLinksLongerThanWarningLength() {
    return List.copyOf(internalLinksLongerThanWarningLength);
  }

  /**
   * Links made internal because road traffic only reaches them by driving through the intersection
   *
   * @return each case, unmodifiable
   */
  public List<LinkReachedOnlyThroughIntersection> getLinksReachedOnlyThroughIntersection() {
    return Collections.unmodifiableList(linksReachedOnlyThroughIntersection);
  }

  /**
   * Crossing intersections that joined a junction along the road
   *
   * @return crossings joined, unmodifiable
   */
  public List<Intersection> getCrossingsJoined() {
    return Collections.unmodifiableList(crossingsJoined);
  }

  /**
   * Crossing intersections that joined a junction from one of its slip lanes
   *
   * @return crossings joined from a slip lane, unmodifiable
   */
  public List<Intersection> getCrossingsJoinedFromSlipLane() {
    return Collections.unmodifiableList(crossingsJoinedFromSlipLane);
  }

  /**
   * Segments that enter a signalised junction and allow a motorised road mode, but are not approaches. They are
   * counted, not added as approaches
   *
   * @return missing approach segments, unmodifiable
   */
  public List<MacroscopicLinkSegment> getMissingApproachSegments() {
    return List.copyOf(missingApproachSegments);
  }

  /**
   * Signalised junctions where fewer than half of the approaches that allow a motorised road mode have a signal of
   * their own
   *
   * @return junctions with few lit arms, unmodifiable
   */
  public List<Intersection> getJunctionsWithFewLitArms() {
    return Collections.unmodifiableList(junctionsWithFewLitArms);
  }

  /**
   * Movements stored on the spatial intersections, each from an approach to an exit
   *
   * @return stored movements, unmodifiable
   */
  public List<SpatialMovement> getStoredSpatialMovements() {
    return Collections.unmodifiableList(storedSpatialMovements);
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
        "few lit arms; stored %d movements",
        replacedIntersections.size(), joinedNodes.size(), crossingsJoined.size(), crossingsJoinedFromSlipLane.size(),
        approachSegmentsMadeInternal.size(), linksReachedOnlyThroughIntersection.size(),
        intersectionClustersTooLargeToSubdivide.size(), internalLinksLongerThanWarningLength.size(),
        missingApproachSegments.size(), junctionsWithFewLitArms.size(), storedSpatialMovements.size());
  }
}
