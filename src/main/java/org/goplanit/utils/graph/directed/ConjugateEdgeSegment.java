package org.goplanit.utils.graph.directed;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.misc.Pair;

/**
 * Conjugate version of edge segment representing connection between two edge segments on origin network
 * 
 * @author markr
 *
 */
public interface ConjugateEdgeSegment extends EdgeSegment{

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
  public abstract ConjugateEdgeSegment shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateEdgeSegment deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public default ConjugateEdgeSegment getOppositeDirectionSegment() {
    return (ConjugateEdgeSegment) EdgeSegment.super.getOppositeDirectionSegment();  
  }

  // NEW and specific to conjugate aspect of class

  /**
   * Adjacent edge segments in original graph for this conjugate
   *
   * @return edge segment pair
   */
  public default Pair<? extends EdgeSegment,? extends EdgeSegment> getOriginalAdjacentEdgeSegments(){
    return getParent().getOriginalAdjacentEdgeSegments(isDirectionAb());
  }

  /**
   * Access to the underlying original centre vertex for the two adjacent edge segments that underpin this conjugate
   * edge segment
   *
   * @return original centre (middle) vertex
   */
  public default DirectedVertex getOriginalCentreVertex(){
    if(getOriginalAdjacentEdgeSegments().first() != null){
      return getOriginalAdjacentEdgeSegments().first().getDownstreamVertex();
    }else if(getOriginalAdjacentEdgeSegments().second() != null){
      return getOriginalAdjacentEdgeSegments().second().getUpstreamVertex();
    }else{
      throw new PlanItRunTimeException(
              "At least one original edge segment expected to be present on conjugate segment (%s), but both are null",
              getIdsAsString());
    }
  }
}
