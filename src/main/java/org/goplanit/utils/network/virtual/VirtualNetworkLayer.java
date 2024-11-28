package org.goplanit.utils.network.virtual;

import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.network.virtual.graph.CentroidVertex;
import org.goplanit.utils.network.virtual.graph.CentroidVertices;
import org.goplanit.utils.network.virtual.physical.ConnectoidLink;
import org.goplanit.utils.network.virtual.physical.ConnectoidLinks;
import org.goplanit.utils.network.virtual.physical.ConnectoidSegment;
import org.goplanit.utils.network.virtual.physical.ConnectoidSegments;

/**
 * Virtual network layer consisting of centroid vertices, connectoid edges and connectoid segments
 *
 * @author markr
 */
public interface VirtualNetworkLayer extends UntypedVirtualLayer<CentroidVertex, ConnectoidLink, ConnectoidSegment> {

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
  public abstract ConnectoidLinks getConnectoidLinks();

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
   * @param resetIdToken when true reset the id token before creating ids, otherwise keep as is
   * @return conjugate version of this layer
   */
  public abstract ConjugateVirtualNetworkLayer createConjugate(
          final IdGroupingToken idToken, final boolean resetIdToken);

}
