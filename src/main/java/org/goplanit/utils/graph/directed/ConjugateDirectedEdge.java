package org.goplanit.utils.graph.directed;

import org.goplanit.utils.misc.Pair;

/**
 * Directed Edge interface connecting two vertices in a directional fashion. Each edge has one or
 * two underlying edge segments in a particular direction which may carry
 * additional information for each particular direction of the edge.
 * 
 * @author markr
 *
 */
public interface ConjugateDirectedEdge extends DirectedEdge {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateDirectedVertex getVertexA();

  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract ConjugateDirectedVertex getVertexB();

  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract ConjugateEdgeSegment registerEdgeSegment(final EdgeSegment edgeSegment, final boolean directionAB);
  
  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract ConjugateEdgeSegment removeEdgeSegmentAb();
  
  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract ConjugateEdgeSegment removeEdgeSegmentBa();

  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract ConjugateEdgeSegment getEdgeSegmentAb();
  
  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract ConjugateEdgeSegment getEdgeSegmentBa();
  
 
  /**
   * {@inheritDoc}
   */  
  @Override
  public default ConjugateEdgeSegment getEdgeSegment(boolean directionAb) {
    return directionAb ? getEdgeSegmentAb() : getEdgeSegmentBa();
  }
  
  /* NEW methods */
  
  /** Conjugate edge represents two adjacent edges in original form (potential turn movement).
   *  
   * @return directed original adjacent edge pair
   */
  public abstract Pair<DirectedEdge,DirectedEdge> getOriginalEdges(); 
  
}
