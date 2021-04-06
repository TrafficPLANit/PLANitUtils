package org.planit.utils.zoning;

import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.network.physical.Node;


/**
 * container and factory class for undirected connectoids
 * 
 * @author markr
 *
 */
public interface UndirectedConnectoids extends Connectoids<UndirectedConnectoid> {

  /** factory method for undirected connectoid
   * 
   * @param accessNode to use
   * @param parentZone to use
   * @param length to use between connectoid access node and zone
   * @return created undirected connectoid
   * @throws PlanItException thrown if error
   */
  public UndirectedConnectoid registerNew(Node accessNode, Zone parentZone, double length) throws PlanItException;

  /** factory method for undirected connectoid, with default length of 0 to use between zone and access node 
   * 
   * @param accessNode to use
   * @param parentZone to use
   * @return created undirected connectoid
   * @throws PlanItException thrown if error
   */  
  public UndirectedConnectoid registerNew(Node accessNode, Zone parentZone) throws PlanItException;

  /** factory method for undirected connectoid, with default length of 0 to use between (yet unknown parent) zone and access node 
   * 
   * @param accessNode to use
   * @return created undirected connectoid
   * @throws PlanItException thrown if error
   */    
  public UndirectedConnectoid registerNew(Node accessNode) throws PlanItException;
}
