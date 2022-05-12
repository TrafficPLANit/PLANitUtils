package org.goplanit.utils.zoning;

import org.goplanit.utils.id.ManagedIdEntityFactory;
import org.goplanit.utils.network.layer.physical.LinkSegment;

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

  
  /** Create a new directed connectoid with default length 0 and no parent zone (yet)
   *  
   * @param accessLinkSegment to use
   * @return created directed connectoid
   */  
  public abstract DirectedConnectoid registerNew(LinkSegment accessLinkSegment);
}
