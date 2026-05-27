package org.goplanit.utils.network.virtual;

import org.goplanit.utils.graph.GraphEntityDeepCopyMapper;
import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.id.ManagedIdDeepCopyMapper;
import org.goplanit.utils.network.layer.physical.Movement;
import org.goplanit.utils.network.virtual.graph.ConnectoidDirectedEdge;
import org.goplanit.utils.network.virtual.physical.ConnectoidSegment;

/**
 * Model free virtual network interface which is part of the zoning and holds all the virtual infrastructure connecting
 * the zones to the physical road network.
 * 
 * @author markr
 */
public interface VirtualNetwork extends UntypedVirtualNetwork<VirtualNetworkLayer>{

  /**
   * Create a conjugate version of this virtual network, also known as the edge-to-vertex-dual representation, where
   * all connectoidedges/edge segments become (dangling) conjugate vertices.
   * <p>
   * It is recommended to first create the conjugate of this virtual network BEFORE creating conjugates of network
   * layers. The latter takes a conjugate zoning as input such that it can connect the conjugate virtual nodes to
   * the conjugate network layer where appropriate, otherwise these connections are ignored
   * </p>
   * 
   * @param idToken to use for conjugate entity creation
   * @param resetManagedIdToken when true reset token for id generation, when false do not
   * @return conjugate version of this virtual network
   */
  public abstract ConjugateVirtualNetwork createConjugate(IdGroupingToken idToken, boolean resetManagedIdToken);

  /**
   * Perform shallow clone
   *
   * @return shallow copy
   */
  @Override
  public abstract VirtualNetwork shallowClone();

  /**
   * Perform deep clone
   *
   * @return deep copy
   */
  @Override
  public abstract VirtualNetwork deepClone();

  /**
   * Perform a deep clone where mappings between original and copy are captured in the two provided mappers
   *
   * @param connectoidEdgeMapper to use for tracking mapping between original and copied entity (may be null)
   * @param connectoidSegmentMapper to use for tracking mapping between original and copied entity (may be null)
   * @param centroidVertexMapper to use for tracking mapping between original and copied entity (may be null)
   * @param movementMapper to use for tracking mapping between original and copied entity (may be null)
   * @return deep copy
   */
  @Override
  public VirtualNetwork deepCloneWithMapping(
          GraphEntityDeepCopyMapper<? extends ConnectoidDirectedEdge> connectoidEdgeMapper,
          GraphEntityDeepCopyMapper<? extends ConnectoidSegment> connectoidSegmentMapper,
          GraphEntityDeepCopyMapper<? extends DirectedVertex> centroidVertexMapper,
          ManagedIdDeepCopyMapper<Movement> movementMapper);


}