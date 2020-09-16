package org.planit.utils.mode;

/**
 * the physical features of a mode are listed by this class. Inspired by the categorisation as offered in open street maps as per 
 * https://wiki.openstreetmap.org/wiki/Key:access#Transport_mode_restrictions
 * 
 * @author markr
 *
 */
public class PhysicalModeFeatures {
  
  /**
   * default is of a vehicular type
   */
  public static VehicularModeType DEFAULT_VEHICULAR_TYPE = VehicularModeType.VEHICLE;
  
  /**
   * default is motorised
   */
  public static MotorisationModeType DEFAULT_MOTORISATION_TYPE = MotorisationModeType.MOTORISED;
  
  /**
   * default is double-tracked, e.g. creates two tracks when driving
   */
  public static TrackModeType DEFAULT_TRACK_TYPE = TrackModeType.DOUBLE;  
  
  /** the vehicular type */
  protected VehicularModeType vehicularType; 
  
  /** the motorisation type */
  protected MotorisationModeType motorisationType;
  
  /** the track type */
  protected TrackModeType trackType;  
  
  /**
   * Default constructor
   */
  public PhysicalModeFeatures() {
    this.vehicularType = DEFAULT_VEHICULAR_TYPE;
    this.motorisationType = DEFAULT_MOTORISATION_TYPE;
    this.trackType = DEFAULT_TRACK_TYPE;
  }
  
  /* getters - setters */
  
  public VehicularModeType getVehicularType() {
    return vehicularType;
  }

  public void setVehicularType(VehicularModeType vehicularType) {
    this.vehicularType = vehicularType;
  }

  public MotorisationModeType getMotorisationType() {
    return motorisationType;
  }

  public void setMotorisationType(MotorisationModeType motorisationType) {
    this.motorisationType = motorisationType;
  }

  public TrackModeType getTrackType() {
    return trackType;
  }

  public void setTrackType(TrackModeType trackType) {
    this.trackType = trackType;
  }  

}
