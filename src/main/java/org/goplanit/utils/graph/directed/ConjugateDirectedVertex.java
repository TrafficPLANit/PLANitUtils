package org.goplanit.utils.graph.directed;

import java.util.Collection;

/**
 * Conjugate of directed vertex representing an edge segment in "regular" directed graph
 * 
 * @author markr
 *
 */
public interface ConjugateDirectedVertex extends DirectedVertex{
  
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Collection<? extends ConjugateDirectedEdge> getEdges();
  
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Iterable<? extends ConjugateEdgeSegment> getEntryEdgeSegments();
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Iterable<? extends ConjugateEdgeSegment> getExitEdgeSegments();  
  


  /**
   * {@inheritDoc}
   */
  @Override
  default ConjugateEdgeSegment getEdgeSegment(DirectedVertex otherVertex) {
   return (ConjugateEdgeSegment ) DirectedVertex.super.getEdgeSegment(otherVertex);
   }


  /** Original edge in original directed graph this conjugate represents
   * @return original edge 
   */
  public abstract DirectedEdge getOriginalEdge();
    
}
