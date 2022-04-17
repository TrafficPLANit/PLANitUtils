package org.goplanit.utils.graph.directed;

import org.goplanit.utils.graph.Edge;
import org.goplanit.utils.misc.Pair;

/**
 * Conjugate version of edge segment representing connection between two edge segments on origin network
 * 
 * @author markr
 *
 */
public interface ConjugateEdgeSegment extends EdgeSegment{
  
  /** id class for generating ids */
  public static final Class<ConjugateEdgeSegment> CONJUGATE_EDGE_SEGMENT_ID_CLASS = ConjugateEdgeSegment.class;
  
  /**
   * For a given conjugate edge segment extract the original adjacent edge segments it represents, i.e. a turn. Thse segments are provided from upstream to downstream direction
   * 
   * @param conjugateEdgeSegment to extract from
   * @return pair of edge segments representing turn from a-to-b-to-c by means of (segmentAb, segment Bc)
   */
  public static Pair<? extends EdgeSegment, ? extends EdgeSegment> getOriginalAdjcentEdgeSegments(final ConjugateEdgeSegment conjugateEdgeSegment) {
    DirectedEdge startEdge = conjugateEdgeSegment.getUpstreamVertex().getOriginalEdge();
    DirectedEdge endEdge = conjugateEdgeSegment.getDownstreamVertex().getOriginalEdge();
    var sharedVertex = Edge.getSharedVertex(startEdge, endEdge);

    var startEdgeSegment = startEdge.isVertexA(sharedVertex) ? startEdge.getEdgeSegmentBa() : startEdge.getEdgeSegmentAb();
    var endEdgeSegment = endEdge.isVertexA(sharedVertex) ? startEdge.getEdgeSegmentAb() : startEdge.getEdgeSegmentBa();

    return Pair.of(startEdgeSegment, endEdgeSegment);
  }  
  
  /**
   * {@inheritDoc}
   */
  @Override
  public default ConjugateDirectedVertex getUpstreamVertex() {
    return (ConjugateDirectedVertex) EdgeSegment.super.getUpstreamVertex();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public default ConjugateDirectedVertex getDownstreamVertex() {
    return (ConjugateDirectedVertex) EdgeSegment.super.getDownstreamVertex();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override  
  public abstract ConjugateDirectedEdge getParent();
  
  /**
   * {@inheritDoc}
   */
  @Override 
  public abstract ConjugateEdgeSegment clone();
  
  /**
   * All edges use the CONJUGATE_EDGE_SEGMENT_ID_CLASS to generate the unique internal ids
   */
  @Override
  public default Class<? extends ConjugateEdgeSegment> getIdClass() {
    return CONJUGATE_EDGE_SEGMENT_ID_CLASS;
  }   
   
  /**
   * {@inheritDoc}
   */
  @Override
  public default ConjugateEdgeSegment getOppositeDirectionSegment() {
    return (ConjugateEdgeSegment) EdgeSegment.super.getOppositeDirectionSegment();  
  }

  /**
   * Adjacent edge segments in original graph for this conjugate
   * @return edge segment pair
   */
  public abstract Pair<? extends EdgeSegment,? extends EdgeSegment> getOriginalAdjcentEdgeSegments();

}
