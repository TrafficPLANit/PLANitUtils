package org.planit.utils.zoning;

import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.network.layer.physical.LinkSegment;


/**
 * container and factory class for directed connectoids
 * 
 * @author markr
 *
 */
public interface DirectedConnectoids extends Connectoids<DirectedConnectoid> {


  /** Create a new directed connectoid
   *  
   * @param accessLinkSegment to use
   * @param parentZone to use
   * @param length to use for distance between zone and connectoid
   * @return created directed connectoid
   * @throws PlanItException thrown if error
   */
  public DirectedConnectoid registerNew(LinkSegment accessLinkSegment, Zone parentZone, double length) throws PlanItException;

  /** Create a new directed connectoid, with default length 0
   *  
   * @param accessLinkSegment to use
   * @param parentZone to use
   * @return created directed connectoid
   * @throws PlanItException thrown if error
   */  
  public DirectedConnectoid registerNew(LinkSegment accessLinkSegment, Zone parentZone) throws PlanItException;

  
  /** Create a new directed connectoid with default length 0 and no parent zone (yet)
   *  
   * @param accessLinkSegment to use
   * @return created directed connectoid
   * @throws PlanItException thrown if error
   */  
  public DirectedConnectoid registerNew(LinkSegment accessLinkSegment) throws PlanItException;
  
}
