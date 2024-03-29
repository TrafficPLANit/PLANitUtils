package org.goplanit.utils.wrapper;

import org.goplanit.utils.misc.IterableUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Wrap a map as a named class for a non-null key
 * 
 * @author markr
 *
 * @param <K> key type
 * @param <V> value type
 */
public interface MapWrapper<K, V> extends Iterable<V> {

  /**
   * Collect first match by iterating through map based on some predicate
   *
   * @param <KK> key type
   * @param <VV> value type
   * @param mapWrapper the wrapper to apply to
   * @param valuePredicate the filter to apply to obtain the first match
   * @return found match
   */
  public static <KK,VV> VV firstMatch(MapWrapper<KK,VV> mapWrapper, Predicate<VV> valuePredicate) {
    return mapWrapper.stream().filter(valuePredicate).findFirst().orElse(null);
  }
  
  /**
   * Register on the internal container (no null keys allowed which will trigger a warning and the value not to be registered)
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
   * Remove all entries in provided collection.
   *
   * @param toBeRemoved the to be removed entries
   */
  public default void removeAll(Collection<V> toBeRemoved){
    toBeRemoved.forEach(e -> remove(e));
  }

  /**
   * Remove all values that satisfy the given condition
   *
   * @param condition to remove
   */
  public abstract void removeIf(Predicate<V> condition);
  
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
  public abstract boolean containsValue(V value);

  /**
   * Collect values as unmodifiable collection
   * 
   * @return collection
   */
  public abstract Collection<V> toCollection();

  /**
   * Create a copy of the map's distinct values as a set 
   * 
   * @return copy of values as set
   */
  public abstract Set<V> valuesAsNewSet();
  
  /**
   * Find first entry that matches the predicate
   * 
   * @param valuePredicate that checks a property of the value and the first which matches is returned
   * @return the retrieved entry, or null if no traveler type was found
   */
  public abstract V firstMatch(Predicate<V> valuePredicate);

  /**
   * Each map wrapper should be cloneable where the contents are references of the original where possible
   * but the underlying map itself is newly created
   * 
   * @return copy
   */
  public abstract MapWrapper<K, V> shallowClone();
  
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
    values.forEach( (v) -> { if(containsValue(v)){consumer.accept(v);}});
  }

  /**
   * Convert to a map with a custom key obtained from entries
   *
   * @param getCustomKey function to extract key from entries
   * @return populated map
   *
   * @param <K> type of key
   */
  public default <K> Map<K, V> toMap(Function<V,K> getCustomKey){
    return IterableUtils.toMap(this, getCustomKey, new HashMap<>());
  }

  /**
   * Create a stream from this iterable
   *
   * @return stream of all entries (values)
   */
  public default Stream<V> stream(){
    return StreamSupport.stream(this.spliterator(), false);
  }

  /**
   * stream in a sorted manner to allow for a specific ordering other than the underlying key used
   *
   * @param <M> type of comparable
   * @param comparingFunction to apply to sorted stream
   * @return stream of all entries (values) ordered by given comparing function
   */
  public default <M extends Comparable> Stream<V> streamSorted(Function<V,M> comparingFunction){
    return stream().sorted(Comparator.comparing(comparingFunction));
  }

}