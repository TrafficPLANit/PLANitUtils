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
    while (!removeMatching(items, conditionForPass.get()).isEmpty()) {
      removedAny = true;
    }
    return removedAny;
  }

  /**
   * Remove the items that meet a condition from a collection and give them back, so a caller can report what it
   * removed, such as the modes dropped from a scheme because its toll roads do not price them
   *
   * @param <T> type of item
   * @param items to remove from
   * @param condition an item to remove meets
   * @return the items removed, in the order the collection held them, empty when none was
   */
  public static <T> List<T> removeMatching(Collection<T> items, Predicate<? super T> condition) {
    var removed = new ArrayList<T>();
    items.removeIf(item -> {
      boolean matches = condition.test(item);
      if (matches) {
        removed.add(item);
      }
      return matches;
    });
    return removed;
  }

  /**
   * Find the entries held by two or more owners, each with those owners: for example the link segments two sections of
   * a scheme both hold, which may not happen. What {@link MapUtils#invertMap} does for owners of one entry each, for
   * owners of several
   *
   * @param <O> type of the owners
   * @param <E> type of the entries
   * @param owners to look in
   * @param entriesOf gives the entries of an owner
   * @return each entry held by two or more owners, with those owners in the order given, entries in the order first
   *     held; empty when no entry is shared
   */
  public static <O, E> Map<E, List<O>> findEntriesSharedBetween(
      Collection<O> owners,
      Function<? super O, ? extends Collection<E>> entriesOf) {
    var ownersByEntry = new LinkedHashMap<E, List<O>>();
    for (var owner : owners) {
      for (var entry : new LinkedHashSet<E>(entriesOf.apply(owner))) {
        ownersByEntry.computeIfAbsent(entry, newEntry -> new ArrayList<>()).add(owner);
      }
    }
    ownersByEntry.values().removeIf(ownersOfEntry -> ownersOfEntry.size() < 2);
    return ownersByEntry;
  }

  /**
   * Given the colletion and mapping to unmodifiable version of the collection check if not null and then apply the
   * wrapping
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

  /**
   * The union of sets: each element of any of them, once. For example, the event types of an owner and of the
   * producers it holds
   *
   * @param <T> type of the elements
   * @param sets to join
   * @return unmodifiable set of the elements of all the sets
   */
  @SafeVarargs
  public static <T> Set<T> unmodifiableUnionOf(final Set<? extends T>... sets) {
    var union = new HashSet<T>();
    for (var set : sets) {
      union.addAll(set);
    }
    return Collections.unmodifiableSet(union);
  }
}
