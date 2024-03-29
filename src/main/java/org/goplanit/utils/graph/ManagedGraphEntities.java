package org.goplanit.utils.graph;

import org.goplanit.utils.id.ManagedId;
import org.goplanit.utils.id.ManagedIdEntities;
import org.goplanit.utils.network.virtual.ConnectoidSegment;
import org.goplanit.utils.network.virtual.ConnectoidSegments;

import java.util.function.BiConsumer;

/** Container class for any graph entities and a factory to create them
 * 
 * @author markr
 *
 * @param <E> type of graph entity
 */
public interface ManagedGraphEntities<E extends GraphEntity & ManagedId> extends GraphEntities<E>, ManagedIdEntities<E>{
      
  /**
   * shallow clone implementation
   * 
   * @return clone of entities
   */
  @Override  
  public abstract ManagedGraphEntities<E> shallowClone();

  /**
   * Force clone implementation
   *
   * @return clone of entities
   */
  @Override
  public abstract ManagedGraphEntities<E> deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ManagedGraphEntities<E> deepCloneWithMapping(BiConsumer<E, E> mapper);



}
