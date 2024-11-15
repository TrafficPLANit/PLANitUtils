package org.goplanit.utils.network.virtual;

import org.goplanit.utils.graph.GraphEntities;
import org.goplanit.utils.graph.GraphEntityDeepCopyMapper;
import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.network.layer.UntypedDirectedGraphLayer;
import org.goplanit.utils.network.layer.physical.Link;
import org.goplanit.utils.network.layer.physical.LinkSegment;
import org.goplanit.utils.network.layer.physical.Node;

/**
 * Virtual network layer consisting of vertices (centroids vertices), connectoid edges and connectoid segments
 *
 * @author markr
 */
public interface UntypedVirtualLayer<V extends DirectedVertex, E extends ConnectoidEdge, ES extends ConnectoidSegment>
        extends UntypedDirectedGraphLayer<V, E, ES> {

  /**
   * Access to connectoid segments
   *
   * @return connectoidSegments
   */
  public abstract GraphEntities<ES> getConnectoidSegments();

  /**
   * Access to connectoid edges
   *
   * @return connectoidEdges
   */
  public abstract GraphEntities<E> getConnectoidEdges();

  /**
   * Access virtual network vertices
   *
   * @return connectoidEdges
   */
  public abstract GraphEntities<V> getVertices();


  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UntypedVirtualLayer<V,E,ES> shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UntypedVirtualLayer<V,E,ES> deepClone();

  /**
   * Perform a deep clone where mappings between original and copy are captured in the two provided mappers
   *
   * @param connectoidEdgeMapper to use for tracking mapping between original and copied entity (may be null)
   * @param connectoidSegmentMapper to use for tracking mapping between original and copied entity (may be null)
   * @param vertexMapper to use for tracking mapping between original and copied entity (may be null)
   * @return deep copy
   */
  public UntypedVirtualLayer<V,E,ES> deepCloneWithMapping(
          GraphEntityDeepCopyMapper<E> connectoidEdgeMapper,
          GraphEntityDeepCopyMapper<ES> connectoidSegmentMapper,
          GraphEntityDeepCopyMapper<V> vertexMapper);

  /**
   * Clear the entire layer from vertices, edges, and segments
   */
  public default void clear(){
    getVertices().clear();
    getConnectoidEdges().clear();
    getConnectoidSegments().clear();
  }
}
