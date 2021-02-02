package org.planit.utils.zoning;

/**
 * Interface to manage zones
 * 
 * @author markr
 *
 * @param <Z>
 */
/**
 * @author markr
 *
 * @param <Z> zone type
 */
public interface Zones<Z extends Zone> extends Iterable<Z> {
    
  /**
   * Add zone to the internal container. Use carefully to avoid issues with internal
   * contiguous ids
   *
   * @param zone the zone to be added
   * @return the zone added
   */
  Zone register(final Z zone);

  /**
   * Create and register new zone
   *
   * @return the new zone created
   */
  public Z registerNew();

  /**
   * Retrieve zone by its Id
   *
   * @param id the id of the zone
   * @return zone the zone retrieved
   */
  public Zone get(final long id);

  /**
   * Collect number of zones
   *
   * @return the number of zones
   */
  public int size();
  
  /** check if no zones are registered, i.e., size=0
   * 
   * @return true when empty, false otherwise
   */
  default boolean isEmpty() {
    return size()<= 0;
  }
  
  /** each zone has exactly one centroid, so this is functionally equivalent to calling {@link size()}
   * @return number of centroids
   */
  default public int getNumberOfCentroids() {
    return size();
  }

}
