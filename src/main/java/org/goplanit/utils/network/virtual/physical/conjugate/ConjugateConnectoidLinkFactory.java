package org.goplanit.utils.network.virtual.physical.conjugate;

import org.goplanit.utils.graph.GraphEntityFactory;
import org.goplanit.utils.network.virtual.physical.ConnectoidLink;

/** Factory interface for connectoid conjugate links
 * 
 * @author markr
 *
 */
public interface ConjugateConnectoidLinkFactory extends GraphEntityFactory<ConjugateConnectoidLink>{

  /**
   * Register a new conjugate connectoid edge
   *
   * @param vertexA to use
   * @param vertexB to use
   * @param registerOnNodes when true register edge on node
   * @param originalConnectoidLink of the original network this conjugate represents (only partly because otheroriginal
   *                               edge is dummy and therefore null)
   * @return created conjugate edge
   */
  public abstract ConjugateConnectoidLink registerNew(
      ConjugateConnectoidNode vertexA, ConjugateConnectoidNode vertexB, boolean registerOnNodes, ConnectoidLink originalConnectoidLink);
}
