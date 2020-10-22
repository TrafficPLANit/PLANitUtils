package org.planit.utils.network.physical.macroscopic;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.planit.utils.mode.Mode;

/**
 * The macroscopic link segment type characteristics are contained in this class
 * 
 * @author markr
 *
 */
public interface MacroscopicLinkSegmentType {

  /**
   * Default capacity per lane (pcu/h)
   */
  double DEFAULT_CAPACITY_LANE = 1800.0;
  
  /**
   * Default capacity per lane (pcu/h)
   */
  double DEFAULT_MAX_DENSITY_LANE = 180.0;  

  /**
   * Return the unique id of this macroscopic link segment type
   * 
   * @return the id
   */
  long getId();

  /**
   * Return the name of this macroscopic link segment type
   * 
   * @return the name
   */
  String getName();

  /**
   * Set the name of this macroscopic link segment type
   * 
   * @param name the name
   */
  void setName(String name);

  /**
   * Return the capacity per lane of this macroscopic link segment type
   * 
   * @return capacity per lane in pcu/h/lane
   */
  double getCapacityPerLane();

  /**
   * Return the maximum density per lane for this macroscopic link segment type
   * 
   * @return the maximum density per lane in pcu/km/lane
   */
  double getMaximumDensityPerLane();

  /**
   * Return the external id of this macroscopic link segment type
   * 
   * @return the external id
   */
  Object getExternalId();

  /**
   * Return whether this link segment type has an external Id
   * 
   * @return true if this link segment type has an external Id, false otherwise
   */
  boolean hasExternalId();

  /**
   * Set the map of mode properties for this link
   * 
   * @param modeProperties map of mode properties for this link
   */
  void setModeProperties(final Map<Mode, MacroscopicModeProperties> modeProperties);
  
  /**
   * Add mode properties for this link segment
   * 
   * @param mode to add properties for
   * @param modeProperties properties to set
   * @return old mode properties for this key (if any) null otherwise
   */
  MacroscopicModeProperties addModeProperties(final Mode mode, final MacroscopicModeProperties modeProperties);

  /**
   * Returns the mode properties for a specified mode along this link
   * 
   * @param mode the specified mode
   * @return the mode properties for this link and mode
   */
  MacroscopicModeProperties getModeProperties(final Mode mode);
  
  /**
   * Verify if mode is available on type
   * 
   * @return available modes
   */
  boolean isModeAvailable(final Mode mode);   
  
  /**
   * return the available modes for which mode properties have been registered
   * 
   * @return available modes
   */
  Set<Mode> getAvailableModes();

  /** Method which identifies which of the passed in modes is unavailable on the link segment
   * @param mode
   * @return collection which is a subset of the passed in modes containing only the ones that are not available
   */
  default Collection<Mode> getUnAvailableModesFrom(Collection<Mode> includedModes){
    return includedModes.stream().filter(mode -> !isModeAvailable(mode)).collect(Collectors.toSet());
  }
  
  /** Method which identifies which of the passed in modes is unavailable on the link segment
   * @param mode
   * @return collection which is a subset of the passed in modes containing only the ones that are not available
   */
  default Collection<Mode> getAvailableModesFrom(Collection<Mode> includedModes){
    return includedModes.stream().filter(mode -> isModeAvailable(mode)).collect(Collectors.toSet());
  }  

}
