package org.goplanit.utils.graph;

/**
 * Container class for vertices and creating instances within this container via factory.
 * 
 * @author markr
 */
public interface Vertices extends GraphEntities<Vertex> {
      
  /**
   * Collect the vertex factory to use for creating instances
   * 
   * @return vertexFactory to create edges for this container
   */
  @Override
  public abstract VertexFactory getFactory();  
  
  /**
   * shallow clone vertices
   */
  @Override
  public abstract Vertices shallowClone();

  /**
   * deep clone vertices
   */
  @Override
  public abstract Vertices deepClone();
}
