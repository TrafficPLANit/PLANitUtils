package org.goplanit.utils.network.layer.macroscopic;

import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.physical.LinkSegment;
import org.goplanit.utils.network.layer.physical.Node;
import org.goplanit.utils.pcu.PcuCapacitated;

/**
 * Macroscopic traffic modeling oriented link segment. 
 * 
 * @author markr
 *
 */
public interface MacroscopicLinkSegment extends LinkSegment, PcuCapacitated {

  /**
   * Compute the free flow travel time by mode, i.e. with the link's maximum speed possibly capped by the mode's
   * maximum speed
   * <p>
   * If the input data are invalid, this method logs the problem and returns a negative value. When the mode is not
   * supported the maximum possible double value is returned as the free flow travel time
   * </p>
   *
   * @param mode mode of travel
   * @return freeFlowTravelTime for this mode
   */
  public abstract double computeFreeFlowTravelTimeHour(Mode mode);

  /**
   * Collect the maximum speed limit for the mode by taking the minimum of: (i) physical speed limit, (ii) mode's
   * maximum speed limit, (iii) link segment type's mode specific speed limit.
   * <p>
   * The link segment type's mode specific speed limit can exceed the physical speed limit, when the user has assigned
   * the link a type that is less restrictive than the physical speed limit sign. It can also be more restrictive, for
   * example when trucks have a designated lower speed limit on a motorway. The mode's global speed limit can also be
   * more restrictive than the physical one, for example for trucks on a motorway whose physical speed limit exceeds
   * the mode specific one.
   * </p>
   * <p>
   * So always use this speed limit when collecting speed limits, not the physical, mode specific, or link type/mode
   * specific speed limits
   * </p>
   *
   * @param mode to collect modelled maximum speed for
   * @return modelled speed limit, when mode is not allowed on link, 0 is returned
   */  
  public default double getModelledSpeedLimitKmH(Mode mode) {  
    if (!isModeAllowed(mode)) {
      return 0.0;
    }
    return Math.min(getPhysicalSpeedLimitKmH(), getLinkSegmentType().getMaximumSpeedKmH(mode));
  }

  /**
   * Set the link segment type this link segment adheres to
   * 
   * @param linkSegmentType the link segment type
   */
  public abstract void setLinkSegmentType(MacroscopicLinkSegmentType linkSegmentType);

  /**
   * Collect the link segment type of the link segment
   * 
   * @return the link segment
   */
  public abstract MacroscopicLinkSegmentType getLinkSegmentType();
  
  /** Verify if link segment type is present on the link segment
   * 
   * @return true if present, false otherwise
   */
  public default boolean hasLinkSegmentType() {
    return getLinkSegmentType()!=null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicLink getParent();

    
  /**
   * {@inheritDoc}
   */
  @Override
  public default double getCapacityOrDefaultPcuH() {
    return getCapacityOrDefaultPcuHLane() * getNumberOfLanes();
  }
  
  /**
   * {@inheritDoc}
   */  
  @Override
  public default double getCapacityOrDefaultPcuHLane() {
    return getLinkSegmentType().getExplicitCapacityPerLaneOrDefault();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicLinkSegment shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicLinkSegment deepClone();

}
