package org.goplanit.utils.network.layer.modifier.consolidator;

import org.goplanit.utils.exceptions.ExceptionUtils;
import org.goplanit.utils.settings.annotations.SettingsKeyLevel;
import org.goplanit.utils.settings.annotations.SettingsRead;
import org.goplanit.utils.settings.annotations.SettingsWrite;

/**
 * The intersection consolidation settings a settings file can set: only the methods of its keys, each write with the
 * read of the same key. {@link IntersectionConsolidatorSettings} extends it with what code needs on top.
 * <p>
 * The values here tune how consolidation groups parsed intersections, and a settings file can give each of them; the
 * lists of nodes to join or keep apart name nodes by the ids a converter gives them, so a converter's own settings
 * add those, and they stay on the class code uses
 * </p>
 *
 * @author markr
 */
public abstract class IntersectionConsolidatorYamlKeyedSettings {

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

  /** see {@link #setMaximumDistanceBetweenCrossingsSideBySideMeters(double)} */
  private double maximumDistanceBetweenCrossingsSideBySideMeters =
      DEFAULT_MAXIMUM_DISTANCE_BETWEEN_CROSSINGS_SIDE_BY_SIDE_METERS;

  /** see {@link #setIncludeActiveModesInMovements(boolean)} */
  private boolean includeActiveModesInMovements = DEFAULT_INCLUDE_ACTIVE_MODES_IN_MOVEMENTS;

  /**
   * Constructor, every value at its default
   */
  protected IntersectionConsolidatorYamlKeyedSettings() {
  }

  /**
   * Copy constructor
   *
   * @param other to copy the values of
   */
  protected IntersectionConsolidatorYamlKeyedSettings(IntersectionConsolidatorYamlKeyedSettings other) {
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
    this.includeActiveModesInMovements = other.includeActiveModesInMovements;
  }

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

  /** by default movements are for motorised road and rail modes only, the traffic a signal controls */
  public static final boolean DEFAULT_INCLUDE_ACTIVE_MODES_IN_MOVEMENTS = false;

  /**
   * Joining reach: how far apart along the road the parts of one group may be, see
   * {@link #setMaximumJoiningReachMeters(double)}
   *
   * @return reach in metres
   */
  @SettingsRead(key = "maximumJoiningReachMeters")
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
  @SettingsWrite(key = "maximumJoiningReachMeters", level = SettingsKeyLevel.UNCOMMON,
      description = "How far apart along the road the parts of one consolidated intersection may be, in metres")
  public void setMaximumJoiningReachMeters(double maximumJoiningReachMeters) {
    this.maximumJoiningReachMeters = maximumJoiningReachMeters;
  }

  /**
   * Least gain in coverage a member must bring to its cluster subset
   *
   * @return least gain
   */
  @SettingsRead(key = "minimumCoverageGainPerMember")
  public double getMinimumCoverageGainPerMember() {
    return minimumCoverageGainPerMember;
  }

  /**
   * Set the least gain in coverage a member must bring to its cluster subset
   *
   * @param minimumCoverageGainPerMember least gain
   */
  @SettingsWrite(key = "minimumCoverageGainPerMember", level = SettingsKeyLevel.RARE,
      description = "Least gain in coverage a member must bring to its part of an intersection cluster")
  public void setMinimumCoverageGainPerMember(double minimumCoverageGainPerMember) {
    this.minimumCoverageGainPerMember = minimumCoverageGainPerMember;
  }

  /**
   * Margin within which the totals of two subdivisions count as a tie
   *
   * @return margin
   */
  @SettingsRead(key = "coverageTieMargin")
  public double getCoverageTieMargin() {
    return coverageTieMargin;
  }

  /**
   * Set the margin within which the totals of two subdivisions count as a tie, won by the one with fewer cluster
   * subsets
   *
   * @param coverageTieMargin margin
   */
  @SettingsWrite(key = "coverageTieMargin", level = SettingsKeyLevel.RARE,
      description = "Margin within which the totals of two subdivisions of a cluster count as a tie")
  public void setCoverageTieMargin(double coverageTieMargin) {
    this.coverageTieMargin = coverageTieMargin;
  }

  /**
   * Largest number of parsed intersections in an intersection cluster for which every subdivision is tried
   *
   * @return largest number
   */
  @SettingsRead(key = "maximumParsedIntersectionsToTryEverySubdivision")
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
  @SettingsWrite(key = "maximumParsedIntersectionsToTryEverySubdivision", level = SettingsKeyLevel.RARE,
      description = "Largest intersection cluster, in parsed intersections, for which every subdivision is tried")
  public void setMaximumParsedIntersectionsToTryEverySubdivision(int maximumParsedIntersectionsToTryEverySubdivision) {
    this.maximumParsedIntersectionsToTryEverySubdivision = maximumParsedIntersectionsToTryEverySubdivision;
  }

  /**
   * Number of subdivisions kept after each step of the search through a cluster too large to try every subdivision
   *
   * @return number kept per step
   */
  @SettingsRead(key = "subdivisionsKeptPerSearchStep")
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
   * @throws IllegalArgumentException naming the number, keeping the one before, when it is less than one
   */
  @SettingsWrite(key = "subdivisionsKeptPerSearchStep", level = SettingsKeyLevel.RARE,
      description = "Subdivisions kept after each step of the search through a larger cluster, at least one")
  public void setSubdivisionsKeptPerSearchStep(int subdivisionsKeptPerSearchStep) {
    ExceptionUtils.throwIllegalSettingValueIf(subdivisionsKeptPerSearchStep < 1, "Subdivisions kept per search step",
        subdivisionsKeptPerSearchStep, "1 or more");
    this.subdivisionsKeptPerSearchStep = subdivisionsKeptPerSearchStep;
  }

