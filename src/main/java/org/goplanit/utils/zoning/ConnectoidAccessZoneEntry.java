package org.goplanit.utils.zoning;

import java.util.Optional;

/**
 *each connectoid may provide access to one or more zones of some type, e.g. OD and/or transfer. Each single combination
 * is captured in this class with specific properties such as length and type
 *
 * @author markr
 *
 */
public interface ConnectoidAccessZoneEntry {

  /**
   * Default connectoid length in km
   */
  public static Optional<Double> DEFAULT_LENGTH_KM = Optional.of(0.0);
  
  /** default type is set to none */
  public static ZoneConnectoidType DEFAULT_CONNECTOID_TYPE = ZoneConnectoidType.NONE;

  /** Set the type of the connectoid
   * 
   * @param type its type
   */
  public abstract void setType(ZoneConnectoidType type);
  
  /** The type of the connectoid
   * 
   * @return its type
   */
  public abstract ZoneConnectoidType getType();
      
  /**
   * The zone accessed by this entry
   * 
   * @return accessible zones
   */
  public abstract Zone getAccessZone();

  /**
   * set or override existing access zone
   *
   * @param accessZone to set
   */
  public void setAccessZone(Zone accessZone);
  
  /** Provide length
   * 
   * @param lengthKm to traverse between connectoid and zone
   */
  public abstract void setLengthKm(double lengthKm);

  /** length can be used to virtually assign a length to the connectoid/zone combination
   * 
   * @return length in km(null if zone is not registered)
   */
  public abstract Optional<Double> getLengthKm();

  /** Verify if a length has been specified
   *
   * @return true if present, false otherwise
   */
  public default boolean hasLength() {
    try {
      return getLengthKm().isEmpty();
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * {@inheritDoc}
   */
  public abstract ConnectoidAccessZoneEntry shallowClone();

  /**
   * {@inheritDoc}
   */
  public abstract ConnectoidAccessZoneEntry deepClone();


}
