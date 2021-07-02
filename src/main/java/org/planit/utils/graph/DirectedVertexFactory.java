package org.planit.utils.graph;

/** Factory interface for creating vertex instances
 * 
 * @author markr
 *
 */
public interface DirectedVertexFactory extends GraphEntityFactory<DirectedVertex> {

  /** Create a new directed vertex (without registering on this class)
   * 
   * @return created vertex
   */
  public abstract DirectedVertex createNew();
  
  /**
   * Create and register new directed vertex
   *
   * @return new node created
   */
  public abstract DirectedVertex registerNew(); 
  
}
