package org.goplanit.utils.misc;

public class ComparableUtils {

  /**
   *   Helper to handle potential nulls during comparison
   * @param <T> type
   * @param a first
   * @param b second
   */
  public static <T extends Comparable<T>> int compareNullable(T a, T b) {
    if (a == b) return 0;
    if (a == null) return -1;
    if (b == null) return 1;
    return a.compareTo(b);
  }
}
