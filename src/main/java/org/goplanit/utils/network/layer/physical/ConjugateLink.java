package org.goplanit.utils.network.layer.physical;

import java.util.Collection;

import org.goplanit.utils.graph.directed.ConjugateDirectedEdge;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;

/**
 * Conjugate link representing two adjacent directed edges in original network and connecting two conjugated nodes 
 * 
 * @author markr
 *
 */
public interface ConjugateLink extends ConjugateDirectedEdge {
  
  /** id class for generating ids */
  public static final Class<ConjugateLink> CONJUGATE_LINK_ID_CLASS = ConjugateLink.class;     
    
  /**
   * Return class used to generate unique conjugate link ids via the id generator
   * 
   * @return class type
   */
  public default Class<? extends ConjugateLink> getConjugateLinkIdClass(){
    return CONJUGATE_LINK_ID_CLASS;
  }
  
  @SuppressWarnings("unchecked")
  public default <N extends ConjugateNode> N getConjugateNodeA() {
    return (N) getVertexA();
  }
  
  @SuppressWarnings("unchecked")
  public default <N extends ConjugateNode> N getConjugateNodeB() {
    return (N) getVertexB();
  }  
  
  @SuppressWarnings("unchecked")
  public default <LS extends ConjugateLinkSegment> LS getConjugateLinkSegment(boolean directionAb) {
    return (LS) getEdgeSegment(directionAb);
  }   
  
  public default <LS extends ConjugateLinkSegment> LS getConjugateLinkSegmentAb() {
    return getConjugateLinkSegment(true);
  }   
  
  public default boolean hasConjugateLinkSegmentAb() {
    return hasEdgeSegmentAb();
  }   
  
  public default <LS extends ConjugateLinkSegment> LS getLinkSegmentBa() {
    return getConjugateLinkSegment(false);
  } 
  
  public default boolean hasConjugateLinkSegmentBa() {
    return hasEdgeSegmentBa();
  }

  @SuppressWarnings("unchecked")
  public default <LS extends ConjugateLinkSegment> Collection<LS> getConjugateLinkSegments(){
    return (Collection<LS>) getEdgeSegments();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateLink clone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateLink deepClone();
}
