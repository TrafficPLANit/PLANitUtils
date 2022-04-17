package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.GraphEntityFactory;

/** Factory interface for creating conjugate node instances
 * 
 * @author markr
 *
 */
public interface ConjugateNodeFactory extends GraphEntityFactory<ConjugateNode> {

  /** Create a new conjugate node (without registering)
   *
   *@param originalLink this node is the conjugate of
   * @return created conjugate node
   */
  public abstract ConjugateNode createNew(final Link originalLink);
  
  /**
   * Create and register new conjugate node
   *
   *@param originalLink this node is the conjugate of
   * @return new node created
   */
  public abstract ConjugateNode registerNew(final Link originalLink);      
  
}
