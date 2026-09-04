package org.goplanit.utils.network.virtual.graph.conjugate;

import org.goplanit.utils.graph.ManagedGraphEntities;

import java.util.function.BiConsumer;

/**
 * Container to register and manager conjugate connectoid edges.
 * 
 * @author markr
 *
 */
public interface ConjugateConnectoidEdges extends ManagedGraphEntities<ConjugateConnectoidDirectedEdge> {
  
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
  public abstract ConjugateConnectoidEdges deepCloneWithMapping(
          BiConsumer<ConjugateConnectoidDirectedEdge, ConjugateConnectoidDirectedEdge> mapper);
}
