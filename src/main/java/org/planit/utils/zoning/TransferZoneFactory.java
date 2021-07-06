package org.planit.utils.zoning;

import org.planit.utils.id.ContainerisedManagedIdEntityFactory;

/** Factory interface for directed connectoids
 * 
 * @author markr
 *
 */
public interface TransferZoneFactory extends ContainerisedManagedIdEntityFactory<TransferZone>{

  /**
   * Create and register new transfer zone
   *
   *@param centroid to use
   * @return the new zone created
   */
  public abstract TransferZone registerNew(final Centroid centroid);
  
  /**
   * Create a new transfer zone without registering
   *
   * @return the new zone created
   */
  public abstract TransferZone createNew(final Centroid centroid); 
}
