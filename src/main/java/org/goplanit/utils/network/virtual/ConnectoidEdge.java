package org.goplanit.utils.network.virtual;

import org.goplanit.utils.graph.Vertex;
import org.goplanit.utils.graph.directed.DirectedEdge;
import org.goplanit.utils.id.IdGenerator;
import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.zoning.Connectoid;

/**
 * the connecting component between centroid and a first physical node in the network.
 * Note that all connectoids are directed edges but not all edges are connectoids
 * 
 * @author markr
 *
 */
public interface ConnectoidEdge extends DirectedEdge{

  /**
   * Generate connectoid id
   *
   * @param tokenId contiguous id generation within this group for instances of this class
   * @return id of connectoid edge
   */
  public static long generateConnectoidEdgeId(final IdGroupingToken tokenId) {
    return IdGenerator.generateId(tokenId, Connectoid.class);
  }

  /** additional id class for generating connectoid edge ids */
  public static Class<ConnectoidEdge> CONNECTOID_EDGE_ID_CLASS = ConnectoidEdge.class;
  
  /**
   * Return class used to generate unique connectoid edge ids via the id generator
   * 
   * @return class type
   */
  public default Class<ConnectoidEdge> getConnectoidEdgeIdClass(){
    return CONNECTOID_EDGE_ID_CLASS;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidEdge shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidEdge deepClone();

  /**
   * Register connectoidSegment.
   * 
   * If there already exists a connectoidSegment for that direction it is replaced
   * and returned
   * 
   * @param connectoidSegment connectoid segment to be registered
   * @param directionAB direction of travel
   * @return replaced ConnectoidSegment
   */
  public abstract ConnectoidSegment registerConnectoidSegment(ConnectoidSegment connectoidSegment, boolean directionAB);

  /**
   * 
   * Return the unique id of this connectoid edge
   * 
   * @return id of this connectoid edge
   */
  public abstract long getConnectoidEdgeId();

  /** Collect the non-centroid vertex attached to the connectoid, which should always exist and only be a single one
   * @return non-centroid found, null if not found
   */
  public default Vertex getNonCentroidVertex() {
    var centroidVertex = getCentroidVertex();
    return getVertexB() == centroidVertex ? getVertexA() : getVertexB();
  }

  /** Collect the centroid vertex attached to the connectoid, which should always exist and only be a single one
   * @return centroid found, null if not found
   */
  public default CentroidVertex getCentroidVertex() {
    if(getVertexA() instanceof CentroidVertex) {
      return (CentroidVertex) getVertexA();
    }else if (getVertexB() instanceof CentroidVertex) {
      return (CentroidVertex) getVertexB();
    }else {
      return null;
    }
  }

}
