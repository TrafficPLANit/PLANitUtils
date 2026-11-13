package org.goplanit.utils.containers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Index of entities by the keys they refer to, with keys and entities compared by identity, so it stays valid when ids
 * are recreated
 *
 * @param <K> type of key
 * @param <E> type of entity
 * @author markr
 */
public class IdentityEntityIndex<K, E> implements EntityIndex<K, E> {

  /** collects the keys an entity refers to at this moment, may contain nulls and repeats */
  private final Function<? super E, ? extends Collection<? extends K>> keysOf;

  /** key to the entities indexed under it, in the order indexed */
  private final Map<K, List<E>> entitiesByKey = new IdentityHashMap<>();

  /**
   * Collect the distinct non-null keys the entity refers to at this moment, compared by identity
   *
   * @param entity to collect the keys of
   * @return distinct keys
   */
  private Set<K> distinctKeysOf(E entity) {
    Set<K> keys = Collections.newSetFromMap(new IdentityHashMap<>());
    for (K key : keysOf.apply(entity)) {
      if (key != null) {
        keys.add(key);
      }
    }
    return keys;
  }

  /**
   * Constructor
   *
   * @param keysOf collects the keys an entity refers to at this moment, may contain nulls and repeats
   */
  public IdentityEntityIndex(Function<? super E, ? extends Collection<? extends K>> keysOf) {
    this.keysOf = keysOf;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void index(E entity) {
    for (K key : distinctKeysOf(entity)) {
      entitiesByKey.computeIfAbsent(key, k -> new ArrayList<>(1)).add(entity);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void unindex(E entity) {
    if (entity == null) {
      return;
    }
    for (K key : distinctKeysOf(entity)) {
      var indexed = entitiesByKey.get(key);
      if (indexed != null) {
        indexed.removeIf(held -> held == entity);
        if (indexed.isEmpty()) {
          entitiesByKey.remove(key);
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void replace(E previous, E current) {
    if (previous == current) {
      return;
    }
    unindex(previous);
    index(current);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void update(E entity, Consumer<? super E> change) {
    unindex(entity);
    change.accept(entity);
    index(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void reindex(Iterable<? extends E> entities) {
    clear();
    entities.forEach(this::index);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void clear() {
    entitiesByKey.clear();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<E> get(K key) {
    return List.copyOf(entitiesByKey.getOrDefault(key, List.of()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public E getFirst(K key) {
    var indexed = entitiesByKey.get(key);
    return indexed == null ? null : indexed.get(0);
  }
}
