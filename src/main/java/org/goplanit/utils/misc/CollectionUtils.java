package org.goplanit.utils.misc;

import java.util.Collection;

/**
 * Lightweight collection utilities
 * 
 * @author markr
 *
 */
public class CollectionUtils {

  /**
   * Verify if collection is null or empty
   *
   * @param c to check
   * @return true when null or empty
   */
  public static boolean nullOrEmpty(Collection<?> c) {
    return c == null || c.isEmpty();
  }

  /**
   * Check equality on elements of collection and array by calling equals on each synchronised entry
   *
   * @param <T>        type of collection and array entries
   * @param collection to use
   * @param array      to use
   * @return true when equal false otherwise
   */
  public static <T> boolean equals(final Collection<T> collection, T[] array) {
    if (array.length != collection.size()) {
      return false;
    }
    var iter = collection.iterator();
    int index = 0;
    while (iter.hasNext() && iter.next().equals(array[index++])) {
    }

    return !iter.hasNext();
  }

  /**
   * Convert a collection of a generic type T to a raw array of this type
   *
   * @param <T>        type
   * @param collection to convert
   * @return the array
   */
  public static <T> T[] toArray(Collection<T> collection) {
    @SuppressWarnings("unchecked")
    T[] array = (T[]) java.lang.reflect.Array.newInstance(
            collection.iterator().next().getClass(), collection.size());
    return collection.toArray(array);
  }
}
