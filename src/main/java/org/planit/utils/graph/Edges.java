package org.planit.utils.graph;

/**
 * Container and factory class for edges in a graph, also to be used to create and register edges of any
 * (derived) type
 * 
 * @author markr
 */
public interface Edges extends GraphEntities<Edge>{       
  
  /**
   * Collect the edge factory to use for creating instances
   * 
   * @return edgeFactory to create edges for this container
   */
  @Override
  public abstract EdgeFactory getFactory();
  
  /**
   * clone edges
   */
  @Override
  public abstract Edges clone();

}
