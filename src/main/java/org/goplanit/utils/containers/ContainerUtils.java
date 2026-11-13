package org.goplanit.utils.containers;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Utilities for containers
 */
public class ContainerUtils {

  /** Dummy not to be instantiated */
  private ContainerUtils(){}

  /**
   * Remove the items that meet a condition from a collection, pass after pass, until none does. The condition is
   * created again for each pass, from the collection as it is then, since removing one item can make another meet it
   *
   * @param <T> type of item
   * @param items to remove from
   * @param conditionForPass creates, for each pass, the condition an item to remove meets
   * @return true when any item was removed, false otherwise
   */
  public static <T> boolean removeUntilNoneMatches(
      Collection<T> items, Supplier<? extends Predicate<? super T>> conditionForPass) {
    boolean removedAny = false;
    while (items.removeIf(conditionForPass.get())) {
      removedAny = true;
    }
    return removedAny;
  }

  /**
   * Given the colletion and mapping to unmodifiable version of the collection check if not null and then apply the wrapping
   *
   * @param collection to wrap
   * @param wrapInUnmodifiable function to perform wrapping
   * @return wrapped collection
   *
   * @param <T> type of collection
   */
  public static <T extends Collection<?>> T wrapInUnmodifiableCollectionUnlessNull(
          T collection, Function<T,T> wrapInUnmodifiable) {
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

  /**
   * Wrap in an unmodifiable version unless it is null, then return null
   *
   * @param sortedSet to wrap
   * @return wrapped set, or null
   * @param <T> type of entries
   */
  public static <T> SortedSet<T> wrapInUnmodifiableSortedSetUnlessNull(SortedSet<T> sortedSet) {
    return wrapInUnmodifiableCollectionUnlessNull(sortedSet, Collections::unmodifiableSortedSet);
  }
}
