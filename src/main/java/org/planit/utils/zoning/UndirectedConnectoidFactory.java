package org.planit.utils.zoning;

import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.id.ContainerisedManagedIdEntityFactory;
import org.planit.utils.network.layer.physical.Node;

/** Factory interface for undirected connectoids
 * 
 * @author markr
 *
 */
public interface UndirectedConnectoidFactory extends ContainerisedManagedIdEntityFactory<UndirectedConnectoid>{

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
