package org.goplanit.utils.zoning;

import org.goplanit.utils.id.ManagedIdEntityFactory;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.physical.LinkSegment;
import org.goplanit.utils.network.layer.physical.Node;

import java.util.Collection;

/** Factory interface for directed connectoids
 * 
 * @author markr
 *
 */
public interface DirectedConnectoidFactory extends ManagedIdEntityFactory<DirectedConnectoid>{

  /** Create a new directed connectoid
   *
   * @param accessZone to use
   * @param downstreamAccessNode when true access node is chosen as the downstream node of the segment,
   *                             when false, upstream node is chosen
   * @param accessLinkSegment to use
   * @param length to use for distance between zone and connectoid
   * @param type the type of the zone connectoid combination reflecting how it is envisaged to be used
   * @return created directed connectoid
   */
  public abstract DirectedConnectoid registerNew(
      Zone accessZone,
      final boolean downstreamAccessNode,
      LinkSegment accessLinkSegment,
      double length,
      ZoneConnectoidType type);

  /** Create a new directed connectoid, without zone attached and using default length
   *
   * @param accessZone to use
   * @param downstreamAccessNode when true access node is chosen as the downstream node of the segment,
   *                             when false, upstream node is chosen
   * @param accessLinkSegment to use
   * @param type the type of the zone connectoid combination reflecting how it is envisaged to be used
   * @return created directed connectoid
   */
  public default DirectedConnectoid registerNew(
      Zone accessZone, boolean downstreamAccessNode, LinkSegment accessLinkSegment, ZoneConnectoidType type){
    return registerNew(
        accessZone,
        downstreamAccessNode,
        accessLinkSegment,
        ConnectoidAccessZoneEntry.DEFAULT_LENGTH_KM.get(),
        type);
  }

  /** Create a new directed connectoid, with default length 0, and choose downstream access node based on the link
   * segment provided
   *
   * @param accessLinkSegment to use
   * @param accessZone to use for the zone connectoid combination
   * @param syncXmlIdToId flag indicating if we should sync the XML ids to internal ids
   * @param allowedModes to use for the zone connectoid combination
   * @param type the type of the zone connectoid combination reflecting how it is envisaged to be used
   * @return created directed connectoid
   */
  public default DirectedConnectoid registerNewDownstreamAccess(
      Zone accessZone,
      LinkSegment accessLinkSegment,
      boolean syncXmlIdToId,
      Collection<Mode> allowedModes,
      ZoneConnectoidType type){
    return registerNew(accessZone, true, accessLinkSegment, syncXmlIdToId, allowedModes,type);
  }

  /** factory method for directed connectoid, with yet unknown access zones but with access node
   *
   * @param accessNode to use
   * @return created undirected connectoid
   */
  public DirectedConnectoid registerNew(Node accessNode);

  /** Create a new directed connectoid, with default length 0
   *
   * @param downstreamAccessNode when true access node is chosen as the downstream node of the segment, when false,
   *                             upstream node is chosen
   * @param accessLinkSegment to use
   * @param accessZone to use
   * @param syncXmlIdToId flag indicating if we should sync the XML ids to internal ids
   * @param allowedModes to apply
   * @param type the type of the zone connectoid combination reflecting how it is envisaged to be used
   * @return created directed connectoid
   */
  public default DirectedConnectoid registerNew(
      Zone accessZone,
      final boolean downstreamAccessNode,
      LinkSegment accessLinkSegment,
      boolean syncXmlIdToId,
      Collection<Mode> allowedModes,
      ZoneConnectoidType type){
    DirectedConnectoid newEntity = registerNew(accessZone, downstreamAccessNode, accessLinkSegment, type);
    if(syncXmlIdToId == true) {
      newEntity.setXmlId(newEntity.getId());
    }
    newEntity.addAllowedModes(accessZone, allowedModes);
    return newEntity;
  }

}
