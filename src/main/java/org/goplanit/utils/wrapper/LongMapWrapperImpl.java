package org.goplanit.utils.wrapper;

import java.util.Map;
import java.util.function.Function;

/**
 * Wrapper class for a map instance with Long keys
 * 
 * @author markr
 *
 * @param <V> map value
 */
public abstract class LongMapWrapperImpl<V> extends MapWrapperImpl<Long, V> implements LongMapWrapper<V>{
 
  /** Constructor 
   * 
   * @param mapToWrap the map to wrap
   * @param valueToKey function to map a value to its key
   */
  public LongMapWrapperImpl(final Map<Long, V> mapToWrap, final Function<V, Long> valueToKey) {
    super(mapToWrap, valueToKey);
  }
  
  /** Copy constructor 
   * 
   * @param other to copy
   */
  public LongMapWrapperImpl(final LongMapWrapperImpl<V> other) {
    super(other);
  }  
  
  /**
   * {@inheritDoc}
   */  
  @Override
  public V remove(final long key) {
    return getMap().remove(key);
  }  

  /**
   * {@inheritDoc}
   */
  @Override  
  public V get(final long key) {
    return getMap().get(key);
  }
  
  /**
   * {@inheritDoc}
   */
  @Override  
  public boolean containsKey(final long key) {
    return getMap().containsKey(key);
  }  
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract LongMapWrapperImpl<V> shallowClone();
}
