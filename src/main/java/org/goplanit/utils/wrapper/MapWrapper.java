package org.goplanit.utils.wrapper;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
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
   * @return old value if any
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
   * Clear all entries from map
   * 
   */
  public abstract void clear();  

  /**
   * Collect entry from container
   * 
   * @param key to use
   * @return value for key
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
  
  /** Check if value is already present
   * 
   * @param value to verify
   * @return true if present, false otherwise
   */
  public abstract boolean contains(V value);

  /**
   * Collect values as unmodifiable collection
   * 
   * @return collection
   */
  public abstract Collection<V> toCollection();

  /**
   * Create a copy of the map's distinct values as a set 
   * 
   * @return copt of values as set
   */
  public abstract Set<V> valuesAsNewSet();
  
  /**
   * Find first entry that matches the predicate
   * 
   * @param valuePredicate that checks a property of the value and the first which matches is returned
   * @return the retrieved entry, or null if no traveler type was found
   */
  public abstract V findFirst(Predicate<V> valuePredicate);

  /**
   * Each map wrapper should be cloneable where the contents are references of the original where possible
   * but the underlying map itself is newly created
   * 
   * @return copy
   */
  public abstract MapWrapper<K, V> clone();
  
  /** collect the key used for the given value
   * 
   * @param value to collect used key for
   * @return key
   */
  public abstract K getKeyByValue(V value);
    
  /** collect the first entry based on the iterator's result, which is not necessarily the
   * fist entry ordered by the key, it is just the first entry the iterator would provide
   * 
   * @return first iterable entry, null if empty
   */
  public default V getFirst() {
    if(isEmpty()) {
      return null;
    }
    return iterator().next();
  }

  /** add all elements of iterable
   * 
   * @param iterable to add elements of
   */
  public default void addAll(Iterable<? extends V> iterable) {
    iterable.forEach( v -> register(v));
  }
  
  /** apply provided consumer to each element in values as long as that element is registered on this wrapper
   * 
   * @param <T> type of values
   * @param values to apply consumer to when they are registered in this wrapper
   * @param consumer to apply
   */
  public default <T extends V> void forEachIn(final Collection<T> values, final Consumer<T> consumer) {
    values.forEach( (v) -> { if(contains(v)){consumer.accept(v);};});
  }
    

}