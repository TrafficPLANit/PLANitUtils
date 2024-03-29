package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.ManagedGraphEntities;

import java.util.function.BiConsumer;

/**
 * Container class for primary managed nodes container and access to factory to create and register them on this instance
 * 
 * @author markr
 *
 */
public interface Nodes extends ManagedGraphEntities<Node> {
  /* do not derive from DirectedVertices<E> since we require to override the factory method return type. This is only
   * allowed when the return type directly derives from the original return type. NodeFactory cannot
   * derive from DirectedVertexFactory since the signature of the factory methods differs. Hence, we must derive from
   * the base interface instead which has an empty dummy factory return type which one can always overwrite and
   * the NodeFactory is derived from */
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract NodeFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Nodes shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Nodes deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Nodes deepCloneWithMapping(BiConsumer<Node, Node> mapper);
}
