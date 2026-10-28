package org.goplanit.utils.network.layer.modifier.consolidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.goplanit.utils.id.IdMapperType;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.network.layer.modifier.MacroscopicNetworkLayerModifier;

/**
 * Settings of intersection consolidation, see {@link MacroscopicNetworkLayerModifier}. Each value starts at its
 * default. The lists of nodes to join into one intersection, or to keep in separate intersections, override the
 * grouping consolidation chooses, and are empty by default
 *
 * @author markr
 */
public class IntersectionConsolidatorSettings {

  /** the logger */
  private static final Logger LOGGER = Logger.getLogger(IntersectionConsolidatorSettings.class.getCanonicalName());

  /**
   * Whether the node ids name at most one node, so there is nothing to join or keep apart. For example, "12", "12"
   * names one node
   *
   * @param nodeIds to check
   * @return true when they hold fewer than two different ids
   */
  private static boolean isSingleNodeOrNone(Collection<String> nodeIds) {
    return nodeIds.stream().distinct().count() < 2;
  }

  /** see {@link #setMaximumDistanceBetweenCrossingsSideBySideMeters(double)} */
  private double maximumDistanceBetweenCrossingsSideBySideMeters =
      DEFAULT_MAXIMUM_DISTANCE_BETWEEN_CROSSINGS_SIDE_BY_SIDE_METERS;

  /** default joining reach: within it along the road parsed intersections, crossings and nodes join one group, in
   * metres */
  public static final double DEFAULT_MAXIMUM_JOINING_REACH_METERS = 40;

  /** default least gain in coverage a member must bring to its cluster subset */
  public static final double DEFAULT_MINIMUM_COVERAGE_GAIN_PER_MEMBER = 0.05;

  /** default margin within which the totals of two subdivisions count as a tie */
  public static final double DEFAULT_COVERAGE_TIE_MARGIN = 0.05;

  /** default largest number of parsed intersections in an intersection cluster for which every subdivision is tried */
  public static final int DEFAULT_MAXIMUM_PARSED_INTERSECTIONS_TO_TRY_EVERY_SUBDIVISION = 10;

  /** default number of subdivisions kept after each step of the search through a larger intersection cluster */
  public static final int DEFAULT_SUBDIVISIONS_KEPT_PER_SEARCH_STEP = 3;

  /** default distance beyond its radius within which a consolidated intersection pulls in other junctions, in metres */
  public static final double DEFAULT_PULL_IN_MARGIN_BEYOND_RADIUS_METERS = 30;

  /** default longest connection, over any links, along which a junction is pulled in, in metres */
  public static final double DEFAULT_MAXIMUM_PULL_IN_CONNECTION_LENGTH_METERS = 80;

  /** default length above which an internal link is listed with a warning, in metres */
  public static final double DEFAULT_INTERNAL_LINK_LENGTH_WARNING_METERS = 100;

  /** default reach back along the road for a link entering an intersection that is reached only through it, in
   * metres: enough for a road leaving one junction of an intersection and coming back into another across a tram
   * corridor; a large intersection reaches further, see
   * {@link #DEFAULT_MAXIMUM_DISTANCE_REACHED_ONLY_THROUGH_INTERSECTION_RADIUS_FACTOR} */
  public static final double DEFAULT_MAXIMUM_DISTANCE_REACHED_ONLY_THROUGH_INTERSECTION_METERS = 60;

  /** default factor on an intersection's radius for that reach, when longer: twice the radius, the width of the
   * intersection, so a road between two of its junctions may be as long as the intersection is wide */
  public static final double DEFAULT_MAXIMUM_DISTANCE_REACHED_ONLY_THROUGH_INTERSECTION_RADIUS_FACTOR = 2;

  /** default reach from a crossing on a slip lane to a member node of its junction, in metres */
  public static final double DEFAULT_MAXIMUM_DISTANCE_SLIP_LANE_CROSSING_TO_JUNCTION_METERS = 60;

