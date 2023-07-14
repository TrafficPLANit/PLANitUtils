package org.goplanit.utils.graph;

import java.util.Collection;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Conjugate vertex representation connected to one or more conjugate edges
 * 
 * @author markr
 *
 */
public interface ConjugateVertex extends Vertex {
  
  /** vertex logger */
  public static final Logger LOGGER = Logger.getLogger(ConjugateVertex.class.getCanonicalName());
  
  /** id class for generating ids */
  public static final Class<ConjugateVertex> CONJUGATE_VERTEX_ID_CLASS = ConjugateVertex.class;  

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Collection<? extends ConjugateEdge> getEdges();  
    
  /**
   * Shallow Clone the conjugate vertex
   * @return the cloned vertex
   */
  @Override
  public abstract ConjugateVertex shallowClone();

  /**
   * Deep Clone the conjugate vertex
   * @return the cloned vertex
   */
  @Override
  public abstract ConjugateVertex deepClone();
  
  /**
   * All vertices use the CONJUGATE_VERTEX_ID_CLASS to generate the unique internal ids
   */
  @Override
  public default Class<? extends ConjugateVertex> getIdClass() {
    return CONJUGATE_VERTEX_ID_CLASS;
  }  
 
  
  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public default Set<? extends ConjugateEdge> getEdges(Vertex otherVertex) {
    return (Set<? extends ConjugateEdge>) Vertex.super.getEdges(otherVertex);
  }  
  
  /**
   * Collect the original edge this conjugate vertex represents in the conjugate graph
   * @return original edge
   */
  public abstract Edge getOriginalEdge();
}
