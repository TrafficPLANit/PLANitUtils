package org.goplanit.utils.mode;

import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.id.ManagedId;

import java.util.Arrays;

/**
 * Interface to represent a mode
 * 
 * @author markr
 *
 */
public interface Mode extends ExternalIdAble, ManagedId {
  
  /** id class for generating ids */
  public static final Class<Mode> MODE_ID_CLASS = Mode.class;     
  
  /**
   * Default max speed in km/h
   */
  public static final double GLOBAL_DEFAULT_MAXIMUM_SPEED_KMH = 80;
  
  /**
   * Default pcu in pcu
   */  
  public static final double GLOBAL_DEFAULT_PCU = 1;    
  
  /**
   * If no mode is defined the default mode is assumed to have an XML id of 1 
   */
  public static final String DEFAULT_XML_ID = "1";  

  /**
   * Passenger car unit conversion factor for this mode
   * 
   * @return pcu
   */
  public abstract double getPcu();
  
  /**
   * collect the physical features of this mode
   * 
   * @return the physical mode features
   */
  public abstract PhysicalModeFeatures getPhysicalFeatures();
  
  /**
   * collect the features of how this mode is used (public, private etc.)
   * 
   * @return the use features of this mode
   */
  public abstract UsabilityModeFeatures getUseFeatures();  
  
  /**
   * Name of this mode
   * 
   * @return the name
   */
  public abstract String getName();
  

  /** maximum speed for this mode 
   * 
   * @return maximum speed
   */
  public abstract double getMaximumSpeedKmH();  
  
  /**
   * {@inheritDoc}
   */
  @Override
  public default Class<Mode> getIdClass() {
    return MODE_ID_CLASS;
  }

  /** Verify if physical features are available
   * @return true if available, false otherwise
   */
  public default boolean hasPhysicalFeatures() {
    return getPhysicalFeatures()!=null;
  }

  /** Verify if use features are available
   * @return true if available, false otherwise
   */
  public default boolean hasUseFeatures() {
    return getUseFeatures()!=null;
  }

  /** check if the mode is one of the PLANit predefined mode types or not
   * 
   * @return true when predefined, false, when custom
   */
  public default boolean isPredefinedModeType(){
    return false;
  }
  
  /**
   * collect the predefined mode type
   * 
   * @return the type, set to CUSTOM when it is not one of the regular predefined modes
   */
  public default PredefinedModeType getPredefinedModeType() {
    return PredefinedModeType.CUSTOM;
  }

  /** Verify if mode has a name
   * @return true when present, false otherwise
   */
  public default boolean hasName() {
    return getName()!=null && !getName().isBlank();
  }

  /** verify, given separator, if the provided string exists as external id
   *
   * @param separator to use
   * @param externalId to check for
   * @return true if present, false otherwise
   */
  public default boolean containsExternalId(char separator, String externalId){
    return Arrays.stream(getSplitExternalId(separator)).anyMatch(e -> e.equals(separator));
  }
}
