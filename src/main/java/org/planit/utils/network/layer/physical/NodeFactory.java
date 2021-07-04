package org.planit.utils.network.layer.physical;

import org.planit.utils.graph.GraphEntityFactory;

/** Factory interface for creating node instances
 * 
 * @author markr
 *
 */
public interface NodeFactory extends GraphEntityFactory<Node> {

  /** Create a new node (without registering)
   * 
   * @return created vertex
   */
  public abstract Node createNew();
  
  /**
   * Create and register new node
   *
   * @return new node created
   */
  public abstract Node registerNew(); 
  
}
