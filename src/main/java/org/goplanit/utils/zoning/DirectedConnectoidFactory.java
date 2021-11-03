package org.goplanit.utils.zoning;

import org.goplanit.utils.exceptions.PlanItException;
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
   * @throws PlanItException thrown if error
   */
  public abstract DirectedConnectoid registerNew(LinkSegment accessLinkSegment, Zone parentZone, double length) throws PlanItException;

  /** Create a new directed connectoid, with default length 0
   *  
   * @param accessLinkSegment to use
   * @param parentZone to use
   * @return created directed connectoid
   * @throws PlanItException thrown if error
   */  
  public abstract DirectedConnectoid registerNew(LinkSegment accessLinkSegment, Zone parentZone) throws PlanItException;

  
  /** Create a new directed connectoid with default length 0 and no parent zone (yet)
   *  
   * @param accessLinkSegment to use
   * @return created directed connectoid
   * @throws PlanItException thrown if error
   */  
  public abstract DirectedConnectoid registerNew(LinkSegment accessLinkSegment) throws PlanItException;
}