  /**
   * Distance beyond its radius within which a consolidated intersection pulls in other junctions
   *
   * @return margin in metres
   */
  @SettingsRead(key = "pullInMarginBeyondRadiusMeters")
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
  @SettingsWrite(key = "pullInMarginBeyondRadiusMeters", level = SettingsKeyLevel.UNCOMMON,
      description = "Distance beyond its radius within which a consolidated intersection pulls in other junctions, " +
          "in metres")
  public void setPullInMarginBeyondRadiusMeters(double pullInMarginBeyondRadiusMeters) {
    this.pullInMarginBeyondRadiusMeters = pullInMarginBeyondRadiusMeters;
  }

  /**
   * Longest connection, over any links, along which a junction is pulled in
   *
   * @return length in metres
   */
  @SettingsRead(key = "maximumPullInConnectionLengthMeters")
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
  @SettingsWrite(key = "maximumPullInConnectionLengthMeters", level = SettingsKeyLevel.UNCOMMON,
      description = "Longest connection, over any links, along which a junction is pulled in, in metres")
  public void setMaximumPullInConnectionLengthMeters(double maximumPullInConnectionLengthMeters) {
    this.maximumPullInConnectionLengthMeters = maximumPullInConnectionLengthMeters;
  }

  /**
   * Length above which an internal link is listed in the result
   *
   * @return length in metres
   */
  @SettingsRead(key = "internalLinkLengthWarningMeters")
  public double getInternalLinkLengthWarningMeters() {
    return internalLinkLengthWarningMeters;
  }

  /**
   * Set the length above which an internal link is listed in the result
   *
   * @param internalLinkLengthWarningMeters length in metres
   */
  @SettingsWrite(key = "internalLinkLengthWarningMeters", level = SettingsKeyLevel.RARE,
      description = "Length above which an internal link is listed with a warning, in metres")
  public void setInternalLinkLengthWarningMeters(double internalLinkLengthWarningMeters) {
    this.internalLinkLengthWarningMeters = internalLinkLengthWarningMeters;
  }

  /**
   * How far back along the road a link entering an intersection may be fed only from that intersection, unless the
   * intersection's radius times {@link #getMaximumDistanceReachedOnlyThroughIntersectionRadiusFactor()} is further
   *
   * @return distance in metres
   */
  @SettingsRead(key = "maximumDistanceReachedOnlyThroughIntersectionMeters")
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
  @SettingsWrite(key = "maximumDistanceReachedOnlyThroughIntersectionMeters", level = SettingsKeyLevel.UNCOMMON,
      description = "How far back along the road a link entering an intersection may be fed only from it, to become " +
          "internal, in metres")
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
  @SettingsRead(key = "maximumDistanceReachedOnlyThroughIntersectionRadiusFactor")
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
  @SettingsWrite(key = "maximumDistanceReachedOnlyThroughIntersectionRadiusFactor", level = SettingsKeyLevel.UNCOMMON,
      description = "Factor on an intersection's radius for how far back a link may be fed only from it, when that " +
          "is further")
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
  @SettingsRead(key = "maximumDistanceSlipLaneCrossingToJunctionMeters")
  public double getMaximumDistanceSlipLaneCrossingToJunctionMeters() {
    return maximumDistanceSlipLaneCrossingToJunctionMeters;
  }

  /**
   * Set how far a crossing on a slip lane may be from a member node of the junction it joins
   *
   * @param maximumDistanceSlipLaneCrossingToJunctionMeters distance in metres
   */
  @SettingsWrite(key = "maximumDistanceSlipLaneCrossingToJunctionMeters", level = SettingsKeyLevel.UNCOMMON,
      description = "Furthest a crossing on a slip lane may be from a member node of the junction it joins, in metres")
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
  @SettingsRead(key = "maximumDistanceBetweenCrossingsSideBySideMeters")
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
  @SettingsWrite(key = "maximumDistanceBetweenCrossingsSideBySideMeters", level = SettingsKeyLevel.UNCOMMON,
      description = "Furthest apart along the road two crossings side by side may be to join into one, in metres")
  public void setMaximumDistanceBetweenCrossingsSideBySideMeters(
      double maximumDistanceBetweenCrossingsSideBySideMeters) {
    this.maximumDistanceBetweenCrossingsSideBySideMeters = maximumDistanceBetweenCrossingsSideBySideMeters;
  }

  /**
   * Whether movements are stored for active modes too, see {@link #setIncludeActiveModesInMovements(boolean)}
   *
   * @return true when for active modes too, false when for motorised road and rail modes only
   */
  @SettingsRead(key = "includeActiveModesInMovements")
  public boolean isIncludeActiveModesInMovements() {
    return includeActiveModesInMovements;
  }

  /**
   * Set whether the movements of a consolidated intersection are stored for active modes too, such as pedestrians and
   * bicycles. Without them, a movement exists only when a motorised road or rail mode can get from its approach over
   * the internal segments onto its exit, the traffic a signal controls. With them, a movement also exists where only
   * an active mode can, such as from a road approach onto a footway. Collapsing an intersection for an active mode
   * needs them, since a collapsed mode keeps only the turns its movements give it
   *
   * @param includeActiveModesInMovements true for active modes too, false for motorised road and rail modes only
   */
  @SettingsWrite(key = "includeActiveModesInMovements", level = SettingsKeyLevel.UNCOMMON,
      description = "Store the movements of active modes too, such as pedestrians, which collapsing them needs")
  public void setIncludeActiveModesInMovements(boolean includeActiveModesInMovements) {
    this.includeActiveModesInMovements = includeActiveModesInMovements;
  }
}
