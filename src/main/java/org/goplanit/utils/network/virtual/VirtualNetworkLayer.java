package org.goplanit.utils.network.virtual;

import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.ConjugateMacroscopicNetworkLayer;
import org.goplanit.utils.network.layer.macroscopic.*;
import org.goplanit.utils.network.layer.physical.Node;
import org.goplanit.utils.network.layer.physical.Nodes;
import org.goplanit.utils.network.layer.physical.UntypedPhysicalLayer;

/**
 * Virtual network layer consisting of centroid vertices, connectoid edges and connectoid segments
 *
 * @author markr
 */
public interface VirtualNetworkLayer extends UntypedVirtualLayer<CentroidVertex, ConnectoidEdge, ConnectoidSegment> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract VirtualNetworkLayer shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract VirtualNetworkLayer deepClone();

  /**
   * Access to connectoid edges
   *
   * @return connectoidEdges
   */
  public abstract ConnectoidEdges getConnectoidEdges();

  /**
   * Access to connectoid segments
   *
   * @return connectoidSegments
   */
  @Override
  public abstract ConnectoidSegments getConnectoidSegments();

  /**
   * Access each centroid's vertex
   *
   * @return connectoidEdges
   */
  @Override
  public abstract CentroidVertices getVertices();

  /** Create a conjugate version of this layer
   * 
   * @param idToken to use for generating ids within the layer
   * @return conjugate version of this layer
   */
  public abstract ConjugateVirtualNetworkLayer createConjugate(final IdGroupingToken idToken);

}
