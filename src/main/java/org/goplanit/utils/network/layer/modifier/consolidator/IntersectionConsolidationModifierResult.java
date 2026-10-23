package org.goplanit.utils.network.layer.modifier.consolidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.network.layer.macroscopic.intersection.Intersection;
import org.goplanit.utils.network.layer.macroscopic.SpatialMovement;
import org.goplanit.utils.network.layer.macroscopic.intersection.SpatialIntersection;
import org.goplanit.utils.network.layer.modifier.MacroscopicNetworkLayerModifier;
import org.goplanit.utils.network.layer.physical.Link;
import org.goplanit.utils.network.layer.physical.LinkSegment;
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
    private final LinkSegment enteringSegment;

    /** the member nodes of the intersection that feed it */
    private final List<Node> feedingNodes;

    /** the longest way back from the node it enters to a node feeding it, along the road, in metres */
    private final double distanceMeters;

    /** the segments made internal: the entering segment and every segment traced back over */
    private final List<LinkSegment> segmentsMadeInternal;

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
        LinkSegment enteringSegment, List<Node> feedingNodes, double distanceMeters,
        List<LinkSegment> segmentsMadeInternal, boolean withSignal) {
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
    public LinkSegment getEnteringSegment() {
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
    public List<LinkSegment> getSegmentsMadeInternal() {
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

  /** What consolidation did to an intersection that the result lists */
  public enum IntersectionOutcome {

    /** a crossing intersection that joined a junction along the road */
    CROSSING_JOINED,

    /** a crossing intersection that joined a junction from one of its slip lanes */
    CROSSING_JOINED_FROM_SLIP_LANE,

    /** a junction pulled into a consolidated intersection whose radius, plus the margin, it lies within, see
     * {@link IntersectionConsolidatorSettings#setPullInMarginBeyondRadiusMeters(double)} */
    JUNCTION_PULLED_IN_WITHIN_RADIUS,

    /** a junction pulled into a consolidated intersection because the road of one of its approaches without a signal
     * leads back to it, within the pull-in margin and a further allowance, so that approach is no longer one */
    JUNCTION_PULLED_IN_TO_SALVAGE_APPROACH_WITHOUT_SIGNAL,

    /** a signalised junction where fewer than half of the approaches that allow a motorised road mode have a signal of
     * their own */
    JUNCTION_WITH_FEW_SIGNALISED_ARMS
  }

  /** What consolidation did to an approach segment that the result lists */
  public enum ApproachOutcome {

    /** an approach segment that became an internal segment because it now starts at a member node. It became an
     * internal segment with a signal when it has a signal of its own, as the caller told consolidation, and a plain
     * one otherwise */
    MADE_INTERNAL,

    /** a segment that enters a signalised junction from a slip lane and allows a motorised road mode, made an approach
     * without a signal of its own. A slip lane often has no signal of its own, so these are expected */
    ADDED_WITHOUT_SIGNAL_ON_SLIP_LANE,

    /** a segment that enters a signalised junction, not from a slip lane, and allows a motorised road mode, made an
     * approach without a signal of its own. A road entering a signalised junction without a signal is unusual, so a
     * signal may be missing from the source */
    ADDED_WITHOUT_SIGNAL_NOT_ON_SLIP_LANE,

    /** a segment that enters a signalised junction and allows a motorised road mode, made an approach with a signal: it
     * continues the same road as an approach whose signal was tagged on the node it entered, and that node became a
     * member node, so the signal now stands where the road enters the junction */
    ADDED_WITH_SIGNAL_TAGGED_ON_NODE_AHEAD
  }

  /** each intersection replaced, with the spatial intersection that replaced it */
  private final List<Pair<Intersection, SpatialIntersection>> replacedIntersections = new ArrayList<>();

  /** intersection clusters too large to try every subdivision, subdivided step by step instead */
  private final List<List<Intersection>> intersectionClustersSubdividedStepByStep = new ArrayList<>();

  /** nodes that became member nodes without a signal of their own, each once */
  private final Set<Node> joinedNodes = new LinkedHashSet<>();

  /** internal links longer than the warning length, each once */
  private final Set<Link> internalLinksLongerThanWarningLength = new LinkedHashSet<>();

  /** links made internal because road traffic only reaches them by driving through the intersection */
  private final List<LinkReachedOnlyThroughIntersection> linksReachedOnlyThroughIntersection = new ArrayList<>();

  /** per outcome, the intersections it happened to, in the order they were recorded */
  private final Map<IntersectionOutcome, List<Intersection>> intersectionsByOutcome =
      new EnumMap<>(IntersectionOutcome.class);

  /** per outcome, the approach segments it happened to, each once, in the order they were recorded */
  private final Map<ApproachOutcome, Set<LinkSegment>> approachesByOutcome =
      new EnumMap<>(ApproachOutcome.class);

  /** movements stored on the spatial intersections */
  private final List<SpatialMovement> storedSpatialMovements = new ArrayList<>();

  /** the ids, as listed, of the nodes listed in the settings to join or to keep apart that no node of the layer has,
   * each once, in the order they were recorded */
  private final Set<String> listedNodeIdsNotFound = new LinkedHashSet<>();

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
   * Record that a spatial intersection consolidation made is itself merged into another: the intersections it replaced
   * are now replaced by the other one. It was never one of the layer's own intersections, so it is not listed itself,
   * and each intersection replaced stays listed once
   *
   * @param madeByConsolidation the spatial intersection consolidation made, merged away
   * @param mergedInto the spatial intersection it is merged into
   */
  public void reassignReplacedIntersections(SpatialIntersection madeByConsolidation, SpatialIntersection mergedInto) {
    replacedIntersections.replaceAll(pair -> pair.second() == madeByConsolidation ? Pair.of(pair.first(), mergedInto)
        : pair);
  }

  /**
   * Record an intersection cluster too large to try every subdivision, subdivided step by step instead
   *
   * @param parsedIntersections of the cluster
   */
  public void addIntersectionClusterSubdividedStepByStep(List<Intersection> parsedIntersections) {
    intersectionClustersSubdividedStepByStep.add(List.copyOf(parsedIntersections));
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
   * Record an intersection something happened to
   *
   * @param outcome what happened to it
   * @param intersection it happened to
   */
  public void addIntersection(IntersectionOutcome outcome, Intersection intersection) {
    intersectionsByOutcome.computeIfAbsent(outcome, key -> new ArrayList<>()).add(intersection);
  }

  /**
   * Record an approach segment something happened to, each segment once per outcome
   *
   * @param outcome what happened to it
   * @param approachSegment it happened to
   */
  public void addApproach(ApproachOutcome outcome, LinkSegment approachSegment) {
    approachesByOutcome.computeIfAbsent(outcome, key -> new LinkedHashSet<>()).add(approachSegment);
  }

  /**
   * Record an internal link longer than the warning length
   *
   * @param link longer than the warning length
   */
  public void addInternalLinkLongerThanWarningLength(Link link) {
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
   * Record a movement stored on a spatial intersection
   *
   * @param movement stored
   */
  public void addStoredSpatialMovement(SpatialMovement movement) {
    storedSpatialMovements.add(movement);
  }

  /**
   * Record an id of a node listed in the settings to join or to keep apart that no node of the layer has, each id once
   *
   * @param nodeId the id, as listed
   */
  public void addListedNodeIdNotFound(String nodeId) {
    listedNodeIdsNotFound.add(nodeId);
  }

  /**
   * Each of the layer's intersections replaced, once, with the spatial intersection that replaced it in the end. Every
   * intersection a spatial intersection is made from is replaced, so joining four gives four pairs. When a spatial
   * intersection consolidation made is merged into another, its pairs name the other one, see
   * {@link #reassignReplacedIntersections(SpatialIntersection, SpatialIntersection)}
   *
   * @return pairs of replaced intersection and the spatial intersection replacing it, unmodifiable
   */
  public List<Pair<Intersection, SpatialIntersection>> getReplacedIntersections() {
    return Collections.unmodifiableList(replacedIntersections);
  }

  /**
   * Intersection clusters too large to try every subdivision, subdivided step by step instead. The step by step search
   * is not sure to find the best subdivision, so these are worth a look
   *
   * @return the parsed intersections of each such cluster, unmodifiable
   */
  public List<List<Intersection>> getIntersectionClustersSubdividedStepByStep() {
    return Collections.unmodifiableList(intersectionClustersSubdividedStepByStep);
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
   * The intersections an outcome happened to
   *
   * @param outcome what happened
   * @return the intersections, in the order they were recorded, unmodifiable
   */
  public List<Intersection> getIntersections(IntersectionOutcome outcome) {
    return Collections.unmodifiableList(intersectionsByOutcome.getOrDefault(outcome, List.of()));
  }

  /**
   * The approach segments an outcome happened to
   *
   * @param outcome what happened
   * @return the approach segments, in the order they were recorded, unmodifiable
   */
  public List<LinkSegment> getApproaches(ApproachOutcome outcome) {
    return List.copyOf(approachesByOutcome.getOrDefault(outcome, Set.of()));
  }

  /**
   * Internal links longer than the warning length
   *
   * @return internal links longer than the warning length, unmodifiable
   */
  public List<Link> getInternalLinksLongerThanWarningLength() {
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
   * Movements stored on the spatial intersections, each from an approach to an exit
   *
   * @return stored movements, unmodifiable
   */
  public List<SpatialMovement> getStoredSpatialMovements() {
    return Collections.unmodifiableList(storedSpatialMovements);
  }

  /**
   * The ids, as listed, of the nodes listed in the settings to join or to keep apart that no node of the layer has
   *
   * @return the ids, in the order they were recorded, unmodifiable
   */
  public List<String> getListedNodeIdsNotFound() {
    return List.copyOf(listedNodeIdsNotFound);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return String.format(
        "replaced %d intersections, pulled in %d junctions within a radius and %d to salvage approaches without a " +
        "signal, joined %d nodes and %d crossings (%d " +
        "from a slip lane), made %d approaches internal and %d links reached only through the intersection; %d " +
        "intersection clusters subdivided step by step, %d internal links longer than the warning length, %d " +
        "approaches added without a signal (%d not on a slip lane), %d junctions with few lit arms; stored %d " +
        "movements, did not find %d nodes listed in the settings",
        replacedIntersections.size(), getIntersections(IntersectionOutcome.JUNCTION_PULLED_IN_WITHIN_RADIUS).size(),
        getIntersections(IntersectionOutcome.JUNCTION_PULLED_IN_TO_SALVAGE_APPROACH_WITHOUT_SIGNAL).size(),
        joinedNodes.size(), getIntersections(IntersectionOutcome.CROSSING_JOINED).size(),
        getIntersections(IntersectionOutcome.CROSSING_JOINED_FROM_SLIP_LANE).size(),
        getApproaches(ApproachOutcome.MADE_INTERNAL).size(), linksReachedOnlyThroughIntersection.size(),
        intersectionClustersSubdividedStepByStep.size(), internalLinksLongerThanWarningLength.size(),
        getApproaches(ApproachOutcome.ADDED_WITHOUT_SIGNAL_ON_SLIP_LANE).size() +
            getApproaches(ApproachOutcome.ADDED_WITHOUT_SIGNAL_NOT_ON_SLIP_LANE).size(),
        getApproaches(ApproachOutcome.ADDED_WITHOUT_SIGNAL_NOT_ON_SLIP_LANE).size(),
        getIntersections(IntersectionOutcome.JUNCTION_WITH_FEW_SIGNALISED_ARMS).size(), storedSpatialMovements.size(),
        listedNodeIdsNotFound.size());
  }
}
