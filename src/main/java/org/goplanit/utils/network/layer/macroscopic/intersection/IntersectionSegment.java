package org.goplanit.utils.network.layer.macroscopic.intersection;

import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;

/**
 * A link segment of an intersection as the intersection holds it: an approach entering it, or an internal segment
 * lying inside it. It has a signal, see {@link IntersectionSignalSegment}, or not, see
 * {@link IntersectionUnsignalisedSegment}. Either can have a saturation flow factor, saying how much of the link
 * segment's capacity can discharge; one with a signal also a green ratio. A value not set is 1.
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
  public abstract MacroscopicLinkSegment getLinkSegment();

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
   * The effective capacity factor: the share of the link segment's capacity that can pass, the saturation flow factor
   * without a signal
   *
   * @return the effective capacity factor, 1 when no value is set
   */
  public default double getEffectiveCapacityFactor() {
    return getSaturationFlowFactor();
  }
}
