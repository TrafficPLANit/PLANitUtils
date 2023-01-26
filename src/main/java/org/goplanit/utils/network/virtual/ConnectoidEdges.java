package org.goplanit.utils.network.virtual;

import org.goplanit.utils.graph.GraphEntities;

/**
 * Container to register and manager connectoid edges.
 * 
 * @author markr
 *
 */
public interface ConnectoidEdges extends GraphEntities<ConnectoidEdge>{
  
  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract ConnectoidEdgeFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidEdges clone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidEdges deepClone();
}
