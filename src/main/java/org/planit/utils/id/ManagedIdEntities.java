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
  public abstract ManagedIdEntityFactory<E> getFactory();
  
  /**
   * Collect the class identifier used for the managed ids within the id group for instances of this class used in this container
   * 
   * @return managedIdClass for instances this factory creates
   */
  public abstract Class<? extends ManagedId> getManagedIdClass();  
      
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
  public default boolean containsKey(long id) {
    return get(id) != null;
  }
  
  /**
   * Identical to {@link #recreateIds(true)} 
   */
  public default void recreateIds() {
    recreateIds(true);
  }  
  
  /**
   * When reset it called, it not only clears the entries, but also resets the managedids, such that when the container is reused
   * the managed ids start from zero again. If any entries are
   * managedEntities themselves or contain managed entities themselves, they are reset as well
   */
  public default void reset() {
    for(E entry : this) {
      entry.resetChildManagedIdEntities();
    }
    clear();
    IdGenerator.reset(getFactory().getIdGroupingToken(), getManagedIdClass());     
  }
  
}
