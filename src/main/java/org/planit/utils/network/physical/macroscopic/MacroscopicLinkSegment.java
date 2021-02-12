package org.planit.utils.network.physical.macroscopic;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.planit.utils.mode.Mode;
import org.planit.utils.network.physical.LinkSegment;

/**
 * Macroscopic traffic modeling oriented link segment
 * 
 * @author markr
 *
 */
public interface MacroscopicLinkSegment extends LinkSegment {
  
  /**
   * Returns whether vehicles of a specified mode are allowed through this link
   * 
   * @param mode the specified mode
   * @return true if vehicles of this mode can drive along this link, false otherwise
   */
  boolean isModeAllowed(Mode mode);
  
  /**
   * Returns the modes that are allowed on the link segment
   * 
   * @return allowed modes
   */
  Set<Mode> getAllowedModes();    

  /**
   * Return the total link segment capacity in pcu/h
   * 
   * @return linkSegmentCapacity in PCU/h
   */
  double computeCapacityPcuH();

  /**
   * Compute the free flow travel time by mode, i.e. when the link's maximum speed
   * might be capped by the mode's maximum speed
   * 
   * If the input data are invalid, this method logs the problem and returns a
   * negative value.
   * 
   * @param mode mode of travel
   * @return freeFlowTravelTime for this mode
   */
  double computeFreeFlowTravelTime(Mode mode);
  
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
  double getModelledSpeedLimitKmH(Mode mode);    

  /**
   * Set the link segment type this link segment adheres to
   * 
   * @param linkSegmentType the link segment type
   */
  void setLinkSegmentType(MacroscopicLinkSegmentType linkSegmentType);

  /**
   * Collect the link segment type of the link segment
   * 
   * @return the link segment
   */
  MacroscopicLinkSegmentType getLinkSegmentType();
  
  /** Verify if link segment type is present on the link segment
   * 
   * @return true if present, false otherwise
   */
  default boolean hasLinkSegmentType() {
    return getLinkSegmentType()!=null;
  }

  /** collect the allowed modes from the passed in modes
   * @param modes to choose from
   * @return allowed modes
   */
  public default Set<Mode> getAllowedModes(Collection<Mode> modes){
    Set<Mode> allowedModes = new HashSet<Mode>();
    for(Mode mode : modes) {
      if(isModeAllowed(mode)) {
        allowedModes.add(mode);
      }
    }
    return allowedModes;
  }
  

  
}
