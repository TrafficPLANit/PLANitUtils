package org.goplanit.utils.graph.directed;

import java.util.Collection;

import org.goplanit.utils.graph.EdgeUtils;
import org.goplanit.utils.graph.Vertex;
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
  public abstract ConjugateEdgeSegment registerEdgeSegment(
      final EdgeSegment edgeSegment, final boolean directionAB, final boolean force);
  
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
  public abstract ConjugateDirectedEdge shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateDirectedEdge deepClone();
  
 
  /**
   * {@inheritDoc}
   */  
  @Override
  public default ConjugateEdgeSegment getEdgeSegment(boolean directionAb) {
    return directionAb ? getEdgeSegmentAb() : getEdgeSegmentBa();
  }
  
  /**
   * {@inheritDoc}
   */  
  @SuppressWarnings("unchecked")
  @Override
  public default Collection<? extends ConjugateEdgeSegment> getEdgeSegments(){
    return (Collection<? extends ConjugateEdgeSegment>) DirectedEdge.super.getEdgeSegments();
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
    DirectedEdge originalStartEdge = directionAb ? getVertexA().getOriginalEdge() : getVertexB().getOriginalEdge();
    DirectedEdge originalEndEdge = directionAb ? getVertexB().getOriginalEdge() : getVertexA().getOriginalEdge();

    EdgeSegment startEdgeSegment = null;
    EdgeSegment endEdgeSegment = null;
    if(originalStartEdge == null){
      // not possible to collect shared vertex. This suggests underlying source node
      // if endEdge A node is the source, then we get
      // pairing ( __ -> end edge - segment A->B), otherwise ( __ -> end edge - segment B->A)
      endEdgeSegment = originalEndEdge.getVertexA().getNumberOfEdges() == 1 ?
              originalEndEdge.getEdgeSegmentAb() : originalEndEdge.getEdgeSegmentBa();
    }else if(originalEndEdge==null){
      // not possible to collect shared vertex. This suggests underlying sink node
      // if Edge B node is the sink, then we get
      // pairing ( start edge - segment A->B --> __), otherwise ( start edge - segment B->A --> __)
      startEdgeSegment = originalStartEdge.getVertexB().getNumberOfEdges() == 1 ?
              originalStartEdge.getEdgeSegmentAb() : originalStartEdge.getEdgeSegmentBa();
    }else{
        // regular approach, use shared vertex to determine direction
        var originalSharedVertex = EdgeUtils.getSharedVertex(originalStartEdge, originalEndEdge);
        startEdgeSegment = originalStartEdge.isVertexA(originalSharedVertex) ?
                originalStartEdge.getEdgeSegmentBa() : originalStartEdge.getEdgeSegmentAb();
        endEdgeSegment = originalEndEdge.isVertexA(originalSharedVertex) ?
                  originalEndEdge.getEdgeSegmentAb() : originalEndEdge.getEdgeSegmentBa();
      }


    return Pair.of(startEdgeSegment, endEdgeSegment);
  }
  
}
