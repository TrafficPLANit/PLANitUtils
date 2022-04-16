package org.goplanit.utils.graph;

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
   * clone edges
   */
  @Override
  public abstract ConjugateEdges clone();

}
