package org.goplanit.utils.network.virtual;

import org.goplanit.utils.graph.GraphEntities;
import org.goplanit.utils.network.layer.service.ServiceNode;
import org.goplanit.utils.network.layer.service.ServiceNodes;

import java.util.function.BiConsumer;

/**
 * Container to register and manager conjugate connectoid edges.
 * 
 * @author markr
 *
 */
public interface ConjugateConnectoidEdges extends GraphEntities<ConjugateConnectoidEdge>{
  
  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract ConjugateConnectoidEdgeFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidEdges shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidEdges deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidEdges deepCloneWithMapping(BiConsumer<ConjugateConnectoidEdge, ConjugateConnectoidEdge> mapper);
}