  /** default distance along the road between two crossings side by side that join into one, in metres */
  public static final double DEFAULT_MAXIMUM_DISTANCE_BETWEEN_CROSSINGS_SIDE_BY_SIDE_METERS = 30;

  /** see {@link #setMaximumJoiningReachMeters(double)} */
  private double maximumJoiningReachMeters = DEFAULT_MAXIMUM_JOINING_REACH_METERS;

  /** see {@link #setMinimumCoverageGainPerMember(double)} */
  private double minimumCoverageGainPerMember = DEFAULT_MINIMUM_COVERAGE_GAIN_PER_MEMBER;

  /** see {@link #setCoverageTieMargin(double)} */
  private double coverageTieMargin = DEFAULT_COVERAGE_TIE_MARGIN;

  /** see {@link #setMaximumParsedIntersectionsToTryEverySubdivision(int)} */
  private int maximumParsedIntersectionsToTryEverySubdivision =
      DEFAULT_MAXIMUM_PARSED_INTERSECTIONS_TO_TRY_EVERY_SUBDIVISION;

  /** see {@link #setSubdivisionsKeptPerSearchStep(int)} */
  private int subdivisionsKeptPerSearchStep = DEFAULT_SUBDIVISIONS_KEPT_PER_SEARCH_STEP;

  /** see {@link #setPullInMarginBeyondRadiusMeters(double)} */
  private double pullInMarginBeyondRadiusMeters = DEFAULT_PULL_IN_MARGIN_BEYOND_RADIUS_METERS;

  /** see {@link #setMaximumPullInConnectionLengthMeters(double)} */
  private double maximumPullInConnectionLengthMeters = DEFAULT_MAXIMUM_PULL_IN_CONNECTION_LENGTH_METERS;

  /** see {@link #setInternalLinkLengthWarningMeters(double)} */
  private double internalLinkLengthWarningMeters = DEFAULT_INTERNAL_LINK_LENGTH_WARNING_METERS;

  /** see {@link #setMaximumDistanceReachedOnlyThroughIntersectionMeters(double)} */
  private double maximumDistanceReachedOnlyThroughIntersectionMeters =
      DEFAULT_MAXIMUM_DISTANCE_REACHED_ONLY_THROUGH_INTERSECTION_METERS;

  /** see {@link #setMaximumDistanceReachedOnlyThroughIntersectionRadiusFactor(double)} */
  private double maximumDistanceReachedOnlyThroughIntersectionRadiusFactor =
      DEFAULT_MAXIMUM_DISTANCE_REACHED_ONLY_THROUGH_INTERSECTION_RADIUS_FACTOR;

  /** see {@link #setMaximumDistanceSlipLaneCrossingToJunctionMeters(double)} */
  private double maximumDistanceSlipLaneCrossingToJunctionMeters =
      DEFAULT_MAXIMUM_DISTANCE_SLIP_LANE_CROSSING_TO_JUNCTION_METERS;

  /** see {@link #addNodeIdsToJoinIntoOneIntersection(Collection, IdMapperType)}, empty by default */
  private final List<Pair<Set<String>, IdMapperType>> nodeIdsToJoinIntoOneIntersection = new ArrayList<>();

  /** see {@link #addNodeIdsToKeepInSeparateIntersections(Collection, IdMapperType)}, empty by default */
  private final List<Pair<Set<String>, IdMapperType>> nodeIdsToKeepInSeparateIntersections = new ArrayList<>();

  /**
   * Constructor, every value at its default
   */
  public IntersectionConsolidatorSettings() {
  }

