package org.planit.utils.id;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import org.planit.utils.id.IdGenerator;
import org.planit.utils.wrapper.LongMapWrapperImpl;

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
    super(new TreeMap<Long, E>(), valueToKey);
    this.managedIdClass = managedIdClass;
  }

  /**
   * copy constructor
   * 
   * @param other to copy
   */
  protected ManagedIdEntitiesImpl(ManagedIdEntitiesImpl<E> other) {
    super(other);
    this.managedIdClass = other.managedIdClass;
  }

  /**
   * updates the container keys based on currently presiding ids. Only to be used when an external force has changed already registered edges' their ids
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
    if(resetManagedIdClass == true) {
      IdGenerator.reset(getFactory().getIdGroupingToken(), getManagedIdClass() /* e.g. Edge.class, vertex.class etc. */);
    }
    
    if (!isEmpty()) {
      /* remove gaps by simply resetting and recreating all entity ids */            
      forEach(entity -> entity.recreateManagedIds(getFactory().getIdGroupingToken()));
      updateIdMapping();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ManagedIdEntitiesImpl<E> clone();

}
