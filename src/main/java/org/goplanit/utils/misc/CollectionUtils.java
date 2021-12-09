package org.goplanit.utils.misc;

import java.util.Collection;

/**
 * Lightweight collection utilities
 * 
 * @author markr
 *
 */
public class CollectionUtils {

  /** Verify if collection is null or empty
   * 
   * @param c to check
   * @return true when null or empty
   */
  public static boolean nullOrEmpty(Collection<?> c) {
    return c==null || c.isEmpty();
  }
}
