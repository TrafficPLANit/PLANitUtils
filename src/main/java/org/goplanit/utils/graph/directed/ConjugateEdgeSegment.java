package org.goplanit.utils.graph.directed;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.graph.ConjugateEdge;
import org.goplanit.utils.id.ExternalIdAbleUtils;
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
   * check if original entry segment is not null
   *
   * @return true when present, false otherwise
   */
  public default boolean hasOriginalEntryEdgeSegment(){
    return getParent().getOriginalAdjacentEdgeSegments(isDirectionAb()).firstNotNull();
  }

  /**
   * check if original exit segment is not null
   *
   * @return true when present, false otherwise
   */
  public default boolean hasOriginalExitEdgeSegment(){
    return getParent().getOriginalAdjacentEdgeSegments(isDirectionAb()).secondNotNull();
  }

  /**
   * Adjacent edge segments (entry/exit) in original graph for this conjugate
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

  /**
   * Get string rperesentation of original segments underpinning this conjugate edge segment
   * @return from: (_from_ids_as_string_) to: (_to_ids_as_string_)
   */
  public default String getOriginalAdjacentEdgeSegmentsIdsAsString(){
    if(!hasOriginalEntryEdgeSegment()){
      return "no original ids available";
    }
    StringBuilder sb = new StringBuilder("from: (");
    if(getOriginalAdjacentEdgeSegments().first()!=null){
      sb.append(getOriginalAdjacentEdgeSegments().first().getIdsAsString());
    }else{
      sb.append("-");
    }

    sb.append(") to: (");
    if(getOriginalAdjacentEdgeSegments().second()!=null){
      sb.append(getOriginalAdjacentEdgeSegments().second().getIdsAsString());
    }else{
      sb.append("-");
    }
    sb.append(")");
    return sb.toString();
  }

  /**
   * populate the XMLId by either copying its internal id or using the underlying original edge segment XMLIds.
   * Optionally post-fix as well.
   *
   * @param deriveFromOriginalEdgeSegments when true use original edge segment XML ids, otherwise use internal
   *                                       id of conjugates
   * @param postFix to apply
   */
  public default void populateXmlId(boolean deriveFromOriginalEdgeSegments, String postFix){
    setXmlId(
            deriveFromOriginalEdgeSegments ?
                    ExternalIdAbleUtils.combinePairBasedXmlId(getOriginalAdjacentEdgeSegments(), postFix) :
                    getId() + postFix);
  }

}
