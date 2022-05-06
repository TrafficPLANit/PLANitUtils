package org.goplanit.utils.network.virtual;

import org.goplanit.utils.graph.GraphEntityFactory;

/** Factory interface for creating conjugate connectoid node instances
 * 
 * @author markr
 *
 */
public interface ConjugateConnectoidNodeFactory extends GraphEntityFactory<ConjugateConnectoidNode> {

  /** Create a new conjugate connectoid node (without registering)
   *
   *@param originalConnectoidEdge this node is the conjugate of
   * @return created conjugate connectoid node
   */
  public abstract ConjugateConnectoidNode createNew(final ConnectoidEdge originalConnectoidEdge);
  
  /**
   * Create and register new conjugate node. The originalConnectoidEdge may be null in which case it reflects a dummy conjugate node for original network turns where 
   * either the incoming or outgoing original link does not exist, i.e., around centroids for example
   *
   *@param originalConnectoidEdge this node is the conjugate of
   *@return new node created
   */
  public abstract ConjugateConnectoidNode registerNew(final ConnectoidEdge originalConnectoidEdge);      
  
}
