package org.goplanit.utils.graph;

import org.goplanit.utils.id.IdGenerator;
import org.goplanit.utils.id.ManagedId;
import org.goplanit.utils.id.ManagedIdEntities;
import org.goplanit.utils.id.ManagedIdEntitiesImpl;
import org.goplanit.utils.wrapper.LongMapWrapperImpl;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Base class for containers of managed id entities that are also graph entities
 * 
 * @author markr
 *
 * @param <E> type of graph entity and managed id entity
 */
public abstract class ManagedGraphEntitiesImpl<E extends GraphEntity & ManagedId> extends ManagedIdEntitiesImpl<E> implements ManagedGraphEntities<E> {

  /**
   * Constructor
   *
   * @param valueToKey the mapping from key to value of the graph entity
   * @param managedIdClass should reflect the class signature used for generating the managed id of this class when creating it via the factory
   * of this container
   */
  protected ManagedGraphEntitiesImpl(final Function<E, Long> valueToKey, final Class<? extends ManagedId> managedIdClass) {
    super(valueToKey, managedIdClass);
  }

  /**
   * Constructor. while not recommended it is allowed to create managed ids that do not rely on id generation of the class itself. It can be that they rely on child
   * ids or synced ids of other internal referenced classes. In that case this constructor can be used directly. this however should generally be avoided.
   *
   * @param valueToKey the mapping from key to value of the graph entity
   */
  protected ManagedGraphEntitiesImpl(final Function<E, Long> valueToKey) {
    super(valueToKey);
  }

  /**
   * copy constructor
   *
   * @param other to copy
   * @param deepCopy when true, create a deep copy, shallow copy otherwise
   * @param mapper to apply in case of deep copy to each original to copy combination (when provided, may be null)
   */
  protected ManagedGraphEntitiesImpl(ManagedGraphEntitiesImpl<E> other, boolean deepCopy, BiConsumer<E, E> mapper) {
    super(other, deepCopy, mapper);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ManagedGraphEntitiesImpl<E> shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ManagedGraphEntitiesImpl<E> deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ManagedGraphEntitiesImpl<E> deepCloneWithMapping(BiConsumer<E,E> mapper);

  /**
   * {@inheritDoc}
   *
   * Note: not an efficient implementation since it loops over all entities in linear time to identify the correct one,
   * preferably use {@link #get(Object)} instead whenever possible.
   */
  @Override
  public E getByXmlId(String xmlId)
  {
    return GraphEntities.getByXmlId(this, xmlId);
  }
  
}
