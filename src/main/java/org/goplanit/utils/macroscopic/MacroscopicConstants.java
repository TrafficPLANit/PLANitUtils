package org.goplanit.utils.macroscopic;

public class MacroscopicConstants {

  /**
   * Capacity per lane (pcu/h/lane) of 2000
   */
  public static final double CAPACITY_2000_PCU_HOUR_LANE = 2000.0;

  /**
   * Capacity per lane (pcu/h/lane) of 2000
   */
  public static final double CAPACITY_1800_PCU_HOUR_LANE = 1800.0;

  /**
   * Default capacity per lane (pcu/h/lane)
   */
  public static final double DEFAULT_CAPACITY_PCU_HOUR_LANE = CAPACITY_1800_PCU_HOUR_LANE;
  
  /**
   * Default maximum density per lane (pcu/km/lane)
   */
  public static final double DEFAULT_MAX_DENSITY_PCU_KM_LANE = 180.0;

  /**
   * Default maximum density per lane (pcu/km/lane)
   */  
  public static final double DEFAULT_EMPTY_DENSITY_PCU_HOUR_LANE = 0.0;
  
  /**
   * Default backward wave speed (km/h) (negative value)
   */
  public static final double DEFAULT_BACKWARD_WAVE_SPEED_KM_HOUR = -12.5;

  /**
   * 100 km/h speed
   */
  public static final double SPEED_100_KMH = 100;

  /**
   * 80 km/h speed
   */
  public static final double SPEED_80_KMH = 80;

  /**
   * 50 km/h speed
   */
  public static final double SPEED_50_KMH = 50;

  /**
   * 30 km/h speed
   */
  public static final double SPEED_30_KMH = 30;

  /**
   * Default critical speed is 80 km/h
   */
  public static final double DEFAULT_CRITICAL_SPEED = SPEED_80_KMH;
  
}
