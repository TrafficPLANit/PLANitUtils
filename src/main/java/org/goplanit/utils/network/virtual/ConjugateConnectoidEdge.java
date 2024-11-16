package org.goplanit.utils.network.virtual;

import java.util.Collection;

import org.goplanit.utils.graph.directed.ConjugateDirectedEdge;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.network.layers.UntypedPhysicalNetworkLayers;

/**
 * the connecting component between centroid and a first physical node in the network.
 * Note that all connectoids are directed edges but not all edges are connectoids
 * 
 * @author markr
 *
 */
public interface ConjugateConnectoidEdge extends ConjugateDirectedEdge, ConnectoidEdge {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidEdge shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidEdge deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidNode getVertexA();

  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract ConjugateConnectoidNode getVertexB();

  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract ConjugateConnectoidSegment registerEdgeSegment(
      final EdgeSegment edgeSegment, final boolean directionAB, final boolean force);
  
  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract ConjugateConnectoidSegment removeEdgeSegmentAb();
  
  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract ConjugateConnectoidSegment removeEdgeSegmentBa();

  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract ConjugateConnectoidSegment getEdgeSegmentAb();
  
  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract ConjugateConnectoidSegment getEdgeSegmentBa();
  
 
  /**
   * {@inheritDoc}
   */  
  @Override
  public default ConjugateConnectoidSegment getEdgeSegment(boolean directionAb) {
    return directionAb ? getEdgeSegmentAb() : getEdgeSegmentBa();
  }
  
  /**
   * {@inheritDoc}
   */  
  @SuppressWarnings("unchecked")
  @Override
  public default Collection<? extends ConjugateConnectoidSegment> getEdgeSegments(){
    return (Collection<? extends ConjugateConnectoidSegment>) ConjugateDirectedEdge.super.getEdgeSegments();
  }

  /**
   * Provide access to the underlying original Centroid vertex of the zone (if available)
   *
   * @return centroid vertex, null if not found
   */
  @Override
  public default CentroidVertex getCentroidVertex(){
    if(getVertexA() != null && getVertexA().getOriginalEdge() != null){
      return getVertexA().getOriginalEdge().getCentroidVertex();
    }else if(getVertexB() != null && getVertexB().getOriginalEdge() != null){
      return getVertexB().getOriginalEdge().getCentroidVertex();
    }
    return null;
  }
  
}
