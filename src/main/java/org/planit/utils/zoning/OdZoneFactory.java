package org.planit.utils.zoning;

import org.planit.utils.id.ContainerisedManagedIdEntityFactory;

/** Factory interface for directed connectoids
 * 
 * @author markr
 *
 */
public interface OdZoneFactory extends ContainerisedManagedIdEntityFactory<OdZone>{

  /**
   * Create and register new OD zone
   *
   *@param centroid to use
   * @return the new zone created
   */
  public abstract OdZone registerNew(final Centroid centroid);
  
  /**
   * Create a new OD zone without registering
   *
   * @return the new zone created
   */
  public abstract OdZone createNew(final Centroid centroid);  
}
