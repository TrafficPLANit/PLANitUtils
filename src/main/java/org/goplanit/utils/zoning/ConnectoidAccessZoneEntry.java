package org.goplanit.utils.zoning;

import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.id.ManagedId;
import org.goplanit.utils.mode.Mode;

import java.util.Collection;
import java.util.Optional;

/**
 *each connectoid may provide access to one or more zones of some type, e.g. OD and/or transfer. Each single combination
 * is captured in this class with specific properties such as length and type
 *
 * @author markr
 *
 */
public interface ConnectoidAccessZoneEntry extends ExternalIdAble, ManagedId, Iterable<Zone> {

  /**
   * Default connectoid length in km
   */
  public static double DEFAULT_LENGTH_KM = 0.0;
  
  /** default type is set to none */
  public static ConnectoidType DEFAULT_CONNECTOID_TYPE = ConnectoidType.NONE;

  /** Set the type of the connectoid
   * 
   * @param type its type
   */
  public abstract void setType(ConnectoidType type);  
  
  /** The type of the connectoid
   * 
   * @return its type
   */
  public abstract ConnectoidType getType();  
      
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
  @Override
  public abstract ConnectoidAccessZoneEntry shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidAccessZoneEntry deepClone();


}