  /**
   * Copy constructor
   *
   * @param other to copy
   */
  public IntersectionConsolidatorSettings(IntersectionConsolidatorSettings other) {
    this.maximumJoiningReachMeters = other.maximumJoiningReachMeters;
    this.minimumCoverageGainPerMember = other.minimumCoverageGainPerMember;
    this.coverageTieMargin = other.coverageTieMargin;
    this.maximumParsedIntersectionsToTryEverySubdivision = other.maximumParsedIntersectionsToTryEverySubdivision;
    this.subdivisionsKeptPerSearchStep = other.subdivisionsKeptPerSearchStep;
    this.pullInMarginBeyondRadiusMeters = other.pullInMarginBeyondRadiusMeters;
    this.maximumPullInConnectionLengthMeters = other.maximumPullInConnectionLengthMeters;
    this.internalLinkLengthWarningMeters = other.internalLinkLengthWarningMeters;
    this.maximumDistanceReachedOnlyThroughIntersectionMeters =
        other.maximumDistanceReachedOnlyThroughIntersectionMeters;
    this.maximumDistanceReachedOnlyThroughIntersectionRadiusFactor =
        other.maximumDistanceReachedOnlyThroughIntersectionRadiusFactor;
    this.maximumDistanceSlipLaneCrossingToJunctionMeters = other.maximumDistanceSlipLaneCrossingToJunctionMeters;
    this.maximumDistanceBetweenCrossingsSideBySideMeters = other.maximumDistanceBetweenCrossingsSideBySideMeters;
    other.nodeIdsToJoinIntoOneIntersection.forEach(list -> this.nodeIdsToJoinIntoOneIntersection.add(
        Pair.of(new LinkedHashSet<>(list.first()), list.second())));
    other.nodeIdsToKeepInSeparateIntersections.forEach(list -> this.nodeIdsToKeepInSeparateIntersections.add(
        Pair.of(new LinkedHashSet<>(list.first()), list.second())));
  }

  /**
   * Joining reach: how far apart along the road the parts of one group may be, see
   * {@link #setMaximumJoiningReachMeters(double)}
   *
   * @return reach in metres
   */
  public double getMaximumJoiningReachMeters() {
    return maximumJoiningReachMeters;
  }

  /**
   * Set the joining reach: how far apart along the road the parts of one group may be. Parsed intersections within it
   * of each other, in either direction, form an intersection cluster, and only links within it count when the ways to
   * divide a cluster are scored. A crossing joins a junction within it along the road, and a node a group encloses
   * joins over links within it. It decides what joins a group, not what becomes internal inside one: a road reached
   * only through the group becomes internal over a longer distance, see
   * {@link #setMaximumDistanceReachedOnlyThroughIntersectionMeters(double)}
   *
   * @param maximumJoiningReachMeters reach in metres
   */
  public void setMaximumJoiningReachMeters(double maximumJoiningReachMeters) {
    this.maximumJoiningReachMeters = maximumJoiningReachMeters;
  }

  /**
   * Least gain in coverage a member must bring to its cluster subset
   *
   * @return least gain
   */
  public double getMinimumCoverageGainPerMember() {
    return minimumCoverageGainPerMember;
  }

  /**
   * Set the least gain in coverage a member must bring to its cluster subset
   *
   * @param minimumCoverageGainPerMember least gain
   */
  public void setMinimumCoverageGainPerMember(double minimumCoverageGainPerMember) {
    this.minimumCoverageGainPerMember = minimumCoverageGainPerMember;
  }

  /**
   * Margin within which the totals of two subdivisions count as a tie
   *
   * @return margin
   */
  public double getCoverageTieMargin() {
    return coverageTieMargin;
  }

  /**
   * Set the margin within which the totals of two subdivisions count as a tie, won by the one with fewer cluster
   * subsets
   *
   * @param coverageTieMargin margin
   */
  public void setCoverageTieMargin(double coverageTieMargin) {
    this.coverageTieMargin = coverageTieMargin;
  }

  /**
   * Largest number of parsed intersections in an intersection cluster for which every subdivision is tried
   *
   * @return largest number
   */
  public int getMaximumParsedIntersectionsToTryEverySubdivision() {
    return maximumParsedIntersectionsToTryEverySubdivision;
  }

