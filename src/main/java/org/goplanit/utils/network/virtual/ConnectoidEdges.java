package org.goplanit.utils.network.virtual;

import org.goplanit.utils.graph.GraphEntities;
import org.goplanit.utils.graph.ManagedGraphEntities;

import java.util.function.BiConsumer;

/**
 * Container to register and manager connectoid edges.
 * 
 * @author markr
 *
 */
public interface ConnectoidEdges extends ManagedGraphEntities<ConnectoidEdge> {
  
  /**
   * {@inheritDoc}
   */  
  @Override
  public abstract ConnectoidEdgeFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidEdges shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidEdges deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidEdges deepCloneWithMapping(BiConsumer<ConnectoidEdge, ConnectoidEdge> mapper);
}
