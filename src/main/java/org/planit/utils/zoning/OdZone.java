package org.planit.utils.zoning;

/**
 * an OdZone is a zone where trips terminate and or commence
 * 
 * @author markr
 *
 */
public interface OdZone extends Zone {

  /** In addition to a zone id across all zones of any derived type, each Od zone also has a unique id
   * across the Od zones specifically
   * @return
   */
  public long getOdZoneId();
}
