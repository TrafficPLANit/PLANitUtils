package org.goplanit.utils.graph;

import java.util.function.BiConsumer;

/**
 * Container and factory class for conjugate edges in a conjugate graph, also to be used to create and register conjugate edges of any
 * (derived) type
 * 
 * @author markr
 */
public interface ConjugateEdges extends GraphEntities<ConjugateEdge>{
  
  /**
   * Collect the conjugate edge factory to use for creating instances
   * 
   * @return conjugate edgeFactory to create conjugate edges for this container
   */
  @Override
  public abstract ConjugateEdgeFactory getFactory();
  
  /**
   * shallow clone edges
   */
  @Override
  public abstract ConjugateEdges shallowClone();

  /**
   * deep clone conjugate edges
   */
  @Override
  public abstract ConjugateEdges deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateEdges deepCloneWithMapping(BiConsumer<ConjugateEdge, ConjugateEdge> mapper);

}
