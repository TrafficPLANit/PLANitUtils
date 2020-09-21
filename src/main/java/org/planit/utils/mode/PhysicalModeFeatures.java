package org.planit.utils.mode;

/**
 * the physical features of a mode are listed by this class. Inspired by the categorisation as offered in open street maps as per 
 * https://wiki.openstreetmap.org/wiki/Key:access#Transport_mode_restrictions
 * 
 * @author markr
 *
 */
public interface PhysicalModeFeatures {
  
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

  /** collect the vehicular type of this mode
   * @return vehicular type
   */
  VehicularModeType getVehicularType();

  /** collect the motorisation type of this mode
   * @return motorisation type
   */
  MotorisationModeType getMotorisationType();

  /** collect the track type of this mode
   * @return track type (single, double)
   */
  TrackModeType getTrackType();

}