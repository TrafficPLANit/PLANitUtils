package org.goplanit.utils.network.layer.macroscopic.intersection;

import org.goplanit.utils.network.layer.physical.LinkSegment;

/**
 * A link segment of an intersection as the intersection holds it, an object of its own: an approach object for a link
 * segment entering the intersection, or an internal segment object for one lying inside it. Which of the two it is
 * follows from the list the intersection holds it in, see {@link Intersection#getApproaches()} and
 * {@link SpatialIntersection#getIntersectionInternalSegments()}; there is no type of its own for either. Each object is
 * an {@link IntersectionSignalSegment} when a signal controls its traffic, and an
 * {@link IntersectionUnsignalisedSegment} when not. Either can have a saturation flow factor, saying how much of the
 * link segment's capacity can discharge; one with a signal also a green ratio. A value not set is 1.
 * <p>
 * It belongs to its intersection and has no id of its own; the intersection finds it by its link segment. When its
 * signal changes, the intersection replaces it by a new instance of the other kind, so look it up again by its link
 * segment rather than holding on to the instance.
 * </p>
 *
 * @author markr
 */
public interface IntersectionSegment {

  /**
   * The link segment this is about
   *
   * @return the link segment
   */
  public abstract LinkSegment getLinkSegment();

  /**
   * Verify if a signal controls traffic on this link segment at the intersection
   *
   * @return true for an {@link IntersectionSignalSegment}, false for an {@link IntersectionUnsignalisedSegment}
   */
  public abstract boolean hasSignal();

  /**
   * The saturation flow factor: the share of the link segment's capacity that discharges, while it has green when it
   * has a signal, from 0 to 1. For example 0.5 for a minor road joining a signalised junction without a signal of its
   * own
   *
   * @return the saturation flow factor, 1 when not set
   */
  public abstract double getSaturationFlowFactor();

  /**
   * How the saturation flow factor got its value
   *
   * @return the origin, null when no saturation flow factor is set
   */
  public abstract CapacityFactorOrigin getSaturationFlowFactorOrigin();

  /**
   * Set the saturation flow factor, see {@link #getSaturationFlowFactor()}
   *
   * @param saturationFlowFactor from 0 to 1
   * @param origin how the value was found
   * @return true when set, false when the value is outside 0 to 1, leaving it unchanged
   */
  public abstract boolean setSaturationFlowFactor(double saturationFlowFactor, CapacityFactorOrigin origin);

  /**
   * Remove the saturation flow factor, so it is 1 again
   *
   * @return true when removed, false when none was set
   */
  public abstract boolean removeSaturationFlowFactor();

  /**
   * Shallow clone this instance: a new object of the same kind, on the same link segment and with the same values
   *
   * @return shallow copy of this instance
   */
  public abstract IntersectionSegment shallowClone();

  /**
   * Deep clone this instance: a new object of the same kind, with the same values and on the same link segment, which
   * it shares since it does not own it
   *
   * @return deep copy of this instance
   */
  public abstract IntersectionSegment deepClone();

  /**
   * The effective capacity factor: the share of the link segment's capacity that can pass, the saturation flow factor
   * without a signal
   *
   * @return the effective capacity factor, 1 when no value is set
   */
  public default double getEffectiveCapacityFactor() {
    return getSaturationFlowFactor();
  }
}
