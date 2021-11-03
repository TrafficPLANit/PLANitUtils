package org.goplanit.utils.zoning;

import org.goplanit.utils.id.ManagedIdEntityFactory;

/** Factory interface for directed connectoids
 * 
 * @author markr
 *
 */
public interface OdZoneFactory extends ManagedIdEntityFactory<OdZone>{

  /**
   * Create and register new OD zone
   *
   * @return the new zone created
   */
  public abstract OdZone registerNew();
  
  /**
   * Create a new OD zone without registering
   *
   * @return the new zone created
   */
  public abstract OdZone createNew();  
}
