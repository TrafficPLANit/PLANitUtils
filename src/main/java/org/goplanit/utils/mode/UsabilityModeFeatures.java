package org.goplanit.utils.mode;

/**
 * Class to highlight the use of a particular mode, i.e., is it a public or private mode, ride-share, etc. Inspired by the categorisation as offered in open street maps as per 
 * https://wiki.openstreetmap.org/wiki/Key:access#Transport_mode_restrictions
 * 
 * @author markr
 *
 */
public interface UsabilityModeFeatures {

  /**
   * default use of mode is private use
   */
  public static UseOfModeType DEFAULT_USEOF_TYPE = UseOfModeType.PRIVATE;
  
  
  /**
   * collect how this mode is used via this type, e.g. public, private, etc.
   * @return use of this type
   */
  UseOfModeType getUseOfType();

}