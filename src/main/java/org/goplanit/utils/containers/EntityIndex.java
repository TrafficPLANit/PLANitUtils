package org.goplanit.utils.containers;

import java.util.List;
import java.util.function.Consumer;

/**
 * Index of entities by the keys they refer to, e.g. the link segments a banned movement starts or ends at, so the
 * entities referring to a key are found without going over all of them. An entity is indexed under the keys it refers
 * to at the moment it is indexed; a change to what it refers to is only seen when made through
 * {@link #update(Object, Consumer)}
 *
 * @param <K> type of key
 * @param <E> type of entity
 * @author markr
 */
public interface EntityIndex<K, E> {

  /**
   * Index the entity under every key it refers to at this moment, null keys excepted
   *
   * @param entity to index
   */
  public abstract void index(E entity);

  /**
   * Remove the entity from under every key it refers to at this moment; nothing happens for null
   *
   * @param entity to unindex, may be null
   */
  public abstract void unindex(E entity);

  /**
   * Replace one indexed entity by another, e.g. when registering replaces the entity with the same id; nothing happens
   * when both are the same
   *
   * @param previous entity to unindex, may be null
   * @param current entity to index
   */
  public abstract void replace(E previous, E current);

  /**
   * Apply a change to what an indexed entity refers to, keeping the index in step with what it refers to before and
   * after
   *
   * @param entity to change
   * @param change to apply to it
   */
  public abstract void update(E entity, Consumer<? super E> change);

  /**
   * Rebuild the index from the given entities, in their order
   *
   * @param entities to index
   */
  public abstract void reindex(Iterable<? extends E> entities);

  /**
   * Remove every entity from the index
   */
  public abstract void clear();

  /**
   * Collect the entities referring to the key
   *
   * @param key to look up
   * @return entities in the order they were indexed, empty when none
   */
  public abstract List<E> get(K key);

  /**
   * Collect the first entity indexed under the key
   *
   * @param key to look up
   * @return first entity indexed, null when none
   */
  public abstract E getFirst(K key);
}
