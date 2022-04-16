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


  /** Original edge segment in original directed graph in AB direction
   * @return original edge segment Ab
   */
  public abstract EdgeSegment getOriginalEdgeSegmentAb();
  
  /** Original edge segment in original directed graph in BA direction
   * @return original edge segment Ba
   */
  public abstract EdgeSegment getOriginalEdgeSegmentBa();  
}
