package org.goplanit.utils.network.virtual;

import org.goplanit.utils.graph.directed.ConjugateDirectedVertex;
import org.goplanit.utils.graph.directed.DirectedEdge;
import org.goplanit.utils.network.layer.physical.Node;

/**
 * Conjugate Node is the conjugate of a normal link. It is expected that its id is synced with the original link it represents
 * 
 * @author markr
 *
 */
public interface ConjugateConnectoidNode extends ConjugateDirectedVertex, Node {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidNode shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidNode deepClone();

  /** Original edge in original directed graph this conjugate represents
   * @return original edge
   */
  @Override
  public abstract ConnectoidEdge getOriginalEdge();

  /**
   * Access to original centroid vertex of the zone if available
   *
   * @return centroid vertex when available, null otherwise
   */
  public default CentroidVertex getCentroidVertex(){
    return hasOriginalEdge() ? getOriginalEdge().getCentroidVertex() : null;
  }
}
