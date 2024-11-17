package org.goplanit.utils.network.virtual.graph.conjugate;

import org.goplanit.utils.graph.directed.ConjugateDirectedVertex;
import org.goplanit.utils.network.virtual.graph.ConnectoidDirectedEdge;
import org.goplanit.utils.network.virtual.graph.ConnectoidDirectedVertex;
import org.goplanit.utils.network.virtual.graph.CentroidVertex;

/**
 * Conjugate connectoid directed vertex is the conjugate of a connectoid directed vertex.
 * 
 * @author markr
 *
 */
public interface ConjugateConnectoidDirectedVertex extends ConjugateDirectedVertex, ConnectoidDirectedVertex {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidDirectedVertex shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidDirectedVertex deepClone();

  /** Original edge in original directed graph this conjugate represents
   * @return original edge
   */
  @Override
  public abstract ConnectoidDirectedEdge getOriginalEdge();

  /**
   * Access to original centroid vertex of the zone if available
   *
   * @return centroid vertex when available, null otherwise
   */
  public default CentroidVertex getCentroidVertex(){
    return hasOriginalEdge() ? getOriginalEdge().getCentroidVertex() : null;
  }
}
