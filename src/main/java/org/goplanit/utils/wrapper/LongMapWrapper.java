package org.goplanit.utils.wrapper;

/** Version of MApWrapper when we use Long as key
 * 
 * @author markr
 *
 * @param <V> value type of the map
 */
public interface LongMapWrapper<V> extends MapWrapper<Long,V> {

  /**
   * Remove value from map by the long key
   * 
   * @param key to remove
   * @return removed entry if any
   */
  public abstract V remove(final long key); 
  
  /**
   * Collect entry from container using primitive
   * 
   * @param key to use
   * @return value found
   */
  public abstract V get(final long key);
  
  /**
   * Verify if entry is present
   * 
   * @param key to use
   * @return true when present, false otherwise
   */
  public abstract boolean containsKey(final long key);
    
}