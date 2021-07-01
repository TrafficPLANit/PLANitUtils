package org.planit.utils.graph;

/** Factory interface for creating vertex instances
 * 
 * @author markr
 *
 * @param <E> type of edge
 */
public interface VertexFactory<V extends Vertex> extends GraphEntityFactory<V> {

  /** Create a new vertex (without registering on this class)
   * 
   * @return created vertex
   */
  public abstract V createNew();
  
  /**
   * Create and register new entity
   *
   * @return new node created
   */
  public abstract V registerNew(); 
  
}
