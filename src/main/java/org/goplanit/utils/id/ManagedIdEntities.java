package org.goplanit.utils.id;

import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegmentType;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegmentTypes;
import org.goplanit.utils.service.routed.RoutedTripSchedule;
import org.goplanit.utils.wrapper.LongMapWrapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Container class for any managed id derived entities and a factory to create them
 * 
 * @author markr
 *
 * @param <E> type of managed id entity
 */
public interface ManagedIdEntities<E extends ManagedId> extends LongMapWrapper<E> {

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
   * Recreate the ids for all registered entities with or without resetting, this includes child managed ids, i.e., nested magedidentities containers if so indicated
   * 
   * @param resetManagedIdClass when true we reset the managedId's counter to zero (via its id class) before recreating the ids, otherwise we simply recreate the managed id by
   *                            starting with the next available id without resetting
   */
  public abstract void recreateIds(boolean resetManagedIdClass);
    
  /**
   * Shallow clone implementation
   * 
   * @return clone of entities
   */
  public abstract ManagedIdEntities<E> shallowClone();

  /**
   * Deep clone implementation
   *
   * @return deep copy of entities
   */
  public abstract ManagedIdEntities<E> deepClone();

  /**
   * Deep clone implementation where the mapping for its internal copies is captured by the provided mapper
   *
   * @param mapper to apply to each mapping between original and copy
   */
  public abstract ManagedIdEntities<E> deepCloneWithMapping(BiConsumer<E, E> mapper);
    
  /** Verify if present
   * 
   * @param id to verify
   * @return true when present false otherwise
   */
  public default boolean containsKey(long id) {
    return get(id) != null;
  }
  
  /**
   * Identical to {@link #recreateIds(boolean)}
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

  /**
   * Convenience method to perform group by based on uncerlying streaming mechanisms
   *
   * @param classifier for group by
   * @return map with key containing the classifier and value being a list of matched entities in the container
   * @param <K> classifier to apply
   */
  public default <K> Map<K, List<E>> groupBy(Function<? super E, ? extends K> classifier){
    return this.stream().collect(Collectors.groupingBy(classifier));
  }

}
