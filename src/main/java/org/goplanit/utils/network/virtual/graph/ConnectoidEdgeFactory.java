package org.goplanit.utils.network.virtual.graph;

import org.goplanit.utils.graph.GraphEntityFactory;
import org.goplanit.utils.graph.directed.DirectedVertex;

/** Factory interface for connectoid edges
 * 
 * @author markr
 *
 */
public interface ConnectoidEdgeFactory extends GraphEntityFactory<ConnectoidDirectedEdge>{

  /**
   * Create new connectoid edges from a specified connectoid to all centroids of the zones this connectoid has registered as access zone.
   * 
   * @param centroidVertex the centroidVertex connecting to the centroid, i.e., zone
   * @param nonCentroidVertex the other vertex connecting to the (physical) layer
   * @param lengthKm length in km to set
   * @return newly created connectoid edge (reference vertices not yet aware of connection these have to be added afterwards)
   */
  public ConnectoidDirectedEdge registerNew(
          CentroidVertex centroidVertex, DirectedVertex nonCentroidVertex, double lengthKm);
}
