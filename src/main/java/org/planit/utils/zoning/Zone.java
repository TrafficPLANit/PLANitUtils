package org.planit.utils.zoning;

import org.locationtech.jts.geom.Polygon;
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
  
  /** set the geometry (outer border) of this zone
   * 
   * @param geometry of the zone
   */
  void setGeometry(Polygon geometry);  
  
  /** Collect the geometry (outer border) of this zone
   * 
   * @return geometry of the zone
   */
  Polygon getGeometry();

}
