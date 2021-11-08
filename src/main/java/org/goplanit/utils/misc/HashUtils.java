package org.goplanit.utils.misc;

import java.util.Arrays;

/** Common location to provide some utilities for common hashing tasks
 * 
 * @author markr
 */
public class HashUtils {

  /** When you want to avoid using nested hash maps and do not need access to the keys themselves, use a combined hash key via this method, 
   * so all can be stored in a single hash map which saves a lot of memory space. 
   * 
   * @param <T> type of the primitive keys
   * @param primitiveKeys to use
   * @return combinedKey
   */
  @SafeVarargs
  public static <T> int createCombinedHashCode(T... primitiveKeys) {
    return Arrays.hashCode(primitiveKeys);
  }
   
}
