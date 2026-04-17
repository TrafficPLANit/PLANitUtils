package org.goplanit.utils.zoning;

import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.id.ManagedIdEntityFactory;
import org.goplanit.utils.network.layer.physical.Node;

/** Factory interface for undirected connectoids
 * 
 * @author markr
 *
 */
public interface UndirectedConnectoidFactory extends ManagedIdEntityFactory<UndirectedConnectoid>{

  /** factory method for undirected connectoid
   *
   * @param accessNode to use
   * @param accessZone to use
   * @param type type of connectoid zone combination for the given access zone
   * @param length to use between connectoid access node and zone
   * @return created undirected connectoid
   */
  public UndirectedConnectoid registerNew(Zone accessZone, Node accessNode, ZoneConnectoidType type, double length);

  /** factory method for undirected connectoid with default type of #ZoneConnectoidType.TRAVELLER_ACCESS
   * 
   * @param accessNode to use
   * @param accessZone to use
   * @param length to use between connectoid access node and zone
   * @return created undirected connectoid
   */
  public UndirectedConnectoid registerNew(Zone accessZone, Node accessNode, double length);

  /** factory method for undirected connectoid, with default length of 0 to use between zone and access node 
   * 
   * @param accessNode to use
   * @param accessZone to use
   * @return created undirected connectoid
   */
  public UndirectedConnectoid registerNew(Zone accessZone, Node accessNode);

  /** factory method for undirected connectoid, with default length of 0 to use between
   * (yet unknown parent) zone and access node
   * 
   * @param accessNode to use
   * @return created undirected connectoid
   */
  public UndirectedConnectoid registerNew(Node accessNode);
}
