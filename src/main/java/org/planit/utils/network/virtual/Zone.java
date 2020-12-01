package org.planit.utils.network.virtual;

import org.planit.utils.id.ExternalIdable;

/**
 * Zone represent a geographical area with a centroid which in turn represent the
 * single point of departure of all traffic in the zone
 * 
 * @author markr
 *
 */
public interface Zone extends ExternalIdable {

  /**
   * Returns the centroid of this zone
   * 
   * @return centroid of this zone
   */
  Centroid getCentroid();

}
