package org.goplanit.utils.network.virtual;

import org.goplanit.utils.graph.GraphEntityDeepCopyMapper;
import org.goplanit.utils.graph.directed.DirectedVertex;

/**
 * Untyped Virtual network consisting of single layer since currently we only support a single layer for virtual networks
 *
 * @author markr
 */
public interface UntypedVirtualNetwork<L extends
        UntypedVirtualLayer<? extends DirectedVertex,? extends ConnectoidEdge,? extends ConnectoidSegment>> {

  /**
   * Access to the single virtual layer
   * @return layer
   */
  public abstract L getLayer();

  /**
   * free up memory by clearing contents for garbage collection
   */
  public abstract void clear();

  /**
   * identical {@link #clear()} only now all underlying managed ids are also reset
   */
  public abstract void reset();

  /** Log info on this virtual network
   *
   * @param prefix to use
   */
  public default void logInfo(String prefix){
      getLayer().logInfo(prefix);
  }

  /**
   * Verify if entire network is empty
   *
   * @return true if network is empty, false otherwise
   */
  public default boolean isEmpty(){
    return getLayer().isEmpty();
  }

  /**
   * Recreate the ids for all registered entities with or without resetting, this includes child managed ids, i.e.,
   * nested managedIdentities containers if so indicated
   *
   * @param resetManagedIdClass when true we reset the managedId's counter to zero (via its id class) before
   *                            recreating the ids, otherwise we simply recreate the managed id by
   *                            starting with the next available id without resetting
   */
  public abstract void recreateManagedIds(boolean resetManagedIdClass);

  /**
   * Perform shallow clone
   *
   * @return shallow copy
   */
  public abstract UntypedVirtualNetwork<L> shallowClone();

  /**
   * Perform deep clone
   *
   * @return deep copy
   */
  public abstract UntypedVirtualNetwork<L> deepClone();

  /**
   * Perform a deep clone where mappings between original and copy are captured in the two provided mappers
   *
   * @param connectoidEdgeMapper to use for tracking mapping between original and copied entity (may be null)
   * @param connectoidSegmentMapper to use for tracking mapping between original and copied entity (may be null)
   * @param connectoidVertexMapper to use for tracking mapping between original and copied entity (may be null)
   * @return deep copy
   */
  public UntypedVirtualNetwork<L> deepCloneWithMapping(
          GraphEntityDeepCopyMapper<? extends ConnectoidEdge> connectoidEdgeMapper,
          GraphEntityDeepCopyMapper<? extends ConnectoidSegment> connectoidSegmentMapper,
          GraphEntityDeepCopyMapper<? extends DirectedVertex> connectoidVertexMapper);
}
