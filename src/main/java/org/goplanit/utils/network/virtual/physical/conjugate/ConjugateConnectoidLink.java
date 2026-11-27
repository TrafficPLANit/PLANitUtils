package org.goplanit.utils.network.virtual.physical.conjugate;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.network.layer.physical.ConjugateLink;
import org.goplanit.utils.network.virtual.physical.ConnectoidLink;
import org.goplanit.utils.network.virtual.graph.CentroidVertex;

import java.util.Collection;

/**
 * Conjugate version of a connectoid link
 * 
 * @author markr
 *
 */
public interface ConjugateConnectoidLink extends ConnectoidLink, ConjugateLink {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidLink shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidLink deepClone();

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
    return (Collection<? extends ConjugateConnectoidSegment>) ConjugateLink.super.getEdgeSegments();
  }

  /**
   * Provide access to the underlying original Centroid vertex of the zone (if available)
   *
   * @return centroid vertex, null if not found
   */
  @Override
  public default CentroidVertex getCentroidVertex(){
    if(getVertexA() != null && getVertexA().getOriginalEdgeSegment() != null){
      return getVertexA().getOriginalEdgeSegment().getParent().getCentroidVertex();
    }else if(getVertexB() != null && getVertexB().getOriginalEdgeSegment() != null){
      return getVertexB().getOriginalEdgeSegment().getParent().getCentroidVertex();
    }
    return null;
  }
  
}
