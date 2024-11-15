package org.goplanit.utils.network.virtual;

import org.goplanit.utils.network.layer.physical.ConjugateNode;
import org.goplanit.utils.network.layer.physical.ConjugateNodes;

/**
 * Conjugate virtual network layer consisting of conjugate nodes, conjugate connectoid edges and
 * conjugate connectoid segments
 *
 * @author markr
 */
public interface ConjugateVirtualNetworkLayer extends
        UntypedVirtualLayer<ConjugateNode, ConjugateConnectoidEdge, ConjugateConnectoidSegment> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateVirtualNetworkLayer shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateVirtualNetworkLayer deepClone();

  /**
   * Access to connectoid edges
   *
   * @return connectoid edges
   */
  @Override
  public abstract ConjugateConnectoidEdges getConnectoidEdges();

  /**
   * Access to connectoid segments
   *
   * @return connectoid segments
   */
  @Override
  public abstract ConjugateConnectoidSegments getConnectoidSegments();

  /**
   * Access conjugate nodes
   *
   * @return conjugate nodes
   */
  @Override
  public abstract ConjugateNodes getVertices();

}
