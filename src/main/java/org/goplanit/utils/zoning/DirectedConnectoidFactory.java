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

  /** Create a new directed connectoid, without zone attached and using default length
   *
   * @param downstreamAccessNode when true access node is chosen as the downstream node of the segment, when false, upstream node is chosen
   * @param accessLinkSegment to use
   * @return created directed connectoid
   */
  public abstract DirectedConnectoid registerNew(final boolean downstreamAccessNode, LinkSegment accessLinkSegment);

  /** Create a new directed connectoid
   *
   * @param downstreamAccessNode when true access node is chosen as the downstream node of the segment, when false, upstream node is chosen
   * @param accessLinkSegment to use
   * @param parentZone to use
   * @param length to use for distance between zone and connectoid
   * @return created directed connectoid
   */
  public abstract DirectedConnectoid registerNew(final boolean downstreamAccessNode, LinkSegment accessLinkSegment, Zone parentZone, double length);

  /** Create a new directed connectoid, with default length 0
   * @param downstreamAccessNode when true access node is chosen as the downstream node of the segment, when false, upstream node is chosen
   * @param accessLinkSegment to use
   * @param parentZone to use
   * @return created directed connectoid
   */  
  public default DirectedConnectoid registerNew(final boolean downstreamAccessNode, LinkSegment accessLinkSegment, Zone parentZone){
    return registerNew(downstreamAccessNode, accessLinkSegment, parentZone, Connectoid.DEFAULT_LENGTH_KM);
  }

  /** Create a new directed connectoid, with default length 0, and choose downstream access node based on the link segment provided
   *
   * @param accessLinkSegment to use
   * @param parentZone to use
   * @param syncXmlIdToId flag indicating if we should sync the XML ids to internal ids
   * @param allowedModes to apply
   * @return created directed connectoid
   */
  public default DirectedConnectoid registerNew(LinkSegment accessLinkSegment, Zone parentZone, boolean syncXmlIdToId, Collection<Mode> allowedModes){
    return registerNew(true, accessLinkSegment, parentZone, syncXmlIdToId, allowedModes);
  }

  /** Create a new directed connectoid, with default length 0
   *
   * @param downstreamAccessNode when true access node is chosen as the downstream node of the segment, when false, upstream node is chosen
   * @param accessLinkSegment to use
   * @param parentZone to use
   * @param syncXmlIdToId flag indicating if we should sync the XML ids to internal ids
   * @param allowedModes to apply
   * @return created directed connectoid
   */
  public default DirectedConnectoid registerNew(
      final boolean downstreamAccessNode, LinkSegment accessLinkSegment, Zone parentZone, boolean syncXmlIdToId, Collection<Mode> allowedModes){
    DirectedConnectoid newEntity = registerNew(downstreamAccessNode, accessLinkSegment, parentZone);
    if(syncXmlIdToId == true) {
      newEntity.setXmlId(newEntity.getId());
    }
    newEntity.addAllowedModes(parentZone, allowedModes);
    return newEntity;
  }

}
