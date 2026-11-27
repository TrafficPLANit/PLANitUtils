package org.goplanit.utils.id;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.goplanit.utils.containers.EntityIndex;
import org.goplanit.utils.containers.IdentityEntityIndex;

/**
 * Base class for containers of managed id entities that look up their entities by the entities they refer to, their
 * foreign keys, such as the member nodes of an intersection or the node a connectoid sits at
 * <p>
 * A container reacting to a change elsewhere needs the entities it affects: when a node is removed, the intersection
 * it is a member of; when a link segment is removed, the movements using it. Each such container declares an index by
 * foreign key, and this class keeps every declared index current as entities are registered, replaced by id, removed,
 * removed by a condition, cleared or copied, so no container repeats that upkeep. A change of what a registered entity
 * refers to is seen when made through {@link #update(ManagedId, Consumer)}, or once the entity is reindexed after a
 * direct change. Containers without foreign keys extend {@link ManagedIdEntitiesImpl} and carry none of this.
 * </p>
 *
 * @author markr
 *
 * @param <E> type of managed id entity
 */
public abstract class ManagedIdEntitiesWithForeignKeyIndicesImpl<E extends ManagedId> extends ManagedIdEntitiesImpl<E> {

  /** the indices by foreign key the container declared, in the order declared */
  private final List<EntityIndex<?, E>> foreignKeyIndices = new ArrayList<>(1);

  /**
   * Constructor, keeping the entities in a {@link org.goplanit.utils.containers.LongKeyArrayMap}
   *
   * @param valueToKey the mapping from entity to its key
   * @param managedIdClass should reflect the class signature used for generating the managed id of this class when
   *                       creating it via the factory
   */
  protected ManagedIdEntitiesWithForeignKeyIndicesImpl(
      final Function<E, Long> valueToKey, final Class<? extends ManagedId> managedIdClass) {
    super(valueToKey, managedIdClass);
  }

  /**
   * Copy constructor. The copy's indices are declared by the copy, which files the entities it holds, see
   * {@link #addForeignKeyIndex(Function)}
   *
   * @param other to copy
   * @param deepCopy when true, create a deep copy, shallow copy otherwise
   * @param mapper to apply in case of deep copy to each original to copy combination (when provided, may be null)
   */
  protected ManagedIdEntitiesWithForeignKeyIndicesImpl(
      final ManagedIdEntitiesWithForeignKeyIndicesImpl<E> other, boolean deepCopy, BiConsumer<E, E> mapper) {
    super(other, deepCopy, mapper);
  }

  /**
   * Declare an index of the entities by what they refer to, kept current by this container from then on. The entities
   * already held are filed at once, so a container declaring its indices as fields also has them filled in its copies
   *
   * @param <K> type of foreign key
   * @param keysOf collects the foreign keys an entity refers to at this moment, may contain nulls and repeats
   * @return the index, for the container's lookups
   */
  protected final <K> EntityIndex<K, E> addForeignKeyIndex(
      Function<? super E, ? extends Collection<? extends K>> keysOf) {
    var index = new IdentityEntityIndex<K, E>(keysOf);
    index.reindex(this);
    foreignKeyIndices.add(index);
    return index;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public E register(final E entity) {
    var previous = super.register(entity);
    foreignKeyIndices.forEach(index -> index.replace(previous, entity));
    return previous;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public E remove(final E entity) {
    var removed = super.remove(entity);
    foreignKeyIndices.forEach(index -> index.unindex(removed));
    return removed;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public E remove(final long key) {
    var removed = super.remove(key);
    foreignKeyIndices.forEach(index -> index.unindex(removed));
    return removed;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeIf(Predicate<E> condition) {
    stream().filter(condition).collect(Collectors.toList()).forEach(this::remove);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void clear() {
    super.clear();
    foreignKeyIndices.forEach(EntityIndex::clear);
  }

  /**
   * Apply a change to what a registered entity refers to, keeping every index in step with what it refers to before and
   * after
   *
   * @param <T> the kind of entity, so the change can use what is particular to it
   * @param entity to change
   * @param change to apply to it
   */
  public <T extends E> void update(T entity, Consumer<? super T> change) {
    change.accept(entity);
    reindex(entity);
  }

  /**
   * File a registered entity under what it refers to now, in every index, e.g. after a direct change of what it refers
   * to; nothing happens for an entity this container does not hold
   *
   * @param entity to reindex
   */
  public void reindex(E entity) {
    if (get(getKeyByValue(entity)) != entity) {
      return;
    }
    foreignKeyIndices.forEach(index -> index.index(entity));
  }

  /**
   * Rebuild every index from what each registered entity refers to now, e.g. after direct changes of many entities
   */
  public void reindex() {
    foreignKeyIndices.forEach(index -> index.reindex(this));
  }
}
