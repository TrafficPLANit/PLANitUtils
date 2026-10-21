package org.goplanit.utils.network.layer.modifier.consolidator;

import org.goplanit.utils.network.layer.modifier.MacroscopicNetworkLayerModifier;

/**
 * Settings of intersection consolidation, see {@link MacroscopicNetworkLayerModifier}. Each value starts at its
 * default
 *
 * @author markr
 */
public class IntersectionConsolidatorSettings {

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
}
