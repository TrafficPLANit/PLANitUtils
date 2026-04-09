package org.goplanit.utils.zoning;

import org.goplanit.utils.id.ManagedIdEntityFactory;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.physical.LinkSegment;

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
   * @return created directed connectoid
   */
  public abstract DirectedConnectoid registerNew(
      Zone accessZone, final boolean downstreamAccessNode, LinkSegment accessLinkSegment, double length);

  /** Create a new directed connectoid, without zone attached and using default length
   *
   * @param accessZone to use
   * @param downstreamAccessNode when true access node is chosen as the downstream node of the segment,
   *                             when false, upstream node is chosen
   * @param accessLinkSegment to use
   * @return created directed connectoid
   */
  public default DirectedConnectoid registerNew(
      Zone accessZone, boolean downstreamAccessNode, LinkSegment accessLinkSegment){
    return registerNew(
        accessZone, downstreamAccessNode, accessLinkSegment, ConnectoidAccessZoneEntry.DEFAULT_LENGTH_KM.get());
  }

  /** Create a new directed connectoid, with default length 0, and choose downstream access node based on the link
   * segment provided
   *
   * @param accessLinkSegment to use
   * @param accessZone to use
   * @param syncXmlIdToId flag indicating if we should sync the XML ids to internal ids
   * @param allowedModes to apply
   * @return created directed connectoid
   */
  public default DirectedConnectoid registerNewDownstreamAccess(
      Zone accessZone, LinkSegment accessLinkSegment, boolean syncXmlIdToId, Collection<Mode> allowedModes){
    return registerNew(accessZone, true, accessLinkSegment, syncXmlIdToId, allowedModes);
  }

  /** Create a new directed connectoid, with default length 0
   *
   * @param downstreamAccessNode when true access node is chosen as the downstream node of the segment, when false,
   *                             upstream node is chosen
   * @param accessLinkSegment to use
   * @param accessZone to use
   * @param syncXmlIdToId flag indicating if we should sync the XML ids to internal ids
   * @param allowedModes to apply
   * @return created directed connectoid
   */
  public default DirectedConnectoid registerNew(
      Zone accessZone,
      final boolean downstreamAccessNode,
      LinkSegment accessLinkSegment,
      boolean syncXmlIdToId,
      Collection<Mode> allowedModes){
    DirectedConnectoid newEntity = registerNew(accessZone, downstreamAccessNode, accessLinkSegment);
    if(syncXmlIdToId == true) {
      newEntity.setXmlId(newEntity.getId());
    }
    newEntity.addAllowedModes(accessZone, allowedModes);
    return newEntity;
  }

}
