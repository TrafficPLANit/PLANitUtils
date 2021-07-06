package org.planit.utils.wrapper;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Wrap a map as a named class
 * 
 * @author markr
 *
 * @param <K> key type
 * @param <V> value type
 */
public interface MapWrapper<K, V> extends Iterable<V>, Cloneable {

  /**
   * Register on the internal container
   * 
   * @param value to register
   */
  public abstract V register(V value);

  /**
   * Remove value from map
   * 
   * @param value to remove
   * @return removed entry if any
   */
  public abstract V remove(V value);

  /**
   * Collect entry from container
   * 
   * @param key to use
   */
  public abstract V get(K key);

  /**
   * The number of registered entries
   * 
   * @return number of entries
   */
  public abstract int size();

  /**
   * Verify if the map is empty
   * 
   * @return true when empty false otherwise
   */
  public abstract boolean isEmpty();

  /**
   * Collect values as unmodifiable collection
   * 
   * @return collection
   */
  public abstract Collection<V> toCollection();

  /**
   * Create a copy of the map's distinct values as a set 
   */
  public abstract Set<V> copyOfValuesAsSet();

  /**
   * Find first entry that matches the predicate
   * 
   * @param function that checks a property of the value and the first which matches is returned
   * @return the retrieved traveler type, or null if no traveler type was found
   */
  public abstract V findFirst(Predicate<V> valuePredicate);

  /**
   * Each map wrapper should be cloneable where the contents are references of the original where possible
   * but the underlying map itself is newly created
   * 
   * @return copy
   */
  public abstract MapWrapper<K, V> clone();

}