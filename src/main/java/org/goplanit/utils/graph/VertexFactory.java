package org.goplanit.utils.graph;

/** Factory interface for creating vertex instances
 * 
 * @author markr
 *
 */
public interface VertexFactory extends GraphEntityFactory<Vertex> {

  /** Create a new vertex (without registering on this class)
   * 
   * @return created vertex
   */
  public abstract Vertex createNew();
  
  /**
   * Create and register new entity
   *
   * @return new node created
   */
  public abstract Vertex registerNew(); 
  
}
