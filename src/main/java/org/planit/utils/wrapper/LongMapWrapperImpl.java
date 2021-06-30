package org.planit.utils.wrapper;

import java.util.Map;
import java.util.function.Function;

/**
 * Wrapper class for a map instance with Long keys
 * 
 * @author markr
 *
 * @param <U> map value
 */
public abstract class LongMapWrapperImpl<V> extends MapWrapperImpl<Long, V>{
 
  /** Constructor 
   * 
   * @param mapToWrap the map to wrap
   */
  public LongMapWrapperImpl(final Map<Long, V> mapToWrap, final Function<V, Long> valueToKey) {
    super(mapToWrap, valueToKey);
  }
  
  /**
   * Remove value from map by its key
   * 
   * @param key to use
   * @return removed entry if any
   */  
  public V remove(final long key) {
    return getMap().remove(key);
  }  

  /**
   * Collect entry from container
   * 
   * @param key to use
   */ 
  public V get(final long key) {
    return getMap().get(key);
  }
}
