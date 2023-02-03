package org.goplanit.utils.graph;

/**
 * Container class for conjugate vertices and creating instances within this container via factory.
 * 
 * @author markr
 */
public interface ConjugateVertices extends GraphEntities<ConjugateVertex> {
      
  /**
   * Collect the conjugate vertex factory to use for creating instances
   * 
   * @return conjugate vertexFactory to create conjugate edges for this container
   */
  @Override
  public abstract ConjugateVertexFactory getFactory();  
  
  /**
   * shallow clone conjugate vertices
   */
  @Override
  public abstract ConjugateVertices shallowClone();

  /**
   * deep clone conjugate vertices
   */
  @Override
  public abstract ConjugateVertices deepClone();
}
