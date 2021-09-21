package org.planit.utils.zoning;

import org.planit.utils.id.ManagedIdEntityFactory;

/** Factory interface for directed connectoids
 * 
 * @author markr
 *
 */
public interface TransferZoneFactory extends ManagedIdEntityFactory<TransferZone>{

  /**
   * Create and register new transfer zone
   *
   * @return the new zone created
   */
  public abstract TransferZone registerNew();
  
  /**
   * Create a new transfer zone without registering
   *
   * @return the new zone created
   */
  public abstract TransferZone createNew(); 
}
