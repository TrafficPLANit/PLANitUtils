package org.planit.utils.mode;

import org.planit.utils.id.ExternalIdable;

/**
 * Interface to represent a mode
 * 
 * @author markr
 *
 */
public interface Mode extends Comparable<Mode>, ExternalIdable {
  
  /**
   * Default max speed in km/h
   */
  double GLOBAL_DEFAULT_MAXIMUM_SPEED_KMH = 80;
  
  /**
   * Default pcu in pcu
   */  
  double GLOBAL_DEFAULT_PCU = 1;    
  
  /**
   * If no mode is defined the default mode is assumed to have an external idf of 1 
   */
  public static final long DEFAULT_EXTERNAL_ID = 1;  

  /**
   * Passenger car unit conversion factor for this mode
   * 
   * @return pcu
   */
  double getPcu();
  
  /**
   * collect the physical features of this mode
   * 
   * @return the physical mode features
   */
  PhysicalModeFeatures getPhysicalFeatures();
  
  /** Verify if physical features are available
   * @return true if available, false otherwise
   */
  default boolean hasPhysicalFeatures() {
    return getPhysicalFeatures()!=null;
  }  
  
  /**
   * collect the features of how this mode is used (public, private etc.)
   * 
   * @return the use features of this mode
   */
  UsabilityModeFeatures getUseFeatures();  
  
  /** Verify if use features are available
   * @return true if available, false otherwise
   */
  default boolean hasUseFeatures() {
    return getUseFeatures()!=null;
  }    

  /**
   * Name of this mode
   * 
   * @return the name
   */
  String getName();
  

  /** maximum speed for this mode 
   * 
   * @return maximum speed
   */
  double getMaximumSpeedKmH();  
  
  /** check if the mode is one of the PLANit predefined mode types or not
   * 
   * @return true when predefined, false, when custom
   */
  default boolean isPredefinedModeType(){
    return false;
  }
  
  /**
   * collect the predefined mode type
   * 
   * @return the type, set to CUSTOM when it is not one of the regular predefined modes
   */
  default PredefinedModeType getPredefinedModeType() {
    return PredefinedModeType.CUSTOM;
  }



}
