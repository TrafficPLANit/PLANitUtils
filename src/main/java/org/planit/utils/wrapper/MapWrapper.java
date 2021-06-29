package org.planit.utils.wrapper;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Wrapper class for a map instance
 * 
 * @author markr
 *
 * @param <T> map key
 * @param <U> map value
 */
public abstract class MapWrapper<K,V> implements Iterable<V>{

  /**
   * Map storing all key value pairs
   */
  private Map<K, V> theMap;  
  
  /** mapping from value to key */
  private final Function<V, K> valueToKey;
  
  /** Access to the wrapped map
   * 
   * @return wrapper map
   */
  protected Map<K, V> getMap(){
    return theMap;
  }
  
  /** Replace the wrapped map
   * 
   * param replacement map to use as replacement
   */
  protected void setMap(final Map<K, V> replacement){
    this.theMap = replacement;
  }  
  
  /** Constructor 
   * 
   * @param mapToWrap the map to wrap
   */
  public MapWrapper(final Map<K, V> mapToWrap, final Function<V, K> valueToKey) {
    this.theMap = mapToWrap;
    this.valueToKey = valueToKey;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterator<V> iterator() {
    return theMap.values().iterator();
  }

  /**
   * Register on the internal container
   * 
   * @param value to register
   */
  public V register(final V value) {
    return theMap.put(valueToKey.apply(value),value);
  }
  
  /**
   * Remove value from map
   * 
   * @param value to remove
   * @return removed entry if any
   */  
  public V remove(final V value) {
    return theMap.remove(valueToKey.apply(value));
  }  

  /**
   * Collect entry from container
   * 
   * @param key to use
   */ 
  public V get(final K key) {
    return theMap.get(key);
  }

  /**
   * The number of registered entries
   * 
   * @return number of entries
   */
  public int size() {
    return theMap.size();
  }
  
  /**
   * Verify if the map is empty
   * 
   * @return true when empty false otherwise
   */
  public boolean isEmpty() {
    return theMap.isEmpty();
  }  
  
  /**
   * Collect values as unmodifiable collection
   * 
   * @return collection
   */
  public Collection<V> toCollection() {
    return Collections.unmodifiableCollection(theMap.values());
  }  
  
  /**
   * Create a copy of the map's distinct values as a set 
   */
  public Set<V> copyOfValuesAsSet() {
    return Set.copyOf(theMap.values());
  }  
  
  /**
   * Retrieve a TravelerType by its XML Id
   * 
   * This method is not efficient, since it loops through all the registered traveler type in order to find the required entry.
   * 
   * @param function that checks a property of the value and the first which matches is returned
   * @return the retrieved traveler type, or null if no traveler type was found
   */
  public V findFirst(Predicate<V> valuePredicate) {
    for (V value: this) {
      if (valuePredicate.test(value)) {
        return value;
      }
    }
    return null;
  }  
}
