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
public interface ConjugateConnectoidEdge extends ConjugateDirectedEdge {

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
 
  /** additional id class for generating conjugate connectoid edge ids */
  public static Class<ConjugateConnectoidEdge> CONJUGATE_CONNECTOID_EDGE_ID_CLASS = ConjugateConnectoidEdge.class;
  
  /**
   * Return class used to generate unique conjugate connectoid edge ids via the id generator
   * 
   * @return class type
   */
  public default Class<ConjugateConnectoidEdge> getConnectoidEdgeIdClass(){
    return CONJUGATE_CONNECTOID_EDGE_ID_CLASS;
  }    


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
  public abstract ConjugateConnectoidSegment registerEdgeSegment(final EdgeSegment edgeSegment, final boolean directionAB);
  
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
  
}
