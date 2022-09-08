package org.goplanit.utils.graph;

import org.goplanit.utils.id.ManagedIdEntities;

/** Container class for any graph entities and a factory to create them
 * 
 * @author markr
 *
 * @param <E> type of graph entity
 */
public interface ManagedGraphEntities<E extends GraphEntity> extends GraphEntities<E>, ManagedIdEntities<E>{
      
  /**
   * Force clone implementation
   * 
   * @return clone of entities
   */
  @Override  
  public abstract ManagedGraphEntities<E> clone();

}
