package org.goplanit.utils.zoning;

import org.goplanit.utils.id.ManagedIdEntityFactory;

/**
 * A Factory for transfer zone groups to register on container 
 * 
 * @author markr
 *
 */
public interface TransferZoneGroupFactory extends ManagedIdEntityFactory<TransferZoneGroup>{
    
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
