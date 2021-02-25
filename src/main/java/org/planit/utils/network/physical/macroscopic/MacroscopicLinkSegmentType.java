package org.planit.utils.network.physical.macroscopic;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.planit.utils.id.ExternalIdable;
import org.planit.utils.mode.Mode;

/**
 * The macroscopic link segment type characteristics are contained in this class
 * 
 * @author markr
 *
 */
public interface MacroscopicLinkSegmentType extends Cloneable, ExternalIdable {

  /**
   * Default capacity per lane (pcu/h)
   */
  double DEFAULT_CAPACITY_LANE = 1800.0;
  
  /**
   * Default capacity per lane (pcu/h)
   */
  double DEFAULT_MAX_DENSITY_LANE = 180.0;  
  
  /**
   * If no macroscopic link segment type is defined the default takes on an XML id of 1 
   */
  static final String DEFAULT_XML_ID = "1";   

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
  
  /** remove the mode properties for the passed in mode (if present)
   * 
   * @param toBeRemovedMode mode to remove properties for
   * @return existing mode properties at this location tht were removed, null otherwise
   */
  MacroscopicModeProperties removeModeProperties(final Mode toBeRemovedMode);

  /** remove the mode properties for the passed in modes
   * 
   * @param toBeRemovedModes all the modes to make unavailable
   */
  default void removeModeProperties(final Set<Mode> toBeRemovedModes) {
    toBeRemovedModes.forEach( mode -> removeModeProperties(mode));
  }  

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
   * @param mode to verify
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
   * 
   * @param modes to verify
   * @return collection which is a subset of the passed in modes containing only the ones that are not available
   */
  default Set<Mode> getUnAvailableModesFrom(final Collection<Mode> modes){
    return modes.stream().filter(mode -> !isModeAvailable(mode)).collect(Collectors.toSet());
  }
  
  /** Method which identifies which of the passed in modes is available on the link segment
   * @param modes to verify
   * @return collection which is a subset of the passed in modes containing only the ones that are available
   */
  default Set<Mode> getAvailableModesFrom(final Collection<Mode> modes){
    return modes.stream().filter(mode -> isModeAvailable(mode)).collect(Collectors.toSet());
  }
  
  /** Method which identifies which of the passed in modes is available on the link segment but not in the passed in collection of modes
   * @param modes to exclude from the available modes
   * @return collection which is a subset of the available modes, namely excludes the the passed in modes
   */
  default Set<Mode> getAvailableModesNotIn(Collection<Mode> modes){
    return getAvailableModes().stream().filter(mode -> !modes.contains(mode)).collect(Collectors.toSet());
  }  
  
  /** verify if the link segment type has any modes available
   * @return true when at least one mode is available
   */
  default boolean hasAvailableModes() {
    return getAvailableModes()!=null && !getAvailableModes().isEmpty();
  }

  /**
   * Clone this instance using the copy constructor
   * 
   * @return copy of this instance
   */
  MacroscopicLinkSegmentType clone();


  


}
