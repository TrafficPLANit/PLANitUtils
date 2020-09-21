package org.planit.utils.mode;

/**
 * Interface to represent a mode
 * 
 * @author markr
 *
 */
public interface Mode extends Comparable<Mode> {
  
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
  
  /**
   * collect the features of how this mode is used (public, private etc.)
   * 
   * @return the use features of this mode
   */
  UsabilityModeFeatures getUseFeatures();  

  /**
   * Name of this mode
   * 
   * @return the name
   */
  String getName();

  /**
   * Return id of this instance. This id is expected to be generated using the
   * org.planit.utils.misc.IdGenerator
   * 
   * @return unique id
   */
  long getId();

  /**
   * Return the external id of this mode
   * 
   * @return externalId of this mode
   */
  Object getExternalId();

  /**
   * Returns whether this mode has an external Id set
   * 
   * @return true if external Id set, false otherwise
   */
  boolean hasExternalId();
  
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
