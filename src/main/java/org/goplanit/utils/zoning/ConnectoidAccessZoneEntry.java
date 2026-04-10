package org.goplanit.utils.zoning;

import org.goplanit.utils.mode.Mode;

import java.util.*;
import java.util.stream.Collectors;

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


  /** Add an allowed mode.
   *
   * @param allowedMode to add
   */
  public abstract void addExplicitAllowedMode(Mode allowedMode);

  /** Verify if a mode is allowed access to the zone via this connectoid
   *
   * @param mode to verify if allowed
   * @return true when allowed, false otherwise
   */
  public abstract boolean isModeAllowed(Mode mode);

  /** Verify if any of the modes is allowed access to the zone via this connectoid
   *
   * @param modes to verify if any is allowed
   * @return true when allowed, false otherwise
   */
  public default boolean isAnyModeAllowed(Collection<Mode> modes){
    return modes.stream().anyMatch(this::isModeAllowed);
  }

  /** collect modes that are explicitly allowed for this zone (unmodifiable). Note that if no explicit allowed
   * modes are present, all modes are implicitly allowed. When there exist explicitly allowed modes, any modes
   * in the network not included in the explicitly allowed modes are regarded to not be allowed.
   *
   * @return the modes explicitly allowed for this zone, null if none
   */
  public abstract Collection<Mode> getExplicitlyAllowedModes();

  /**
   * Verify which of provided modes is allowed on the access zone connectoid combination
   *
   * @param modes to check
   * @return allowed modes subset (if any)
   */
  public default Collection<Mode> getAllowedModesFrom(Collection<Mode> modes){
    if(!hasExplicitlyAllowedModes()){
      return new TreeSet<>(modes);
    }else{
      return modes.stream().filter(this::isModeAllowed).collect(Collectors.toSet());
    }
  }

  /** Add allowed modes. We assume the zone is already registered as an access zone for this connectoid
   *
   * @param allowedModes to add
   */
  public default void addAllowedModes(Mode... allowedModes) {
    for(int index = 0 ; index < allowedModes.length; ++index) {
      addExplicitAllowedMode(allowedModes[index]);
    }
  }

  /** Add allowed modes. We assume the zone is already registered as an access zone for this connectoid
   *
   * @param allowedModes to add
   */
  public default void addAllowedModes(Collection<Mode> allowedModes) {
    allowedModes.forEach(this::addExplicitAllowedMode);
  }

  /** Verify if any modes are allowed for this zone
   *
   * @return true when at least one mode is allowed, false otherwise
   */
  public default boolean hasExplicitlyAllowedModes() {
    Collection<Mode> allowedModes = getExplicitlyAllowedModes();
    return allowedModes!=null && !allowedModes.isEmpty();
  }

  /** Verify if all modes are allowed for this zone
   *
   * @return true when we know for certain all modes are allowed, false otherwise
   */
  public default boolean isAllModesAllowed() {
    /* no explicit allowed modes set, so all modes allowed */
    return !hasExplicitlyAllowedModes();
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
