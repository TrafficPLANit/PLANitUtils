package org.goplanit.utils.graph.directed;

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
  public abstract  ConjugateDirectedEdge getParentEdge();
  
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
   * Entry segment in original graph for this conjugate
   * @return edge segment
   */
  public abstract EdgeSegment getOriginalEntrySegment();

  /**
   * Exit segment in original graph for this conjugate
   * @return edge segment
   */
  public abstract EdgeSegment getOriginalExitSegment();
}