  /**
   * Set the largest number of parsed intersections in an intersection cluster for which every subdivision is tried.
   * The number of subdivisions grows very fast, 115975 for ten, so a larger cluster is subdivided step by step instead,
   * see {@link #setSubdivisionsKeptPerSearchStep(int)}, and listed in the result
   *
   * @param maximumParsedIntersectionsToTryEverySubdivision largest number
   */
  public void setMaximumParsedIntersectionsToTryEverySubdivision(int maximumParsedIntersectionsToTryEverySubdivision) {
    this.maximumParsedIntersectionsToTryEverySubdivision = maximumParsedIntersectionsToTryEverySubdivision;
  }

  /**
   * Number of subdivisions kept after each step of the search through a cluster too large to try every subdivision
   *
   * @return number kept per step
   */
  public int getSubdivisionsKeptPerSearchStep() {
    return subdivisionsKeptPerSearchStep;
  }

  /**
   * Set the number of subdivisions kept after each step of the search through a cluster too large to try every
   * subdivision. The search starts from the whole cluster as one cluster subset. Each step moves one parsed
   * intersection to another cluster subset, or out into one of its own, from each subdivision kept, and keeps the best
   * of the new ones. More kept finds better subdivisions, at a higher cost; with one kept, each step takes the single
   * best move
   *
   * @param subdivisionsKeptPerSearchStep number kept per step, at least one
   */
  public void setSubdivisionsKeptPerSearchStep(int subdivisionsKeptPerSearchStep) {
    this.subdivisionsKeptPerSearchStep = subdivisionsKeptPerSearchStep;
  }

  /**
   * Distance beyond its radius within which a consolidated intersection pulls in other junctions
   *
   * @return margin in metres
   */
  public double getPullInMarginBeyondRadiusMeters() {
    return pullInMarginBeyondRadiusMeters;
  }

  /**
   * Set the distance beyond its radius within which a consolidated intersection pulls in other junctions. Its radius is
   * the distance from the centre of its member nodes to the furthest of them. A junction with a node within the radius
   * plus this margin of the centre, and connected to a member node within
   * {@link #setMaximumPullInConnectionLengthMeters(double)}, is pulled in: a signal inside a junction is part of it,
   * even when the scoring kept it apart or no road brought it into the cluster. The centre and radius are then worked
   * out again, until nothing more is pulled in. A junction the road of an approach without a signal leads back to may
   * lie further than this margin, so that approach is no longer one
   *
   * @param pullInMarginBeyondRadiusMeters margin in metres
   */
  public void setPullInMarginBeyondRadiusMeters(double pullInMarginBeyondRadiusMeters) {
    this.pullInMarginBeyondRadiusMeters = pullInMarginBeyondRadiusMeters;
  }

  /**
   * Longest connection, over any links, along which a junction is pulled in
   *
   * @return length in metres
   */
  public double getMaximumPullInConnectionLengthMeters() {
    return maximumPullInConnectionLengthMeters;
  }

  /**
   * Set the longest connection along which a junction is pulled into a consolidated intersection, see
   * {@link #setPullInMarginBeyondRadiusMeters(double)}. The connection runs from one of its member nodes over any
   * links, road, tram, bicycle or foot, so junctions that only a tram track or a footway joins are pulled in too
   *
   * @param maximumPullInConnectionLengthMeters length in metres
   */
  public void setMaximumPullInConnectionLengthMeters(double maximumPullInConnectionLengthMeters) {
    this.maximumPullInConnectionLengthMeters = maximumPullInConnectionLengthMeters;
  }

  /**
   * Length above which an internal link is listed in the result
   *
   * @return length in metres
   */
  public double getInternalLinkLengthWarningMeters() {
    return internalLinkLengthWarningMeters;
  }

  /**
   * Set the length above which an internal link is listed in the result
   *
   * @param internalLinkLengthWarningMeters length in metres
   */
  public void setInternalLinkLengthWarningMeters(double internalLinkLengthWarningMeters) {
    this.internalLinkLengthWarningMeters = internalLinkLengthWarningMeters;
  }

