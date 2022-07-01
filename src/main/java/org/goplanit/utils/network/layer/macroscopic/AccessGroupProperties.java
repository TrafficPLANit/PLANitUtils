package org.goplanit.utils.network.layer.macroscopic;

import java.util.Set;

import org.goplanit.utils.mode.Mode;

/**
 * Contains the properties of a mode group specific to a link segment type
 * 
 * @author markr
 *
 */
public interface AccessGroupProperties extends Cloneable {
  
  /**
   * Epsilon margin when comparing speeds (km/h)
   */
  public static double DEFAULT_SPEED_EPSILON = 0.000001;

  /**
   * Collect the maximum speed in km/h
   * 
   * @return the maximum speed in km/h
   */
  public abstract Double getMaximumSpeedKmH();
  
  /**
   * Verify if a maximum speed is set on this group
   * 
   * @return true when set, false otherwise
   */
  public default boolean isMaximumSpeedKmHSet() {
    return getMaximumSpeedKmH()!=null;
  }
  
  /** Collect the maximum speed set, or otherwise the provided default
   * 
   * @param defaultMaximumSpeed to use
   * @return maximum speed
   */
  public default double getMaximumSpeedOrDefaultKmH(double defaultMaximumSpeed) {
    return isMaximumSpeedKmHSet() ? getMaximumSpeedKmH() : defaultMaximumSpeed;
  }
  
  /**
   * set the maximum speed in km/h
   * 
   * @param maxSpeedKmH to set 
   */
  public abstract void setMaximumSpeedKmH(final Double maxSpeedKmH);  

  /**
   * Collect the critical speed in km/h
   * 
   * @return the critical speed in km/h
   */
  public abstract Double getCriticalSpeedKmH();
  
  /**
   * Verify if a critical speed is set on this group
   * 
   * @return true when set, false otherwise
   */
  public default boolean isCriticalSpeedKmHSet() {
    return getCriticalSpeedKmH()!=null;
  }  
  
  /** Collect the critical speed set, or otherwise the provided default
   * 
   * @param defaultCriticalSpeed to use
   * @return critical speed
   */
  public default double getCriticalSpeedOrDefaultKmH(double defaultCriticalSpeed) {
    return isCriticalSpeedKmHSet() ? getCriticalSpeedKmH() : defaultCriticalSpeed;
  }  
  
  /**
   * set the critical speed in km/h
   * 
   * @param criticalSpeedKmH to set
   */
  public abstract void setCriticalSpeedKmH(final Double criticalSpeedKmH);
  
  /** The access modes for this group
   * 
   * @return access modes
   */
  public abstract Set<Mode> getAccessModes();
  
  /**
   * Clone this instance
   * 
   * @return copy of this instance
   */
  public abstract AccessGroupProperties clone();

  /** Remove access mode 
   * 
   * @param toBeRemovedMode the to be removed mode
   * @return true when success, false otherwise
   */
  public abstract boolean removeAccessMode(final Mode toBeRemovedMode);

  /** Verify if equal to given properties except for the allowed modes
   * 
   * @param accessProperties to compare against
   * @return true when deemed equal, false otherwise
   */
  public abstract boolean isEqualExceptForModes(final AccessGroupProperties accessProperties);

  /** Add a new access mode to the group
   * 
   * @param mode to add
   */
  public abstract void addAccessMode(final Mode mode);

  /**
   * Verify if group supports one or more access modes
   *
   * @return true when one or more modes are supported, false otherwise
   */
  public default boolean hasAccessModes(){
    return !getAccessModes().isEmpty();
  }
}
