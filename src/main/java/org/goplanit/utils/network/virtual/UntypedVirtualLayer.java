package org.goplanit.utils.network.virtual;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.geo.PlanitJtsUtils;
import org.goplanit.utils.graph.GraphEntityDeepCopyMapper;
import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.id.ManagedIdDeepCopyMapper;
import org.goplanit.utils.id.ManagedIdEntities;
import org.goplanit.utils.network.layer.NetworkLayer;
import org.goplanit.utils.network.layer.UntypedDirectedGraphLayer;
import org.goplanit.utils.network.layer.physical.Movement;
import org.goplanit.utils.network.layer.physical.Movements;
import org.goplanit.utils.network.virtual.graph.ConnectoidDirectedEdge;
import org.goplanit.utils.network.virtual.physical.ConnectoidSegment;

/**
 * Virtual network layer consisting of vertices (centroids vertices), connectoid edges and connectoid segments
 *
 * @author markr
 */
public interface UntypedVirtualLayer<
        V extends DirectedVertex,
        E extends ConnectoidDirectedEdge,
        ES extends ConnectoidSegment> extends UntypedDirectedGraphLayer<V, E, ES> {

  /**
   * Access to connectoid segments
   *
   * @return connectoidSegments
   */
  public abstract ManagedIdEntities<ES> getConnectoidSegments();

  /**
   * Access to connectoid edges
   *
   * @return connectoidEdges
   */
  public abstract ManagedIdEntities<E> getConnectoidLinks();

  /**
   * Access virtual network vertices
   *
   * @return connectoidEdges
   */
  public abstract ManagedIdEntities<V> getVertices();

  /**
   * Access each movement on virtual network.
   *
   * @return movements
   */
  public abstract Movements getMovements();

  /**
   * Recreate the ids for all registered entities with or without resetting, this includes child managed ids, i.e.,
   * nested managedIdentities containers if so indicated
   *
   * @param resetManagedIdClass when true we reset the managedId's counter to zero (via its id class) before
   *                            recreating the ids, otherwise we simply recreate the managed id by
   *                            starting with the next available id without resetting
   */
  public default void recreateManagedIds(boolean resetManagedIdClass){
    getConnectoidSegments().recreateIds(resetManagedIdClass);
    getConnectoidLinks().recreateIds(resetManagedIdClass);
    getVertices().recreateIds(resetManagedIdClass);
    getMovements().recreateIds(resetManagedIdClass);
  }

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
   * @param connectoidLinkMapper to use for tracking mapping between original and copied entity (may be null)
   * @param connectoidSegmentMapper to use for tracking mapping between original and copied entity (may be null)
   * @param vertexMapper to use for tracking mapping between original and copied entity (may be null)
   * @param movementMapper to apply in case of deep copy to each original to copy combination
   *                       (when provided, may be null)
   * @return deep copy
   */
  public UntypedVirtualLayer<V,E,ES> deepCloneWithMapping(
          GraphEntityDeepCopyMapper<E> connectoidLinkMapper,
          GraphEntityDeepCopyMapper<ES> connectoidSegmentMapper,
          GraphEntityDeepCopyMapper<V> vertexMapper,
          ManagedIdDeepCopyMapper<Movement> movementMapper);

  /**
   * Clear the entire layer from vertices, edges, and segments
   */
  public default void clear(){
    getVertices().clear();
    getConnectoidLinks().clear();
    getConnectoidSegments().clear();
    getMovements().clear();
  }

  public default boolean hasConnectoidLinks(){
    return getConnectoidLinks()!=null && !getConnectoidLinks().isEmpty();
  }

  public default boolean hasConnectoidSegments(){
    return getConnectoidSegments()!=null && !getConnectoidSegments().isEmpty();
  }

  public default boolean hasMovements(){
    return getMovements()!=null && !getMovements().isEmpty();
  }

}
