package org.planit.utils.zoning.modifier;

import org.planit.utils.zoning.modifier.event.ZoningModifierEventProducer;

/**
 * zoning modifier takes care of modifications that comprise multiple components across the zoning in an integrated fashion
 * 
 * @author markr
 *
 */
public interface ZoningModifier extends ZoningModifierEventProducer{

  /**
   * recreate all internal ids fo all connectoids both od and transfer connectoids
   */
  public abstract void recreateConnectoidIds();
  
  /**
   * recreate all internal ids fo all zones both od and transfer zones
   */  
  public abstract void recreateZoneIds();
  
  /**
   * recreate all internal ids fo all zones both od and transfer zones
   */    
  public abstract void recreateTransferZoneGroupIds();  
  
  /**
   * a Method that scans the zones and removes any zones that are not associated with any connectoids, i.e., are not connected to any
   * physical network layer and therefore are considered dangling. If a transfer zone is removed and this zone belongs to a transfer zone group, it is also removed from this group
   */
  public abstract void removeDanglingZones();

  /**
   * a Method that scans the transfer zone groups and removes any groups that are empty, i.e., have no transfer zones and are therefore considered dangling.
   */  
  public abstract void removeDanglingTransferZoneGroups();


}
