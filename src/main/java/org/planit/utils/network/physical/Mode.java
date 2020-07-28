package org.planit.utils.network.physical;

/**
 * Interface to represent a mode
 * 
 * @author markr
 *
 */
public interface Mode extends Comparable<Mode> {
  
  /**
   * If no mode is defined the default mode is assumed to have an external idf of 1 
   */
  public static final long DEFAULT_EXTERNAL_ID = 1;  

  /**
   * Passenger car unit conversion factor for this mode
   * 
   * @return pcu
   */
  double getPcu();

  /**
   * Name of this mode
   * 
   * @return the name
   */
  String getName();

  /**
   * Return id of this instance. This id is expected to be generated using the
   * org.planit.utils.misc.IdGenerator
   * 
   * @return unique id
   */
  long getId();

  /**
   * Return the external id of this mode
   * 
   * @return externalId of this mode
   */
  Object getExternalId();

  /**
   * Returns whether this mode has an external Id set
   * 
   * @return true if external Id set, false otherwise
   */
  boolean hasExternalId();

}