  /**
   * How far back along the road a link entering an intersection may be fed only from that intersection, unless the
   * intersection's radius times {@link #getMaximumDistanceReachedOnlyThroughIntersectionRadiusFactor()} is further
   *
   * @return distance in metres
   */
  public double getMaximumDistanceReachedOnlyThroughIntersectionMeters() {
    return maximumDistanceReachedOnlyThroughIntersectionMeters;
  }

  /**
   * Set how far back along the road a link entering an intersection may be fed only from that intersection, for it
   * to become internal. A large intersection reaches further, see
   * {@link #setMaximumDistanceReachedOnlyThroughIntersectionRadiusFactor(double)}
   *
   * @param maximumDistanceReachedOnlyThroughIntersectionMeters distance in metres
   */
  public void setMaximumDistanceReachedOnlyThroughIntersectionMeters(
      double maximumDistanceReachedOnlyThroughIntersectionMeters) {
    this.maximumDistanceReachedOnlyThroughIntersectionMeters = maximumDistanceReachedOnlyThroughIntersectionMeters;
  }

  /**
   * The factor on an intersection's radius that gives how far back along the road a link entering it may be fed only
   * from that intersection, when further than {@link #getMaximumDistanceReachedOnlyThroughIntersectionMeters()}
   *
   * @return the factor
   */
  public double getMaximumDistanceReachedOnlyThroughIntersectionRadiusFactor() {
    return maximumDistanceReachedOnlyThroughIntersectionRadiusFactor;
  }

  /**
   * Set the factor on an intersection's radius that gives how far back along the road a link entering it may be fed
   * only from that intersection, for it to become internal, when further than
   * {@link #getMaximumDistanceReachedOnlyThroughIntersectionMeters()}. A large intersection has longer roads between
   * its junctions, so it looks further back. The radius is the distance from the centre of the member nodes a
   * motorised road or rail mode uses to the furthest of them
   *
   * @param maximumDistanceReachedOnlyThroughIntersectionRadiusFactor the factor
   */
  public void setMaximumDistanceReachedOnlyThroughIntersectionRadiusFactor(
      double maximumDistanceReachedOnlyThroughIntersectionRadiusFactor) {
    this.maximumDistanceReachedOnlyThroughIntersectionRadiusFactor =
        maximumDistanceReachedOnlyThroughIntersectionRadiusFactor;
  }

  /**
   * How far a crossing on a slip lane may be from a member node of the junction it joins
   *
   * @return distance in metres
   */
  public double getMaximumDistanceSlipLaneCrossingToJunctionMeters() {
    return maximumDistanceSlipLaneCrossingToJunctionMeters;
  }

  /**
   * Set how far a crossing on a slip lane may be from a member node of the junction it joins
   *
   * @param maximumDistanceSlipLaneCrossingToJunctionMeters distance in metres
   */
  public void setMaximumDistanceSlipLaneCrossingToJunctionMeters(
      double maximumDistanceSlipLaneCrossingToJunctionMeters) {
    this.maximumDistanceSlipLaneCrossingToJunctionMeters = maximumDistanceSlipLaneCrossingToJunctionMeters;
  }

  /**
   * How far apart along the road two crossings side by side may be to join into one, see
   * {@link #setMaximumDistanceBetweenCrossingsSideBySideMeters(double)}
   *
   * @return distance in metres
   */
  public double getMaximumDistanceBetweenCrossingsSideBySideMeters() {
    return maximumDistanceBetweenCrossingsSideBySideMeters;
  }

  /**
   * Set how far apart along the road two crossing intersections may be to join into one, such as a cycle crossing
   * beside a foot crossing. Between them the road must simply continue, or pass other crossings, never a junction.
   * Road traffic is then held once, at the road entering the first
   *
   * @param maximumDistanceBetweenCrossingsSideBySideMeters distance in metres
   */
  public void setMaximumDistanceBetweenCrossingsSideBySideMeters(
      double maximumDistanceBetweenCrossingsSideBySideMeters) {
    this.maximumDistanceBetweenCrossingsSideBySideMeters = maximumDistanceBetweenCrossingsSideBySideMeters;
  }

