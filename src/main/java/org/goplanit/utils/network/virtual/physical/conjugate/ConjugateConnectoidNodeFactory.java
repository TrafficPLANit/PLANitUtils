package org.goplanit.utils.network.virtual.physical.conjugate;

import org.goplanit.utils.graph.GraphEntityFactory;
import org.goplanit.utils.network.virtual.graph.ConnectoidDirectedEdge;
import org.goplanit.utils.network.virtual.physical.ConnectoidSegment;

/** Factory interface for creating conjugate connectoid node instances
 * 
 * @author markr
 *
 */
public interface ConjugateConnectoidNodeFactory extends GraphEntityFactory<ConjugateConnectoidNode> {

  /** Create a new conjugate connectoid node (without registering)
   *
   *@param original this node is the conjugate of
   * @return created conjugate connectoid node
   */
  public abstract ConjugateConnectoidNode createNew(final ConnectoidSegment original);
  
  /**
   * Create and register new conjugate node. The originalConnectoidEdge may be null in which case it reflects a dummy conjugate node for original network turns where 
   * either the incoming or outgoing original link does not exist, i.e., around centroids for example
   *
   *@param original this node is the conjugate of
   * @param deriveFromOriginal when true use original XML id, otherwise use internal id of conjugates
   * @param xmlIdPostFix to apply
   *@return new node created
   */
  public abstract ConjugateConnectoidNode registerNew(
          final ConnectoidSegment original, boolean deriveFromOriginal, String xmlIdPostFix);
  
}
