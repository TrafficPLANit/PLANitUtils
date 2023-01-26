package org.goplanit.utils.network.layer.macroscopic;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.physical.Link;
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
   * Compute the free flow travel time by mode, i.e. when the link's maximum speed
   * might be capped by the mode's maximum speed
   * 
   * If the input data are invalid, this method logs the problem and returns a
   * negative value. In case the mode is not supported the maximum possible double value is returned as the free flow travel time
   * 
   * @param mode mode of travel
   * @return freeFlowTravelTime for this mode
   */
  public abstract double computeFreeFlowTravelTimeHour(Mode mode);

  /**
   * Collect the maximum speed limit for the mode by taking the minimum of: (i) physical speed limit, (ii) mode's maximum speed limit, (iii) link segment type's mode specific speed
   * limit.
   * 
   * It is possible the link segment type's mode specific speed limit exceeds the physical speed limit. This can happen when the user has assigned a type to the link that is less
   * restrictive than the physical speed limit sign indicates
   * 
   * It is also possible the link segment type's mode specific speed limit is more restrictive than the physical speed limit. For example when trucks are on a motorway and they
   * have a designated lower speed limit.
   * 
   * It is also possible the mode's global speed limit is more restrictive than the physical speed limit. For example when trucks are on a motorway and the physical speed limit
   * exceeds the mode specific one
   * 
   * Hence, it is important to always use this speed limit value when collecting speed limits, rather than collecting the physical, mode specific, or link type/mode specific speed
   * limits
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
  public abstract Node getUpstreamVertex();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Node getDownstreamVertex();

  /**
   * {@inheritDoc}
   */
  @Override
  public default MacroscopicLink getParentLink() {
    return (MacroscopicLink) LinkSegment.super.getParentLink();
  }
  
    
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
  public abstract MacroscopicLinkSegment clone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicLinkSegment deepClone();
}
