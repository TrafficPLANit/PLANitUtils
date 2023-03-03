package org.goplanit.utils.zoning;

import org.goplanit.utils.id.ManagedIdEntities;

/**
 * Interface to manage zones
 * 
 * @author markr
 *
 * @param <Z> zone type
 */
public interface Zones<Z extends Zone> extends ManagedIdEntities<Z> {
     
  /** Each zone has exactly one centroid, so this is functionally equivalent to calling size()
   * 
   * @return number of centroids
   */
  public default int getNumberOfCentroids() {
    return size();
  }
  
  /** Find the first entry with matching XML id, not efficient as not indexed by XML id, so use carefully
   * 
   * @param xmlId to find
   * @return zone found, null if not present
   */
  public default Z getByXmlId(String xmlId) {
    return firstMatch(zone -> zone.getXmlId().equals(xmlId));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Zones shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Zones deepClone();


}
