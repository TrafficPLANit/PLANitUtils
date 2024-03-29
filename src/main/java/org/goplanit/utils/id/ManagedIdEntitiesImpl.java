package org.goplanit.utils.id;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.goplanit.utils.id.IdGenerator;
import org.goplanit.utils.time.TimePeriod;
import org.goplanit.utils.wrapper.LongMapWrapperImpl;

/**
 * Base class for containers of managed id entities
 * 
 * @author markr
 *
 * @param <E> type of managed id entity
 */
public abstract class ManagedIdEntitiesImpl<E extends ManagedId> extends LongMapWrapperImpl<E> implements ManagedIdEntities<E> {
  
  /** the class signature used for generating the managed id within the group defined by the token */
  protected final Class<? extends ManagedId> managedIdClass;
  
  /**
   * Constructor
   * 
   * @param valueToKey the mapping from key to value of the graph entity
   * @param managedIdClass should reflect the class signature used for generating the managed id of this class when creating it via the factory
   * of this container
   */
  protected ManagedIdEntitiesImpl(final Function<E, Long> valueToKey, final Class<? extends ManagedId> managedIdClass) {
    super(new TreeMap<>(), valueToKey);
    this.managedIdClass = managedIdClass;
  }
  
  /**
   * Constructor. while not recommended it is allowed to create managed ids that do not rely on id generation of the class itself. It can be that they rely on child
   * ids or synced ids of other internal referenced classes. In that case this constructor can be used directly. this however should generally be avoided.
   * 
   * @param valueToKey the mapping from key to value of the graph entity
   */
  protected ManagedIdEntitiesImpl(final Function<E, Long> valueToKey) {
    super(new TreeMap<>(), valueToKey);
    this.managedIdClass = null;
  }  

  /**
   * copy constructor
   * 
   * @param other to copy
   * @param deepCopy when true, create a deep copy, shallow copy otherwise
   * @param mapper to apply in case of deep copy to each original to copy combination (when provided, may be null)
   */
  protected ManagedIdEntitiesImpl(ManagedIdEntitiesImpl<E> other, boolean deepCopy, BiConsumer<E, E> mapper) {
    super(other);
    this.managedIdClass = other.managedIdClass;

    // super already did a shallow copy, so only needed in case of deep copy
    if(deepCopy){
      clear();
      other.forEach(v ->
      {
        var copy = (E) v.deepClone();
        this.register(copy);
        if(mapper!= null) mapper.accept(v, copy);
      });
    }
  }

  /**
   * updates the container keys based on currently presiding ids. Only to be used when an external force has changed already registered entity their ids
   */
  protected void updateIdMapping() {
    /* redo mapping */
    Map<Long, E> updatedMap = createEmptyInstance(getMap());
    getMap().forEach((oldId, entity) -> updatedMap.put(getValueToKey().apply(entity), entity));
    getMap().clear();
    setMap(updatedMap);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<? extends ManagedId> getManagedIdClass() {
    return managedIdClass;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void recreateIds(boolean resetManagedIdClass) {
    if(resetManagedIdClass == true && managedIdClass!=null) {
      IdGenerator.reset(getFactory().getIdGroupingToken(), getManagedIdClass() /* e.g. managed id class */);
    }
    
    if (!isEmpty()) {
      /* remove gaps by simply resetting and recreating all entity ids */            
      for(var entity : this){
        entity.recreateManagedIds(getFactory().getIdGroupingToken());
      }
      updateIdMapping();
    }
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void reset() {
    for(E entry : this) {
      entry.resetChildManagedIdEntities();
    }
    clear();
    
    if(managedIdClass != null) {
      IdGenerator.reset(getFactory().getIdGroupingToken(), getManagedIdClass());
    }
  }  

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ManagedIdEntitiesImpl<E> shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ManagedIdEntitiesImpl<E> deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ManagedIdEntitiesImpl<E> deepCloneWithMapping(BiConsumer<E,E> mapper);
  
}
