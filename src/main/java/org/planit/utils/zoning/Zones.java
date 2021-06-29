package org.planit.utils.zoning;

import java.util.Collection;

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
   * @return the previous zone registered under the same id (if any)
   */
  public abstract Zone register(final Z zone);
  
  /**
   * Remove zone from the internal container. Use carefully to avoid issues with internal
   * contiguous ids
   *
   * @param zone the zone to be removed
   * @return the previous zone registered under the same id (if any)
   */  
  public abstract Zone remove(Z zone);

  /**
   * Create and register new zone
   *
   * @return the new zone created
   */
  public default Z registerNew() {
    Z zone = createNew();
    register(zone);
    return zone;
  }
  
  /**
   * Create a new zone without registering
   *
   * @return the new zone created
   */
  public abstract Z createNew();  

  /**
   * Retrieve zone by its Id
   *
   * @param id the id of the zone
   * @return zone the zone retrieved
   */
  public abstract Zone get(final Long id);

  /**
   * Collect number of zones
   *
   * @return the number of zones
   */
  public abstract  int size();
  
  /** Collect an unmodifiable collection of the known zones
   * 
   * @return the zones
   */
  public abstract Collection<Z> toCollection();  
  
  /** check if no zones are registered, i.e., size=0
   * 
   * @return true when empty, false otherwise
   */
  public default boolean isEmpty() {
    return size()<= 0;
  }
  
  /** Each zone has exactly one centroid, so this is functionally equivalent to calling {@link size()}
   * 
   * @return number of centroids
   */
  public default int getNumberOfCentroids() {
    return size();
  }
  
  /**
   * Retrieve zone by its Id
   *
   * @param id the id of the zone
   * @return zone the zone retrieved
   */
  public default Zone get(final long id) {
    return get(Long.valueOf(id));
  }

}
