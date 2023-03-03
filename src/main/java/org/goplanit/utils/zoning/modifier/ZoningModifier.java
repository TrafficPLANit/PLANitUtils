package org.goplanit.utils.zoning.modifier;

import org.goplanit.utils.network.layers.ServiceNetworkLayers;
import org.goplanit.utils.zoning.modifier.event.ZoningModifierEventProducer;

/**
 * zoning modifier takes care of modifications that comprise multiple components across the zoning in an integrated fashion
 * 
 * @author markr
 *
 */
public interface ZoningModifier extends ZoningModifierEventProducer{

  /**
   * recreate all internal ids of all connectoids both od and transfer connectoids
   * <p>
   *   fire #RecreatedZoningEntitiesManagedIdsEvent upon completion with those connectoids
   * </p>
   */
  public abstract void recreateConnectoidIds();
  
  /**
   * recreate all internal ids fo all zones both od and transfer zones
   * <p>
   *   fires #ModifiedZoneIdsEvent after completion
   * </p>
   */  
  public abstract void recreateZoneIds();
  
  /**
   * recreate all internal ids for all zones both od and transfer zones
   * <p>
   *   fire #RecreatedZoningEntitiesManagedIdsEvent upon completion with those transfer zone groups
   * </p>
   */    
  public abstract void recreateTransferZoneGroupIds();

  /**
   * a Method that scans the transfer zones and removes any transfer zones that are not associated with any directed connectoids, i.e., are not connected to any
   * physical network layer and therefore are considered dangling. If a transfer zone is removed and this zone belongs to a transfer zone group, it is also removed from this group
   *
   * <p>
   *   fires two #RecreatedZoningEntitiesManagedIdsEvent upon completion for transfer zones
   * </p>
   *
   * @param recreateIds when true recreate all zone ids, false do not
   */
  public abstract void removeDanglingTransferZones(boolean recreateIds);

  /**
   * a Method that scans the od zones and removes any that are not associated with any  connectoids, i.e., are not connected to any
   * physical network layer and therefore are considered dangling.
   *
   * <p>
   *   fires two #RecreatedZoningEntitiesManagedIdsEvent upon completion for od zones
   * </p>
   *
   * @param recreateIds when true recreate all zone ids, false do not
   */
  public abstract void removeDanglingOdZones(boolean recreateIds);
  
  /**
   * a Method that scans the zones and removes any zones that are not associated with any connectoids, i.e., are not connected to any
   * physical network layer and therefore are considered dangling. If a transfer zone is removed and this zone belongs to a transfer zone group, it is also removed from this group
   *
   * <p>
   *   fires two #RecreatedZoningEntitiesManagedIdsEvent upon completion one for od and one for transfer zones
   * </p>
   */
  public abstract void removeDanglingZones();

  /**
   * a Method that scans the transfer zone groups and removes any groups that are empty, i.e., have no transfer zones and are therefore considered dangling.
   * <p>
   *   fire #RecreatedZoningEntitiesManagedIdsEvent upon completion with those transfer zone groups
   * </p>
   */  
  public abstract void removeDanglingTransferZoneGroups();


  /**
   * Remove all directed connectoids for which no service nodes' physical nodes on any of the given layers match its access nodes, i.e., the
   * directed connectoid has no routed services that visit it, and therefore they are not explicitly, but implicitly dangling and are removed
   * <p>
   *   fire #RecreatedZoningEntitiesManagedIdsEvent upon completion with remaining connectoids
   * </p>
   *
   * @param serviceNetworkLayers to check against
   * @param recreateManagedConnectoidIds when true recreate connectoid ids, when false do not
   */
  public abstract void removeUnusedTransferConnectoids(ServiceNetworkLayers serviceNetworkLayers, boolean recreateManagedConnectoidIds);

  /**
   * Recreate all managed id entities owned by this zoning
   * <p>
   *   fires #RecreatedZoningManagedIdsEvent after each managedIds container that is has updated
   * </p>
   */
  void recreateManagedIdEntities();

}
