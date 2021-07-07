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

  /**
   * Constructor
   * 
   * @param valueToKey the mapping from key to value of the graph entity
   */
  protected ManagedIdEntitiesImpl(Function<E, Long> valueToKey) {
    super(new TreeMap<Long, E>(), valueToKey);
  }

  /**
   * copy constructor
   * 
   * @param other to copy
   */
  protected ManagedIdEntitiesImpl(ManagedIdEntitiesImpl<E> other) {
    super(other);
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
  public void recreateIds(boolean resetManagedIdClass) {
    if(resetManagedIdClass == true) {
      IdGenerator.reset(getFactory().getIdGroupingToken(), iterator().next().getIdClass() /* e.g. Edge.class, vertex.class etc. */);
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
