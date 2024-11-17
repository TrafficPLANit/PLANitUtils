package org.goplanit.utils.network.virtual.graph.conjugate;

import java.util.Collection;

import org.goplanit.utils.graph.directed.ConjugateDirectedEdge;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.network.virtual.physical.conjugate.ConjugateConnectoidSegment;
import org.goplanit.utils.network.virtual.graph.ConnectoidDirectedEdge;
import org.goplanit.utils.network.virtual.graph.CentroidVertex;

/**
 * the connecting component between centroid and a first physical node in the network.
 * Note that all connectoids are directed edges but not all edges are connectoids
 * 
 * @author markr
 *
 */
public interface ConjugateConnectoidDirectedEdge extends ConjugateDirectedEdge, ConnectoidDirectedEdge {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidDirectedEdge shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidDirectedEdge deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidDirectedVertex getVertexA();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidDirectedVertex getVertexB();

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
