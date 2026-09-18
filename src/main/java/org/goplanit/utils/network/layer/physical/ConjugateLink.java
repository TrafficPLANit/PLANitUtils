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
public interface ConjugateLink extends ConjugateDirectedEdge, Link {
  
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

  /**
   *  obtain conjugate node
   * @return node
   * @param <N> type
   */
  @SuppressWarnings("unchecked")
  public default <N extends ConjugateNode> N getConjugateNodeA() {
    return (N) getVertexA();
  }

  /**
   *  obtain conjugate node
   * @return node
   * @param <N> type
   */
  @SuppressWarnings("unchecked")
  public default <N extends ConjugateNode> N getConjugateNodeB() {
    return (N) getVertexB();
  }

  /**
   *  obtain conjugate link segment in direction
   * @param directionAb the direction
   * @return segment
   * @param <LS> type
   */
  @SuppressWarnings("unchecked")
  public default <LS extends ConjugateLinkSegment> LS getConjugateLinkSegment(boolean directionAb) {
    return (LS) getEdgeSegment(directionAb);
  }

  /**
   *  obtain conjugate link segment in direction AB
   * @return segment
   * @param <LS> type
   */
  public default <LS extends ConjugateLinkSegment> LS getConjugateLinkSegmentAb() {
    return getConjugateLinkSegment(true);
  }

  /**
   *  check if conjugate link segment in direction AB exists
   * @return exists
   */
  public default boolean hasConjugateLinkSegmentAb() {
    return hasEdgeSegmentAb();
  }

  /**
   *  obtain conjugate link segment in direction BA
   * @return segment
   * @param <LS> type
   */
  public default <LS extends ConjugateLinkSegment> LS getConjugateLinkSegmentBa() {
    return getConjugateLinkSegment(false);
  }

  /**
   *  check if conjugate link segment in direction BA exists
   * @return exists
   */
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
  public abstract ConjugateLink shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateLink deepClone();
}
