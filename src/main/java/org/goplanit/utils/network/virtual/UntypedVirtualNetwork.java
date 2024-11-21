package org.goplanit.utils.network.virtual;

import org.goplanit.utils.graph.GraphEntityDeepCopyMapper;
import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.misc.LoggingUtils;
import org.goplanit.utils.network.virtual.graph.ConnectoidDirectedEdge;
import org.goplanit.utils.network.virtual.physical.ConnectoidSegment;

/**
 * Untyped Virtual network consisting of single layer since currently we only support a single layer for virtual networks
 *
 * @author markr
 */
public interface UntypedVirtualNetwork<L extends
        UntypedVirtualLayer<? extends DirectedVertex,? extends ConnectoidDirectedEdge,? extends ConnectoidSegment>> {

  /**
   * Log general information on this virtual network to the user
   *
   * @param prefix to use
   */
  public abstract void logInfo(String prefix);

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
  public default void recreateManagedIds(boolean resetManagedIdClass){
    getLayer().recreateManagedIds(resetManagedIdClass);
  }

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
          GraphEntityDeepCopyMapper<? extends ConnectoidDirectedEdge> connectoidEdgeMapper,
          GraphEntityDeepCopyMapper<? extends ConnectoidSegment> connectoidSegmentMapper,
          GraphEntityDeepCopyMapper<? extends DirectedVertex> connectoidVertexMapper);
}
