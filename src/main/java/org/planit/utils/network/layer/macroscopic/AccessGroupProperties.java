package org.planit.utils.network.layer.macroscopic;

import java.util.Set;

import org.planit.utils.mode.Mode;

/**
 * Contains the properties of a mode specific to a link segment type
 * 
 * @author markr
 *
 */
public interface AccessGroupProperties extends Cloneable {

  /**
   * Default critical speed, i.e. speed at capacity in km/h
   */
  public static double DEFAULT_CRITICAL_SPEED_KMH = 60;
  
  /**
   * Epsilon margin when comparing speeds (km/h)
   */
  public static double DEFAULT_SPEED_EPSILON = 0.000001;

  /**
   * Collect the maximum speed in km/h
   * 
   * @return the maximum speed in km/h
   */
  public abstract double getMaximumSpeedKmH();
  
  /**
   * set the maximum speed in km/h
   * 
   * @param maxSpeedKmH to set 
   */
  public abstract void setMaximumSpeedKmH(double maxSpeedKmH);  

  /**
   * Collect the critical speed in km/h
   * 
   * @return the critical speed in km/h
   */
  public abstract double getCriticalSpeedKmH();
  
  /**
   * set the critical speed in km/h
   * 
   * @param criticalSpeedKmH to set
   */
  public abstract void setCriticalSpeedKmH(double criticalSpeedKmH);
  
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
   * @param toBeRemovedMode
   * @return true when success, false otherwise
   */
  public abstract boolean removeAccessMode(Mode toBeRemovedMode); 

}
