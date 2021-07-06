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
   * recreate the ids for all registered entities with or without resetting 
   * 
   * @param reset when true we rest the counter to zero, otherwise we recreate ids without resetting
   */
  public abstract void recreateIds(boolean reset);  
    
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

}
