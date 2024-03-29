package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.ManagedGraphEntities;

import java.util.function.BiConsumer;

/**
 *Primary managed container class for links with access to factory capable of creating new links and registering them on the container
 * directly
 * 
 * @author markr
 *
 */
public interface Links<L extends Link> extends ManagedGraphEntities<L> {
  /* do not derive from DirectedEdges<E> since we require to override the factory method return type. This is only
   * allowed when the return type directly derives from the original return type. LinkFactory cannot
   * derive from DirectedEdgeFactory since the signature of the factory methods differs. Hence, we must derive from
   * the base interface instead which has an empty dummy factory return type which one can always overwrite and
   * the LinkFactory is derived from */
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract LinkFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Links shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Links deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Links deepCloneWithMapping(BiConsumer<L, L> mapper);

  /**
   * verify if link is present
   * 
   * @param id to check
   * @return true when present false otherwise
   */
  public default boolean hasLink(long id) {
    return containsKey(id);
  }

}
