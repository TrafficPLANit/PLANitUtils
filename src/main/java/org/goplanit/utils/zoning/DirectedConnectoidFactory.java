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
   * @param accessLinkSegment to use
   * @param parentZone to use
   * @param length to use for distance between zone and connectoid
   * @return created directed connectoid
   */
  public abstract DirectedConnectoid registerNew(LinkSegment accessLinkSegment, Zone parentZone, double length);

  /** Create a new directed connectoid, with default length 0
   *  
   * @param accessLinkSegment to use
   * @param parentZone to use
   * @return created directed connectoid
   */  
  public abstract DirectedConnectoid registerNew(LinkSegment accessLinkSegment, Zone parentZone);

  /** Create a new directed connectoid, with default length 0
   *
   * @param accessLinkSegment to use
   * @param parentZone to use
   * @param syncXmlIdToId flag indicating if we should sync the XML ids to internal ids
   * @param allowedModes to apply
   * @return created directed connectoid
   */
  public default DirectedConnectoid registerNew(LinkSegment accessLinkSegment, Zone parentZone, boolean syncXmlIdToId, Collection<Mode> allowedModes){
    DirectedConnectoid newEntity = registerNew(accessLinkSegment, parentZone);
    if(syncXmlIdToId == true) {
      newEntity.setXmlId(newEntity.getId());
    }
    newEntity.addAllowedModes(parentZone, allowedModes);
    return newEntity;
  }

  
  /** Create a new directed connectoid with default length 0 and no parent zone (yet)
   *  
   * @param accessLinkSegment to use
   * @return created directed connectoid
   */  
  public abstract DirectedConnectoid registerNew(LinkSegment accessLinkSegment);
}
