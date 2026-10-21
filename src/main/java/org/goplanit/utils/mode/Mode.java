package org.goplanit.utils.mode;

import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.id.ManagedId;

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

  /** Verify if this mode is motorised and travels on road, e.g., car or bus, but not a pedestrian, bicycle or tram
   *
   * @return true when a motorised road mode, false otherwise, including when it has no physical features
   */
  public default boolean isMotorisedRoadMode() {
    return hasPhysicalFeatures() &&
        getPhysicalFeatures().getMotorisationType() == MotorisationModeType.MOTORISED &&
        getPhysicalFeatures().getTrackType() == TrackModeType.ROAD;
  }

  /** Verify if this mode travels on rail, e.g., train, tram or light rail
   *
   * @return true when a rail mode, false otherwise, including when it has no physical features
   */
  public default boolean isRailMode() {
    return hasPhysicalFeatures() && getPhysicalFeatures().getTrackType() == TrackModeType.RAIL;
  }

  /** Verify if this mode is a motorised road mode or a rail mode, see {@link #isMotorisedRoadMode()} and
   * {@link #isRailMode()}: the traffic a traffic signal controls
   *
   * @return true when either, false otherwise, including when it has no physical features
   */
  public default boolean isMotorisedRoadOrRailMode() {
    return isMotorisedRoadMode() || isRailMode();
  }

  /** Verify if this mode is the predefined tram or light rail mode
   *
   * @return true when a tram or light rail mode, false otherwise, including for a custom mode
   */
  public default boolean isTramOrLightRailMode() {
    return isPredefinedModeType(PredefinedModeType.TRAM) || isPredefinedModeType(PredefinedModeType.LIGHTRAIL);
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
   * Verify if the mode is the given predefined mode type, e.g. the predefined bicycle mode. A custom mode is never one,
   * also not when the custom type is given
   *
   * @param predefinedModeType to check against
   * @return true when a predefined mode of the given type, false otherwise
   */
  public default boolean isPredefinedModeType(PredefinedModeType predefinedModeType){
    return isPredefinedModeType() && getPredefinedModeType() == predefinedModeType;
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
}
