package org.goplanit.utils.graph.directed;

import org.goplanit.utils.graph.EdgeUtils;
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
  public abstract Pair<? extends DirectedEdge,? extends DirectedEdge> getOriginalAdjacentEdges(); 
  
  /** Collect original pair of edge segments that this conjugate in given direction makes up for
   * @param directionAb conjugate direction to use
   * @return pair of original edge segments (can be partially empty/null if combination does not exist)
   */
  public default Pair<? extends EdgeSegment, ? extends EdgeSegment> getOriginalAdjacentEdgeSegments(boolean directionAb){
    DirectedEdge startEdge = directionAb ? getVertexA().getOriginalEdge() : getVertexB().getOriginalEdge();
    DirectedEdge endEdge = directionAb ? getVertexB().getOriginalEdge() : getVertexA().getOriginalEdge();
    var sharedVertex = EdgeUtils.getSharedVertex(startEdge, endEdge);
  
    var startEdgeSegment = startEdge.isVertexA(sharedVertex) ? startEdge.getEdgeSegmentBa() : startEdge.getEdgeSegmentAb();
    var endEdgeSegment = endEdge.isVertexA(sharedVertex) ? startEdge.getEdgeSegmentAb() : startEdge.getEdgeSegmentBa();
  
    return Pair.of(startEdgeSegment, endEdgeSegment);
  }
  
}
