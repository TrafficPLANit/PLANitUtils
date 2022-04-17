package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.directed.ConjugateEdgeSegment;

/**
 * Interface for a turn, i.e. conjugate link segment.
 * 
 * @author markr
 *
 */
public interface ConjugateLinkSegment extends ConjugateEdgeSegment {
  
  /** additional id class for generating link segment ids */
  public static final Class<ConjugateLinkSegment> CONJUGATE_LINK_SEGMENT_ID_CLASS = ConjugateLinkSegment.class;   
  
  /**
   * Return class used to generate unique link ids via the id generator
   * 
   * @return class type
   */
  public default Class<? extends ConjugateLinkSegment> getConjugateLinkSegmentIdClass(){
    return CONJUGATE_LINK_SEGMENT_ID_CLASS;
  }   
      
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateLink getParent();
  
  /**
   * {@inheritDoc}
   */
  @Override
  public default ConjugateNode getUpstreamVertex() {
    return (ConjugateNode) ConjugateEdgeSegment.super.getUpstreamVertex();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public default ConjugateNode getDownstreamVertex() {
    return (ConjugateNode) ConjugateEdgeSegment.super.getDownstreamVertex();
  }
  

}
