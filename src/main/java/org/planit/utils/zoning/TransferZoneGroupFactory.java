package org.planit.utils.zoning;

import org.planit.utils.id.ContainerisedManagedIdEntityFactory;

/**
 * A Factory for transfer zone groups to register on container 
 * 
 * @author markr
 *
 */
public interface TransferZoneGroupFactory extends ContainerisedManagedIdEntityFactory<TransferZoneGroup>{
    
  /**
   * create and register a new transferZoneGroup
   * 
   * @return newly created transfer zone group
   */
  public abstract TransferZoneGroup registerNew(); 
  
  /**
   * create a new transferZoneGroup without registering it yet
   * 
   * @return newly created transfer zone group
   */
  public abstract TransferZoneGroup createNew();   
    
}
