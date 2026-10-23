package org.goplanit.utils.network.virtual.physical;

import java.util.Collection;
import java.util.function.Supplier;

import org.goplanit.utils.graph.GraphEntityFactory;
import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.virtual.graph.CentroidVertex;
import org.goplanit.utils.zoning.connectoid.Connectoid;
import org.goplanit.utils.zoning.connectoid.ZoneConnectoidType;

/** Factory interface for connectoid edges
 * 
 * @author markr
 *
 */
public interface ConnectoidLinkFactory extends GraphEntityFactory<ConnectoidLink>{

  /**
   * Create new connectoid link from a specified connectoid to all centroids of the zones this connectoid has
   * registered as access zone.
   * 
   * @param centroidVertex the centroidVertex connecting to the centroid, i.e., zone
   * @param nonCentroidVertex the other vertex connecting to the (physical) layer
   * @param lengthKm length in km to set
   * @param connectoid the link joins to the network, for the zone of the centroid vertex
   * @param connectoidType of the connectoid's access to that zone
   * @return newly created connectoid link (reference vertices not yet aware of connection these have to be
   * added afterwards)
   */
  public ConnectoidLink registerNew(CentroidVertex centroidVertex, DirectedVertex nonCentroidVertex, double lengthKm,
      Connectoid connectoid, ZoneConnectoidType connectoidType);

  /**
   * Set the producer of all modes known to the network, shared by every connectoid link created from then on. A link
   * uses it to list the modes it allows when the access zone entry of its connectoid lists none, since every mode is
   * allowed then
   *
   * @param allModesProducer producing all modes known to the network
   */
  public void setAllModesProducer(Supplier<? extends Collection<Mode>> allModesProducer);
}
