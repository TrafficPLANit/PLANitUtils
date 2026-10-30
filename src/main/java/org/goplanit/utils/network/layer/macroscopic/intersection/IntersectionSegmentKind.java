package org.goplanit.utils.network.layer.macroscopic.intersection;

/**
 * Kind of intersection segment a link segment is in the intersection it ends in, which says where its effective
 * capacity factor comes from. An intersection that is both a junction and a crossing counts as a junction
 *
 * @author markr
 */
public enum IntersectionSegmentKind {

  /** an approach with a signal of its own at a junction: green only for its phase */
  APPROACH_WITH_SIGNAL_AT_JUNCTION(true),

  /** an approach with a signal of its own at a crossing that is no junction: green unless pedestrians cross */
  APPROACH_WITH_SIGNAL_AT_CROSSING_ONLY(true),

  /** an approach with a signal of its own at a ramp meter: green at a fixed ratio */
  APPROACH_WITH_SIGNAL_AT_RAMP_METER(true),

  /** an approach without a signal of its own, whose traffic gives way, so with a saturation flow factor only */
  APPROACH_WITHOUT_SIGNAL(true),

  /** an internal segment with a signal of a spatial intersection, with values only ever set directly */
  INTERNAL_SEGMENT_WITH_SIGNAL(true),

  /** an internal segment without a signal, whose saturation flow factor is only kept for when it gets a signal */
  INTERNAL_SEGMENT_WITHOUT_SIGNAL(false);

  /** whether the green ratio and saturation flow factor of this kind count towards the effective capacity factor */
  private final boolean holdsEffectiveCapacityFactor;

  /**
   * Constructor
   *
   * @param holdsEffectiveCapacityFactor whether the values of this kind count towards the effective capacity factor
   */
  private IntersectionSegmentKind(boolean holdsEffectiveCapacityFactor) {
    this.holdsEffectiveCapacityFactor = holdsEffectiveCapacityFactor;
  }

  /**
   * Whether the green ratio and saturation flow factor of this kind count towards the effective capacity factor of its
   * link segment, see {@link Intersection#getEffectiveCapacityFactorOf}; when not, the link segment gives 1
   *
   * @return true when they count, false otherwise
   */
  public boolean holdsEffectiveCapacityFactor() {
    return holdsEffectiveCapacityFactor;
  }
}
