package org.goplanit.utils.network.virtual.graph;

import org.goplanit.utils.graph.ManagedGraphEntities;

import java.util.function.BiConsumer;

/**
 * Container to register and manager connectoid edges.
 * 
 * @author markr
 *
 */
public interface ConnectoidEdges extends ManagedGraphEntities<ConnectoidDirectedEdge> {
  
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
  public abstract ConnectoidEdges deepCloneWithMapping(BiConsumer<ConnectoidDirectedEdge, ConnectoidDirectedEdge> mapper);
}
