package org.goplanit.utils.network.virtual;

import org.goplanit.utils.graph.GraphEntityDeepCopyMapper;
import org.goplanit.utils.id.IdGroupingToken;

import java.util.logging.Logger;

/**
 * Model free virtual network interface which is part of the zoning and holds all the virtual infrastructure connecting the zones to the physical road network.
 * 
 * @author markr
 */
public interface VirtualNetwork {

  public static final Logger LOGGER = Logger.getLogger(VirtualNetwork.class.getCanonicalName());

  /**
   * Access to connectoid segments
   * 
   * @return connectoidSegments
   */
  public abstract ConnectoidSegments getConnectoidSegments();

  /**
   * Access to connectoid edges
   * 
   * @return connectoidEdges
   */
  public abstract ConnectoidEdges getConnectoidEdges();

  /**
   * Access each centroid's vertex
   *
   * @return connectoidEdges
   */
  public abstract CentroidVertices getCentroidVertices();

  /**
   * free up memory by clearing contents for garbage collection
   */
  public abstract void clear();
  
  /**
   * identical {@link #clear()} only now all underlying managed ids are also reset
   */
  public abstract void reset();   

  /**
   * Create a conjugate version of this virtual network, also known as the edge-to-vertex-dual representation, where all connectoidedges/edge segments become (dangling) conjugate
   * vertices.
   * <p>
   * It is recommended to first create the conjugate of this virtual network BEFORE creating conjugates of network layers. The latter takes a conjugate zoning as input such that it
   * can connect the conjugate virtual nodes to the conjugate network layer where appropriate, otherwise these connections are ignored
   * 
   * @param idToken to use for conjugate entity creation 
   * @return conjugate version of this virtual network's edges/edgesgments
   */
  public abstract ConjugateVirtualNetwork createConjugate(IdGroupingToken idToken);

  /**
   * Perform shallow clone
   *
   * @return shallow copy
   */
  public abstract VirtualNetwork shallowClone();

  /**
   * Perform deep clone
   *
   * @return deep copy
   */
  public abstract VirtualNetwork deepClone();

  /**
   * Perform a deep clone where mappings between original and copy are captured in the two provided mappers
   *
   * @param connectoidEdgeMapper to use for tracking mapping between original and copied entity (may be null)
   * @param connectoidSegmentMapper to use for tracking mapping between original and copied entity (may be null)
   * @param centroidVertexMapper to use for tracking mapping between original and copied entity (may be null)
   * @return deep copy
   */
  public VirtualNetwork deepCloneWithMapping(
      GraphEntityDeepCopyMapper<ConnectoidEdge> connectoidEdgeMapper,
      GraphEntityDeepCopyMapper<ConnectoidSegment> connectoidSegmentMapper,
      GraphEntityDeepCopyMapper<CentroidVertex> centroidVertexMapper);

}