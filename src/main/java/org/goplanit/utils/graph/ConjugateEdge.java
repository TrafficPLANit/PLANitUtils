package org.goplanit.utils.graph;

import org.goplanit.utils.id.ExternalIdAbleUtils;
import org.goplanit.utils.misc.Pair;

/**
 * Conjugate Edge interface connecting two conjugate vertices in a non-directional fashion.
 * 
 * @author markr
 *
 */
public interface ConjugateEdge extends Edge {
  
  /** id class for generating ids */
  public static final Class<ConjugateEdge> CONJUGATE_EDGE_ID_CLASS = ConjugateEdge.class;

  // Getters-Setters  

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateVertex getVertexA();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateVertex getVertexB();
 
  /**
   * Clone the conjugate edge as is, all shared members are shallow copied, fully owned members are deep copied
   * 
   * @return copy of this conjugate edge
   */
  @Override
  public abstract ConjugateEdge shallowClone();

  /**
   * deep Clone the conjugate edge
   *
   * @return copy of this conjugate edge
   */
  @Override
  public abstract ConjugateEdge deepClone();
     
  /**
   * Edges in original graph representing this conjugate
   * @return edges pair 
   */
  public abstract Pair<? extends Edge,? extends Edge> getOriginalAdjacentEdges();

  /**
   * populate the XMLId by either copying its internal id or using the underlying original edges XMLIds. Optionally
   * post-fix either.
   *
   * @param deriveFromOriginalEdges when true use original edges XML id, otherwise use internal id of conjugates
   * @param postFix to apply
   */
  public default void populateXmlId(boolean deriveFromOriginalEdges, String postFix){
    setXmlId(
            deriveFromOriginalEdges ?
                    ExternalIdAbleUtils.combinePairBasedXmlId(getOriginalAdjacentEdges(), "|", postFix) :
                    getId() + postFix);
  }
}
