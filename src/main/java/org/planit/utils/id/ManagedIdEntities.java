package org.planit.utils.id;

import org.planit.utils.wrapper.LongMapWrapper;

/** Container class for any managed id derived entities and a factory to create them
 * 
 * @author markr
 *
 * @param <E> type of managed id entity
 */
public interface ManagedIdEntities<E extends ManagedId> extends LongMapWrapper<E>, Cloneable {

  /** Factory to create instance of managed id entity (for this container class)
   * 
   * @return entity factory
   */
  public abstract ContainerisedManagedIdEntityFactory<E> getFactory();
      
  /**
   * Recreate the ids for all registered entities with or without resetting 
   * 
   * @param resetManagedIdClass when true we reset the managedId's counter to zero (via its id class) before recreating the ids, otherwise we simply recreate the managed id by 
   * starting with the next available id without resetting
   */
  public abstract void recreateIds(boolean resetManagedIdClass);  
    
  /**
   * Force clone implementation
   * 
   * @return clone of entities
   */
  public abstract ManagedIdEntities<E> clone();  
    
  /** Verify if present
   * 
   * @param id to verify
   * @return true when present false otherwise
   */
  public default boolean contains(long id) {
    return get(id) != null;
  }
  
  /**
   * Identical to {@link #recreateIds(true)} 
   */
  public default void recreateIds() {
    recreateIds(true);
  }  

}