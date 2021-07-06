package org.planit.utils.zoning;

import org.planit.utils.id.ManagedIdEntities;

/**
 * Interface to manage zones
 * 
 * @author markr
 *
 * @param <Z> zone type
 */
public interface Zones<Z extends Zone> extends ManagedIdEntities<Z> {
     
  /** Each zone has exactly one centroid, so this is functionally equivalent to calling {@link size()}
   * 
   * @return number of centroids
   */
  public default int getNumberOfCentroids() {
    return size();
  }


}
