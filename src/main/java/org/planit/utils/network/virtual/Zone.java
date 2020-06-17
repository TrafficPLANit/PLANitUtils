package org.planit.utils.network.virtual;

/**
 * Zone represent a geographical area with a centroid which in turn represent the
 * single point of departure of all traffic in the zone
 * 
 * @author markr
 *
 */
public interface Zone {

  /**
   * Returns the unique id of this zone
   * 
   * @return id of this zone
   */
  long getId();

  /**
   * Returns the external id of this zone
   * 
   * @return the external id
   */
  Object getExternalId();

  /**
   * Returns whether this zone has its external Id set
   * 
   * @return true if the external id has been set, false otherwise
   */
  boolean hasExternalId();

  /**
   * Returns the centroid of this zone
   * 
   * @return centroid of this zone
   */
  Centroid getCentroid();

}
