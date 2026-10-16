package org.goplanit.utils.network.layer.modifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.goplanit.utils.mode.Mode;

/**
 * Settings of intersection consolidation, see {@link MacroscopicNetworkLayerModifier}. Each value starts at its
 * default, and no mode is collapsed
 *
 * @author markr
 */
public class IntersectionConsolidatorSettings {

  /** default reach between parsed intersections along the road, and the longest link used for scoring, in metres */
  public static final double DEFAULT_MAXIMUM_INTERNAL_LINK_LENGTH_METERS = 25;

  /** default least gain in coverage a member must bring to its cluster subset */
  public static final double DEFAULT_MINIMUM_COVERAGE_GAIN_PER_MEMBER = 0.05;

  /** default margin within which the totals of two subdivisions count as a tie */
  public static final double DEFAULT_COVERAGE_TIE_MARGIN = 0.001;

  /** default largest number of parsed intersections in an intersection cluster that is subdivided */
  public static final int DEFAULT_MAXIMUM_PARSED_INTERSECTIONS_PER_INTERSECTION_CLUSTER = 10;

  /** default length above which an internal link is listed with a warning, in metres */
  public static final double DEFAULT_INTERNAL_LINK_LENGTH_WARNING_METERS = 100;

  /** default reach back along the road for a link entering an intersection that is reached only through it, in
   * metres */
  public static final double DEFAULT_MAXIMUM_DISTANCE_REACHED_ONLY_THROUGH_INTERSECTION_METERS = 50;

  /** default reach from a crossing on a slip lane to a member node of its junction, in metres */
  public static final double DEFAULT_MAXIMUM_DISTANCE_SLIP_LANE_CROSSING_TO_JUNCTION_METERS = 60;

  /** see {@link #setMaximumInternalLinkLengthMeters(double)} */
  private double maximumInternalLinkLengthMeters = DEFAULT_MAXIMUM_INTERNAL_LINK_LENGTH_METERS;

  /** see {@link #setMinimumCoverageGainPerMember(double)} */
  private double minimumCoverageGainPerMember = DEFAULT_MINIMUM_COVERAGE_GAIN_PER_MEMBER;

  /** see {@link #setCoverageTieMargin(double)} */
  private double coverageTieMargin = DEFAULT_COVERAGE_TIE_MARGIN;

  /** see {@link #setMaximumParsedIntersectionsPerIntersectionCluster(int)} */
  private int maximumParsedIntersectionsPerIntersectionCluster =
      DEFAULT_MAXIMUM_PARSED_INTERSECTIONS_PER_INTERSECTION_CLUSTER;

  /** see {@link #setInternalLinkLengthWarningMeters(double)} */
  private double internalLinkLengthWarningMeters = DEFAULT_INTERNAL_LINK_LENGTH_WARNING_METERS;

  /** see {@link #setMaximumDistanceReachedOnlyThroughIntersectionMeters(double)} */
  private double maximumDistanceReachedOnlyThroughIntersectionMeters =
      DEFAULT_MAXIMUM_DISTANCE_REACHED_ONLY_THROUGH_INTERSECTION_METERS;

  /** see {@link #setMaximumDistanceSlipLaneCrossingToJunctionMeters(double)} */
  private double maximumDistanceSlipLaneCrossingToJunctionMeters =
      DEFAULT_MAXIMUM_DISTANCE_SLIP_LANE_CROSSING_TO_JUNCTION_METERS;

  /** see {@link #setCollapsedModes(Collection)} */
  private final List<Mode> collapsedModes = new ArrayList<>();

  /**
   * Constructor, every value at its default and no mode collapsed
   */
  public IntersectionConsolidatorSettings() {
  }

  /**
   * Copy constructor
   *
   * @param other to copy
   */
  public IntersectionConsolidatorSettings(IntersectionConsolidatorSettings other) {
    this.maximumInternalLinkLengthMeters = other.maximumInternalLinkLengthMeters;
    this.minimumCoverageGainPerMember = other.minimumCoverageGainPerMember;
    this.coverageTieMargin = other.coverageTieMargin;
    this.maximumParsedIntersectionsPerIntersectionCluster = other.maximumParsedIntersectionsPerIntersectionCluster;
    this.internalLinkLengthWarningMeters = other.internalLinkLengthWarningMeters;
    this.maximumDistanceReachedOnlyThroughIntersectionMeters =
        other.maximumDistanceReachedOnlyThroughIntersectionMeters;
    this.maximumDistanceSlipLaneCrossingToJunctionMeters = other.maximumDistanceSlipLaneCrossingToJunctionMeters;
    this.collapsedModes.addAll(other.collapsedModes);
  }

  /**
   * Reach between parsed intersections along the road, and the longest link used for scoring
   *
   * @return reach in metres
   */
  public double getMaximumInternalLinkLengthMeters() {
    return maximumInternalLinkLengthMeters;
  }

  /**
   * Set the reach between parsed intersections along the road, in either direction, within which they form an
   * intersection cluster. It is also the longest link used for scoring
   *
   * @param maximumInternalLinkLengthMeters reach in metres
   */
  public void setMaximumInternalLinkLengthMeters(double maximumInternalLinkLengthMeters) {
    this.maximumInternalLinkLengthMeters = maximumInternalLinkLengthMeters;
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
   * Largest number of parsed intersections in an intersection cluster that is subdivided
   *
   * @return largest number
   */
  public int getMaximumParsedIntersectionsPerIntersectionCluster() {
    return maximumParsedIntersectionsPerIntersectionCluster;
  }

  /**
   * Set the largest number of parsed intersections in an intersection cluster that is subdivided. Larger clusters are
   * kept as they are and listed in the result
   *
   * @param maximumParsedIntersectionsPerIntersectionCluster largest number
   */
  public void setMaximumParsedIntersectionsPerIntersectionCluster(
      int maximumParsedIntersectionsPerIntersectionCluster) {
    this.maximumParsedIntersectionsPerIntersectionCluster = maximumParsedIntersectionsPerIntersectionCluster;
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
   * How far back along the road a link entering an intersection may be fed only from that intersection
   *
   * @return distance in metres
   */
  public double getMaximumDistanceReachedOnlyThroughIntersectionMeters() {
    return maximumDistanceReachedOnlyThroughIntersectionMeters;
  }

  /**
   * Set how far back along the road a link entering an intersection may be fed only from that intersection, for it
   * to become internal
   *
   * @param maximumDistanceReachedOnlyThroughIntersectionMeters distance in metres
   */
  public void setMaximumDistanceReachedOnlyThroughIntersectionMeters(
      double maximumDistanceReachedOnlyThroughIntersectionMeters) {
    this.maximumDistanceReachedOnlyThroughIntersectionMeters = maximumDistanceReachedOnlyThroughIntersectionMeters;
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
   * The modes collapsed onto one node per intersection
   *
   * @return the modes, unmodifiable, empty for none
   */
  public List<Mode> getCollapsedModes() {
    return Collections.unmodifiableList(collapsedModes);
  }

  /**
   * Set the modes to collapse onto one node per intersection, replacing those set before
   *
   * @param collapsedModes the modes, empty for none
   */
  public void setCollapsedModes(Collection<Mode> collapsedModes) {
    this.collapsedModes.clear();
    this.collapsedModes.addAll(collapsedModes);
  }
}