  /**
   * The lists of nodes to join into one intersection, each with the kind of id its node ids are
   *
   * @return the lists in the order added, unmodifiable, empty when none
   */
  public List<Pair<Set<String>, IdMapperType>> getNodeIdsToJoinIntoOneIntersection() {
    return Collections.unmodifiableList(nodeIdsToJoinIntoOneIntersection);
  }

  /**
   * Add a list of nodes to join into one intersection. This overrides the grouping consolidation chooses. Once the
   * crossings have joined, the intersections holding the listed nodes are joined into one, with the nodes on the
   * shortest roads between them within the joining reach. So any node of an intersection will do, and two nodes of
   * two larger intersections join those intersections as a whole. The steps from the pull-ins to the crossings then
   * run again for it, with its larger radius, before its approaches and movements are worked out. A crossing that
   * joined one of the intersections before stays in it. A crossing intersection named in a list joins even where the
   * crossing rules would not join it. A node in no intersection then has no effect. A list is not joined when it
   * would put nodes listed to keep in separate intersections into one, see
   * {@link #addNodeIdsToKeepInSeparateIntersections(Collection, IdMapperType)}. Lists that share an intersection join
   * into one, in the order added. A list needs at least two different node ids: a shorter one is ignored with a
   * warning. Example: two parts of a large junction that consolidation left as two intersections. Each call adds one
   * list
   *
   * @param nodeIds the ids of the nodes to join
   * @param nodeIdType the kind of id the node ids are, for example the external id
   */
  public void addNodeIdsToJoinIntoOneIntersection(Collection<String> nodeIds, IdMapperType nodeIdType) {
    if (isSingleNodeOrNone(nodeIds)) {
      LOGGER.warning(String.format(
          "Ignored a list of nodes to join into one intersection with fewer than two node ids: %s", nodeIds));
      return;
    }
    nodeIdsToJoinIntoOneIntersection.add(Pair.of(new LinkedHashSet<>(nodeIds), nodeIdType));
  }

  /**
   * The lists of nodes to keep in separate intersections, each with the kind of id its node ids are
   *
   * @return the lists in the order added, unmodifiable, empty when none
   */
  public List<Pair<Set<String>, IdMapperType>> getNodeIdsToKeepInSeparateIntersections() {
    return Collections.unmodifiableList(nodeIdsToKeepInSeparateIntersections);
  }

  /**
   * Add a list of nodes to keep in separate intersections. No grouping step puts two nodes of one list into one
   * intersection. So the subdivision of an intersection cluster that would do so is not allowed and the next best one
   * is chosen, a junction that would do so is not pulled in, a crossing joins the next nearest junction instead, a node
   * that would do so does not join, and a list to join that would do so is not joined. Nodes of one list that are in
   * one intersection before consolidation cannot be split, so they count as one. A list left with nothing to keep
   * apart has no effect. A list needs at least two different node ids: a shorter one is ignored with a warning.
   * Example: two signalised junctions 30 m apart that run as separate controllers. Each call adds one list
   *
   * @param nodeIds the ids of the nodes to keep in separate intersections
   * @param nodeIdType the kind of id the node ids are, for example the external id
   */
  public void addNodeIdsToKeepInSeparateIntersections(Collection<String> nodeIds, IdMapperType nodeIdType) {
    if (isSingleNodeOrNone(nodeIds)) {
      LOGGER.warning(String.format(
          "Ignored a list of nodes to keep in separate intersections with fewer than two node ids: %s", nodeIds));
      return;
    }
    nodeIdsToKeepInSeparateIntersections.add(Pair.of(new LinkedHashSet<>(nodeIds), nodeIdType));
  }
}
