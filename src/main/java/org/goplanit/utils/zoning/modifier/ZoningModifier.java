package org.goplanit.utils.zoning.modifier;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.modifier.LoggableModifier;
import org.goplanit.utils.network.layers.ServiceNetworkLayers;
import org.goplanit.utils.zoning.Zone;
import org.goplanit.utils.zoning.connectoid.Connectoid;
import org.goplanit.utils.zoning.connectoid.ConnectoidAccessZoneEntry;
import org.goplanit.utils.zoning.connectoid.DirectedConnectoidAccessZoneEntry;
import org.goplanit.utils.zoning.connectoid.ZoneConnectoidType;
import org.goplanit.utils.zoning.modifier.event.ZoningModifierEventProducer;

/**
 * zoning modifier takes care of modifications that comprise multiple components across the zoning in an integrated fashion
 * 
 * @author markr
 *
 */
public interface ZoningModifier extends ZoningModifierEventProducer, LoggableModifier {

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
   * a Method that scans the zones and removes any zones that are not associated with any connectoids, i.e.,
   * are not connected to any physical network layer and therefore are considered dangling. If a transfer zone is
   * removed and this zone belongs to a transfer zone group, it is also removed from this group
   *
   * <p>
   *   fires two #RecreatedZoningEntitiesManagedIdsEvent upon completion one for od and one for transfer zones if
   *   ids are recreated
   * </p>
   *
   * @param recreateManagedIds when true recreate ids, otherwise not
   */
  public abstract void removeDanglingZones(boolean recreateManagedIds);

  /**
   * a Method that scans the transfer zone groups and removes any groups that are empty, i.e., have no transfer zones
   * and are therefore considered dangling.
   * <p>
   *   fire #RecreatedZoningEntitiesManagedIdsEvent upon completion with those transfer zone groups if ids are
   *   recreated
   * </p>
   * @param recreateManagedIds when true recreate ids, otherwise not
   */  
  public abstract void removeDanglingTransferZoneGroups(boolean recreateManagedIds);


  /**
   * Remove the entries of the stops served at all directed connectoids for which no service nodes' physical nodes on
   * any of the given layers match its access nodes, i.e., no routed services visit it, so these stops are implicitly
   * dangling there. Entries of other zones, giving travellers access to or egress from them, are kept, and a
   * connectoid is only removed once no entry remains
   * <p>
   *   fire #RecreatedZoningEntitiesManagedIdsEvent upon completion with remaining connectoids
   * </p>
   *
   * @param serviceNetworkLayers to check against
   * @param recreateManagedConnectoidIds when true recreate connectoid ids, when false do not
   */
  public abstract void removeUnusedTransferConnectoids(
          ServiceNetworkLayers serviceNetworkLayers, boolean recreateManagedConnectoidIds);

  /**
   * Remove a connectoid from the zoning, firing a remove connectoid event. Its access zone entries stay on it, so the
   * event shows what it served
   *
   * @param connectoid to remove
   * @return true when removed, false when the zoning does not hold it
   */
  public abstract boolean removeConnectoid(Connectoid connectoid);

  /**
   * Remove an access zone entry from a connectoid, firing a remove access zone entry event. The connectoid stays, even
   * when left without entries
   *
   * @param connectoid to remove the entry from
   * @param accessZone of the entry
   * @param type of the entry
   * @return the entry removed, null when the connectoid has no such entry
   */
  public abstract ConnectoidAccessZoneEntry removeAccessZoneEntry(
      Connectoid connectoid, Zone accessZone, ZoneConnectoidType type);

  /**
   * Remove an access link segment from an access zone entry of a connectoid, firing a remove access link segment event.
   * The entry stays, even when left without access link segments
   *
   * @param connectoid the entry belongs to
   * @param entry to remove the access link segment from
   * @param accessLinkSegment to remove
   * @return true when removed, false when the entry does not hold it
   */
  public abstract boolean removeAccessLinkSegment(
      Connectoid connectoid, DirectedConnectoidAccessZoneEntry entry, EdgeSegment accessLinkSegment);

  /**
   * Remove an explicitly allowed mode from an access zone entry of a connectoid, firing a remove allowed mode event.
   * As {@link ConnectoidAccessZoneEntry#removeExplicitAllowedMode(Mode)}, the last explicitly allowed mode is not
   * removed, since an entry without any would allow every mode
   *
   * @param connectoid the entry belongs to
   * @param entry to remove the mode from
   * @param mode to remove
   * @return true when removed, false when the entry does not allow it explicitly or it is its last
   */
  public abstract boolean removeExplicitAllowedMode(Connectoid connectoid, ConnectoidAccessZoneEntry entry, Mode mode);

  /**
   * Recreate all managed id entities owned by this zoning. Virtual network ids are not part of this as they
   * are shared with the physical network
   * <p>
   *   fires #RecreatedZoningManagedIdsEvent after each managedIds container that is has updated
   * </p>
   * todo: add functionality to also deal with virtual network recreation in conjunction with physical network somehow
   */
  void recreateManagedIdEntities();

}
