package org.goplanit.utils.network.layer.macroscopic.intersection;

/**
 * A link segment of an intersection with a signal controlling its traffic: an approach with a signal of its own, or an
 * internal segment with a signal standing on it, such as a stop line in a median. Next to its saturation flow factor it
 * has a green ratio
 *
 * @author markr
 */
public interface IntersectionSignalSegment extends IntersectionSegment {

  /**
   * A signal segment always has a signal
   *
   * @return true
   */
  @Override
  public default boolean hasSignal() {
    return true;
  }

  /**
   * The green ratio: the share of the signal cycle in which traffic on the link segment has green, from 0 to 1. For
   * example 0.44 when its traffic has green for 40 s of a 90 s cycle
   *
   * @return the green ratio, 1 when not set
   */
  public abstract double getGreenRatio();

  /**
   * How the green ratio got its value
   *
   * @return the origin, null when no green ratio is set
   */
  public abstract CapacityFactorOrigin getGreenRatioOrigin();

  /**
   * Set the green ratio, see {@link #getGreenRatio()}
   *
   * @param greenRatio from 0 to 1
   * @param origin how the value was found
   * @return true when set, false when the value is outside 0 to 1, leaving it unchanged
   */
  public abstract boolean setGreenRatio(double greenRatio, CapacityFactorOrigin origin);

  /**
   * Remove the green ratio, so it is 1 again
   *
   * @return true when removed, false when none was set
   */
  public abstract boolean removeGreenRatio();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract IntersectionSignalSegment shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract IntersectionSignalSegment deepClone();

  /**
   * The effective capacity factor: the green ratio times the saturation flow factor, the share of the link segment's
   * capacity that can pass. For example 0.418 for a green ratio of 0.44 and a saturation flow factor of 0.95
   *
   * @return the effective capacity factor, 1 when neither value is set
   */
  @Override
  public default double getEffectiveCapacityFactor() {
    return getGreenRatio() * getSaturationFlowFactor();
  }
}
