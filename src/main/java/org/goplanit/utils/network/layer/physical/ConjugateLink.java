package org.goplanit.utils.network.layer.physical;

import java.util.Collection;

import org.goplanit.utils.graph.directed.ConjugateDirectedEdge;

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
   * Return class used to generate unique link ids via the id generator
   * 
   * @return class type
   */
  public default Class<? extends ConjugateLink> getLinkIdClass(){
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
  
  /** collect edgeSegment Ab as something extending LinkSegment which is to be expected for any link. Convenience method
   * for readability
   * 
   * @param <LS> link segment type
   * @return link segment in given direction
   */
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
}
