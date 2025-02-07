package org.goplanit.utils.network.virtual.physical;

import org.goplanit.utils.graph.GraphEntityFactory;
import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.network.virtual.graph.CentroidVertex;

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
   * @return newly created connectoid link (reference vertices not yet aware of connection these have to be
   * added afterwards)
   */
  public ConnectoidLink registerNew(CentroidVertex centroidVertex, DirectedVertex nonCentroidVertex, double lengthKm);
}
