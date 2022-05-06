package org.goplanit.utils.network.virtual;

import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.graph.GraphEntityFactory;
import org.goplanit.utils.graph.directed.ConjugateDirectedVertex;

/** Factory interface for connectoid conjugate edges
 * 
 * @author markr
 *
 */
public interface ConjugateConnectoidEdgeFactory extends GraphEntityFactory<ConjugateConnectoidEdge>{

  /**
   * Register a new conjugate connectoid edge
   *
   * @param vertexA to use
   * @param vertexB to use
   * @param registerOnNodes when true register edge on node
   * @param originalConnectoidEdge of the original network this conjugate represents (only partly becaus eothe roriginal edge is dummy and therefore null)
   * @return created conjugate edge
   */
  public abstract ConjugateConnectoidEdge registerNew(ConjugateConnectoidNode vertexA, ConjugateConnectoidNode vertexB, boolean registerOnNodes, ConnectoidEdge originalConnectoidEdge);
}
