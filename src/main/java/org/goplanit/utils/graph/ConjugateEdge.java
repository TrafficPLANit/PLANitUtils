package org.goplanit.utils.graph;

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
   * Clone the edge as is, all shared members are shallow copied, fully owned members are deep copied
   * 
   * @return copy of this edge
   */
  public abstract ConjugateEdge clone();   
     
  /**
   * Edges in original graph representing this conjugate
   * @return edges pair 
   */
  public abstract Pair<Edge,Edge> getOriginalEdges();
    
}
