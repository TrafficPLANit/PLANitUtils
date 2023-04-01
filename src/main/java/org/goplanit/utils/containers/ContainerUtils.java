package org.goplanit.utils.containers;

import org.goplanit.utils.mode.Mode;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Utilities for containers
 */
public class ContainerUtils {

  /**
   * Given the colletion and mapping to unmodifiable version of the collection check if not null and then apply the wrapping
   *
   * @param collection to wrap
   * @param wrapInUnmodifiable function to perform wrapping
   * @return wrapped collection
   *
   * @param <T> type of collection
   */
  public static <T extends Collection<?>> T wrapInUnmodifiableCollectionUnlessNull(T collection, Function<T,T> wrapInUnmodifiable) {
    if(collection == null){
      return null;
    }
    return wrapInUnmodifiable.apply(collection);
  }

  /**
   * Wrap in an unmodifiable version unless it is null, then return null
   *
   * @param list to wrap
   * @return wrapped list, or null
   * @param <T> type of entries
   */
  public static <T> List<T> wrapInUnmodifiableListUnlessNull(List<T> list) {
    return wrapInUnmodifiableCollectionUnlessNull(list, Collections::unmodifiableList);
  }

  /**
   * Wrap in an unmodifiable version unless it is null, then return null
   *
   * @param set to wrap
   * @return wrapped set, or null
   * @param <T> type of entries
   */
  public static <T> Set<T> wrapInUnmodifiableSetUnlessNull(Set<T> set) {
    return wrapInUnmodifiableCollectionUnlessNull(set, Collections::unmodifiableSet);
  }
}
