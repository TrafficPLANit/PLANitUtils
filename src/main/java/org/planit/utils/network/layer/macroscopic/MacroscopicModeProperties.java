package org.planit.utils.network.layer.macroscopic;

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
  double DEFAULT_CRITICAL_SPEED_KMH = 60;
  /**
   * Epsilon margin when comparing speeds (km/h)
   */
  double DEFAULT_SPEED_EPSILON = 0.000001;

  /**
   * Collect the maximum speed in km/h
   * 
   * @return the maximum speed in km/h
   */
  double getMaximumSpeedKmH();
  
  /**
   * set the maximum speed in km/h
   * 
   * @param maxSpeedKmH to set 
   */
  void setMaximumSpeedKmH(double maxSpeedKmH);  

  /**
   * Collect the critical speed in km/h
   * 
   * @return the critical speed in km/h
   */
  double getCriticalSpeedKmH();
  
  /**
   * set the critical speed in km/h
   * 
   * @param criticalSpeedKmH to set
   */
  void setCriticalSpeedKmH(double criticalSpeedKmH);

  /**
   * Clone this instance
   * 
   * @return copy of this instance
   */
  MacroscopicModeProperties clone(); 

}
