package org.planit.utils.network.physical.macroscopic;

/**
 * Contains the properties of a mode specific to a link segment type
 * 
 * @author markr
 *
 */
public interface MacroscopicModeProperties extends Cloneable {

  /**
   * Default critical speed, i.e. speed at capacity in km/h
   */
  double DEFAULT_CRITICAL_SPEED = 60;
  /**
   * Epsilon margin when comparing speeds (km/h)
   */
  double DEFAULT_SPEED_EPSILON = 0.000001;

  /**
   * Collect the maximum speed in km/h
   * 
   * @return the maximum speed in km/h
   */
  double getMaximumSpeed();
  
  /**
   * set the maximum speed in km/h
   * 
   * @param maxSpeed
   */
  double setMaximumSpeed(double maxSpeed);  

  /**
   * Collect the critical speed in km/h
   * 
   * @return the critical speed in km/h
   */
  double getCriticalSpeed();
  
  /**
   * set the critical speed in km/h
   * 
   * @param criticalSpeed
   */
  double setCriticalSpeed(double criticalSpeed);

  /**
   * Clone this instance
   * 
   * @return copy of this instance
   */
  MacroscopicModeProperties clone(); 

}
