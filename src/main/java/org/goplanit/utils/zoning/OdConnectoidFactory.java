package org.goplanit.utils.zoning;

import org.goplanit.utils.id.ManagedIdEntityFactory;
import org.goplanit.utils.network.layer.physical.Node;

import static org.goplanit.utils.zoning.ConnectoidAccessZoneEntry.DEFAULT_LENGTH_KM;

/** Factory interface for undirected connectoids
 * 
 * @author markr
 *
 */
public interface OdConnectoidFactory extends ManagedIdEntityFactory<OdConnectoid>{

  /** factory method for undirected connectoid without any entries for a zone
   *
   * @param accessNode to use
     * @return created undirected connectoid
   */
  public OdConnectoid registerNew(Node accessNode);

  /** factory method for undirected connectoid
   *
   * @param accessNode to use
   * @param accessZone to use
   * @param type type of connectoid zone combination for the given access zone
   * @param length to use between connectoid access node and zone
   * @return created undirected connectoid
   */
  public default OdConnectoid registerNewWithUndirectedEntry(
      Zone accessZone, Node accessNode, ZoneConnectoidType type, double length){
    var connectoid = registerNew(accessNode);
    var entry = connectoid.createUndirectedAccessZoneEntry(accessZone, type);
    entry.setLengthKm(length);
    return connectoid;
  }

  /** factory method for undirected connectoid, with default length of 0 to use between zone and access node 
   * 
   * @param accessZone to use
   * @param accessNode to use
   * @return created undirected connectoid
   */
  public default OdConnectoid registerNewWithUndirectedEntry(Zone accessZone, Node accessNode, ZoneConnectoidType type){
    return registerNewWithUndirectedEntry(accessZone, accessNode, type, DEFAULT_LENGTH_KM.get());
  }

  /** factory method for undirected connectoid, with default length of 0 and type ACCESS/EGRESS to use between
   * zone and access node
   *
   * @param accessZone to use
   * @param accessNode to use
   * @return created undirected connectoid
   */
  public default OdConnectoid registerNewWithUndirectedEntry(Zone accessZone, Node accessNode){
    return registerNewWithUndirectedEntry(accessZone, accessNode, ZoneConnectoidType.ZONE_ACCESS_EGRESS);
  }

}
