package org.goplanit.utils.wrapper;

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
 * @param <K> map key
 * @param <V> map value
 */
public class MapWrapperImpl<K, V> implements MapWrapper<K, V>{
  
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
   * @param <U> map to copy key type
   * @param <L> map to copy value type 
   * @param mapToCopy the map to copy
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
   * @param replacement map to use as replacement
   */
  protected void setMap(final Map<K, V> replacement){
    this.theMap = replacement;
  }  
  
  /** Constructor 
   * 
   * @param mapToWrap the map to wrap
   * @param valueToKey function to map values to their key
   */
  public MapWrapperImpl(final Map<K, V> mapToWrap, final Function<V, K> valueToKey) {
    this.theMap = mapToWrap;
    this.valueToKey = valueToKey;
  }
  
  /** Constructor 
   * 
   * @param mapToWrap the map to wrap
   * @param valueToKey function to map values to their key
   * @param populateWith values to populate the map to wrap with based on index function
   */
  public MapWrapperImpl(final Map<K, V> mapToWrap, final Function<V, K> valueToKey, final Collection<V> populateWith) {
    this.theMap = mapToWrap;
    this.valueToKey = valueToKey;
    populateWith.forEach( value -> register(value));
  }  
  
  /** Special copy(like) constructor allowing one to create a copy with a different index function
   * 
   * @param <U> key type of map with values to populate this wrapper with
   * @param mapToWrap the map to wrap
   * @param valueToKey function to map values to their key
   * @param populateWith values to populate the map to wrap with based on provided index function
   */
  public <U> MapWrapperImpl(final Map<K, V> mapToWrap, final Function<V, K> valueToKey, final MapWrapper<U,V> populateWith) {
    this.theMap = mapToWrap;
    this.valueToKey = valueToKey;
    populateWith.forEach( value -> register(value));
  }   
    
  /** Copy constructor which creates a new underlying map and copies entries over (shallow)
   * 
   * @param other to copy
   */
  public MapWrapperImpl(final MapWrapperImpl<K,V> other) {
    this.valueToKey = other.valueToKey;
    
    Map<K, V> newMap = createEmptyInstance(other.theMap);
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
    return theMap.put(getKeyByValue(value),value);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public V remove(final V value) {
    return theMap.remove(getKeyByValue(value));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeIf(Predicate<V> condition) {
    theMap.values().removeIf(condition);
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
  public Set<V> valuesAsNewSet() {
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
  public MapWrapperImpl<K,V> shallowClone(){
    return new MapWrapperImpl<>(this);
  }

  /**
   * {@inheritDoc}
   */  
  @Override
  public boolean containsValue(V value) {
    return getMap().containsValue(value);
  }

  /**
   * {@inheritDoc}
   */ 
  @Override
  public K getKeyByValue(V value) {
    return valueToKey.apply(value);
  }
  
  /**
   * {@inheritDoc}
   */ 
  @Override
  public void clear() {
    this.theMap.clear();
  }
}
