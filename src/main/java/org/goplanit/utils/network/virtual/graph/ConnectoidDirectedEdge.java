package org.goplanit.utils.network.virtual.graph;

import org.goplanit.utils.graph.Vertex;
import org.goplanit.utils.graph.directed.DirectedEdge;

/**
 * the connecting component between centroid and a first physical node in the network.
 * Note that all connectoids are directed edges but not all edges are connectoids
 * 
 * @author markr
 *
 */
public interface ConnectoidDirectedEdge extends DirectedEdge{

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidDirectedEdge shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidDirectedEdge deepClone();

  /** Collect the non-centroid vertex attached to the connectoid, which should always exist and only be a single one
   * @return non-centroid found, null if not found
   */
  public default Vertex getNonCentroidVertex() {
    var centroidVertex = getCentroidVertex();
    return getVertexB() == centroidVertex ? getVertexA() : getVertexB();
  }

  /** Collect the centroid vertex attached to the connectoid, which should always exist and only be a single one
   * @return centroid found, null if not found
   */
  public default CentroidVertex getCentroidVertex() {
    if(getVertexA() instanceof CentroidVertex) {
      return (CentroidVertex) getVertexA();
    }else if (getVertexB() instanceof CentroidVertex) {
      return (CentroidVertex) getVertexB();
    }else {
      return null;
    }
  }

}
