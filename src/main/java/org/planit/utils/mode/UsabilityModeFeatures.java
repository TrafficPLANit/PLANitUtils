package org.planit.utils.mode;

/**
 * Class to highlight the use of a particular mode, i.e., is it a public or private mode, ride-share, etc. Inspired by the categorisation as offered in open street maps as per 
 * https://wiki.openstreetmap.org/wiki/Key:access#Transport_mode_restrictions
 * 
 * @author markr
 *
 */
public class UsabilityModeFeatures {

  /**
   * default use of mode is private use
   */
  public static UseOfModeType DEFAULT_USEOF_TYPE = UseOfModeType.PRIVATE;
  
  /** the use of the type */
  protected UseOfModeType useOfType;   
  
   
  /**
   * Default constructor
   */
  protected UsabilityModeFeatures() {
    this.useOfType = DEFAULT_USEOF_TYPE;
  }
  
  /* getters - setters */
  
  public UseOfModeType getUseOfType() {
    return useOfType;
  }

  public void setUseOfType(UseOfModeType useOfType) {
    this.useOfType = useOfType;
  }  
  
}
