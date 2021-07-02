package org.planit.utils.network.layer.physical;

import org.planit.utils.graph.GraphEntities;

/**
 * wrapper around edges interface to support Links explicitly rather than edges
 * 
 * @author markr
 *
 * @param <L> link type
 */
public interface Links<L extends Link> extends GraphEntities<L> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract LinkFactory<L> getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Links<L> clone();

  /**
   * verify if link is present
   * 
   * @param id to check
   * @return true when present false otherwise
   */
  public default boolean hasLink(long id) {
    return contains(id);
  }

}
