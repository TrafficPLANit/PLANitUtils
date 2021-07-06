package org.planit.utils.wrapper;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

/**
 * Abstract Wrapper class implemented for a map instance
 * 
 * @author markr
 *
 * @param <T> map key
 * @param <U> map value
 */
public abstract class MapWrapperImpl<K, V> implements MapWrapper<K, V>{
  
  /** logger to use */
  private static final Logger LOGGER = Logger.getLogger(MapWrapperImpl.class.getCanonicalName());

  /**
   * Map storing all key value pairs
   */
  private Map<K, V> theMap;  
  
  /** mapping from value to key */
  private final Function<V, K> valueToKey;
  
  /** Create an empty map of the same implementation using reflection, i.e., if the map is a TreeMap
   * a TreeMap is used, same for hashmap etc.
   *  
   * @param mapToCopy
   * @return created copy with the same underlying map implementation
   */
  @SuppressWarnings("unchecked")
  protected static <U,L> Map<U,L> createEmptyInstance(Map<U,L> mapToCopy){
    try {
      return mapToCopy.getClass().getConstructor().newInstance(); 
    } catch (Exception e) {
      LOGGER.warning("Unable to instantiate new instance of map signature in map wrapper in copy constructor");
    }
    return null;
  }
  
  /** Collect the function used to map value to key
   * 
   * @return valueToKey
   */
  public Function<V, K> getValueToKey() {
    return valueToKey;
  }

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
  public MapWrapperImpl(final Map<K, V> mapToWrap, final Function<V, K> valueToKey) {
    this.theMap = mapToWrap;
    this.valueToKey = valueToKey;
  }
    
  /** Copy constructor 
   * 
   * @param other to copy
   */
  public MapWrapperImpl(final MapWrapperImpl<K,V> other) {
    this.valueToKey = other.valueToKey;
    
    Map<K, V> newMap = createEmptyInstance(theMap);    
    if(newMap != null) {
      newMap.putAll(other.getMap());
      this.theMap = newMap;
    }
  }   

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterator<V> iterator() {
    return theMap.values().iterator();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public V register(final V value) {
    return theMap.put(valueToKey.apply(value),value);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public V remove(final V value) {
    return theMap.remove(valueToKey.apply(value));
  }  

  /**
   * {@inheritDoc}
   */
  @Override
  public V get(final K key) {
    return theMap.get(key);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int size() {
    return theMap.size();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isEmpty() {
    return theMap.isEmpty();
  }  
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<V> toCollection() {
    return Collections.unmodifiableCollection(theMap.values());
  }  
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Set<V> copyOfValuesAsSet() {
    return Set.copyOf(theMap.values());
  }  
  
  /**
   * {@inheritDoc}
   */
  @Override
  public V findFirst(Predicate<V> valuePredicate) {
    for (V value: this) {
      if (valuePredicate.test(value)) {
        return value;
      }
    }
    return null;
  }  
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MapWrapperImpl<K,V> clone();
}
