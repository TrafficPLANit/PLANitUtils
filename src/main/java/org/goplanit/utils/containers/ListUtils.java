package org.goplanit.utils.containers;

import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.output.OutputUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utilities for lists
 */
public class ListUtils {

  /** static utility class, not to be instantiated */
  private ListUtils() {
  }

  /**
   * Get first entry of list
   *
   * @param list to use
   * @param <T> type
   * @return first entry, null if empty or null
   */
  public static <T> T getFirstValue(List<T> list){
    if(list == null || list.isEmpty()){
      return null;
    }
    return list.get(0);
  }

  /**
   * Get last entry of list
   *
   * @param list to use
   * @param <T> type
   * @return last entry, null if empty or null
   */
  public static <T> T getLastValue(List<T> list){
    if(list == null || list.isEmpty()){
      return null;
    }
    return list.get(list.size()-1);
  }

  /**
   * Collect every unordered pair of different entries of the list once, in the order of the list: for x, y and z, the
   * pairs (x,y), (x,z) and (y,z). Unlike {@link #getPairPermutations}, a pair is not repeated the other way round, so
   * a check that is the same either way, such as whether two time bands overlap, is made once per pair
   *
   * @param list to pair the entries of
   * @return the pairs, empty for a list of one entry or none
   * @param <T> type of list
   */
  public static <T> List<Pair<T,T>> getPairCombinations(List<T> list) {
    var combinations = new ArrayList<Pair<T,T>>();
    for (int first = 0; first < list.size(); first++) {
      for (int second = first + 1; second < list.size(); second++) {
        combinations.add(Pair.of(list.get(first), list.get(second)));
      }
    }
    return combinations;
  }

  /**
   * Collect every ordered pair of entries of the list. An entry is paired with itself only when self permutations are
   * kept, or when the list holds a single entry
   *
   * @param list to get permutations from
   * @param keepSelfPermutation when true keep permutation with self
   * @return result
   * @param <T> type of list
   */
  public static <T> List<Pair<T,T>> getPairPermutations(List<T> list, boolean keepSelfPermutation){
    List<Pair<T,T>> permutations = new ArrayList<>();
    if(list.size()==1){
      permutations.add(Pair.of(getFirstValue(list), getFirstValue(list)));
    }else {
      for (var val1 : list) {
        for (var val2 : list) {
          if (val1 == val2 && !keepSelfPermutation) {
            continue;
          }
          permutations.add(Pair.of(val1, val2));
        }
      }
    }
    return permutations;
  }

  /**
   * Find every way to divide the entries of a list into groups, each entry in exactly one group, e.g., [a,b] gives
   * [[a],[b]] and [[a,b]]. Within a group the entries keep the order of the list. There are as many ways as the Bell
   * number of the list's size, 115975 for ten entries, so this is meant for short lists
   *
   * @param <T> type of entry
   * @param list to divide
   * @return each way to divide it, as a list of groups
   */
  public static <T> List<List<List<T>>> findAllPartitions(List<T> list) {
    var result = new ArrayList<List<List<T>>>();
    if (list.isEmpty()) {
      result.add(new ArrayList<>());
      return result;
    }
    var first = list.get(0);
    for (var rest : findAllPartitions(list.subList(1, list.size()))) {
      var alone = new ArrayList<List<T>>();
      alone.add(new ArrayList<>(List.of(first)));
      rest.forEach(group -> alone.add(new ArrayList<>(group)));
      result.add(alone);
      for (int index = 0; index < rest.size(); ++index) {
        var joined = new ArrayList<List<T>>();
        for (int other = 0; other < rest.size(); ++other) {
          var copy = new ArrayList<>(rest.get(other));
          if (other == index) {
            copy.add(0, first);
          }
          joined.add(copy);
        }
        result.add(joined);
      }
    }
    return result;
  }

  /**
   * Compare two lists entry by entry, as words are ordered in a dictionary: the first entries that differ decide, and
   * when one list starts with all of the other, the shorter one comes first, e.g., [1,2] before [1,3] and [1] before
   * [1,2]
   *
   * @param <T> type of entry
   * @param first list to compare
   * @param second list to compare it with
   * @param entryOrder how two entries compare
   * @return negative when the first comes first, positive when the second does, zero when they are equal
   */
  public static <T> int compareEntryByEntry(
      List<? extends T> first, List<? extends T> second, Comparator<? super T> entryOrder) {
    for (int index = 0; index < Math.min(first.size(), second.size()); ++index) {
      int comparison = entryOrder.compare(first.get(index), second.get(index));
      if (comparison != 0) {
        return comparison;
      }
    }
    return Integer.compare(first.size(), second.size());
  }

  /**
   * Divide entries into disjoint sets, where entries joined by a pair, directly or through other entries, are in the
   * same set, e.g., entries a, b, c and d with the pairs (a, b) and (b, c) give the sets {a, b, c} and {d}
   *
   * @param <T> type of entry
   * @param entries to divide
   * @param joinedPairs pairs of entries that belong in one set, each entry one of the entries
   * @return the disjoint sets, in the order of their first entry, each in the order of the entries
   */
  public static <T> List<Set<T>> extractDisjointSets(List<T> entries, Collection<Pair<T, T>> joinedPairs) {
    /* the entries each entry is joined to directly */
    var joinedTo = new HashMap<T, List<T>>();
    for (var pair : joinedPairs) {
      joinedTo.computeIfAbsent(pair.first(), key -> new ArrayList<>()).add(pair.second());
      joinedTo.computeIfAbsent(pair.second(), key -> new ArrayList<>()).add(pair.first());
    }
    /* each entry not yet in a set starts one, which every entry it reaches through the pairs belongs to */
    var setOf = new HashMap<T, Set<T>>();
    var disjointSets = new ArrayList<Set<T>>();
    for (var entry : entries) {
      if (setOf.containsKey(entry)) {
        continue;
      }
      var set = new LinkedHashSet<T>();
      disjointSets.add(set);
      setOf.put(entry, set);
      var pending = new ArrayDeque<>(List.of(entry));
      while (!pending.isEmpty()) {
        for (var joined : joinedTo.getOrDefault(pending.pop(), List.of())) {
          if (setOf.putIfAbsent(joined, set) == null) {
            pending.push(joined);
          }
        }
      }
    }
    /* fill each set in the order of the entries */
    entries.forEach(entry -> setOf.get(entry).add(entry));
    return disjointSets;
  }

  /**
   * Filter entries in order, each judged together with the entries kept before it: an entry is kept when the entries
   * kept so far and the entry itself meet the condition. So what is kept depends on the order. For example, entries
   * 5, 4, 2 and 1 with the condition that their sum is at most 8 give 5, 2 and 1: 4 is left out, since 5 and 4 sum to
   * 9
   *
   * @param <T> type of entry
   * @param entries to filter, in the order to judge them
   * @param keptMeetCondition true when the given entries, those kept so far followed by the one judged, may be kept
   * @return the entries kept, in order
   */
  public static <T> List<T> filterEachTogetherWithThoseKept(
      Collection<? extends T> entries, Predicate<List<T>> keptMeetCondition) {
    var kept = new ArrayList<T>();
    for (var entry : entries) {
      kept.add(entry);
      if (!keptMeetCondition.test(Collections.unmodifiableList(kept))) {
        kept.remove(kept.size() - 1);
      }
    }
    return kept;
  }

  /**
   * Transpose the given list of lists, e.g., [[1,2,3,4],["a","b","c","d"]] becomes [[1,"a"],[2,"b"],[3,"c"],[4,"d"]]
   *
   * @param <V> value type of list
   * @param <R> mapped return value of transposed list
   * @param original the original list of lists
   * @param mapEachValue apply map function to each value while transposing
   * @return transpose the transposed list of lists
   */
  public static <V,R> List<? extends List<R>> transpose(
          List<? extends List<V>> original, Function<V,R> mapEachValue) {
    return IntStream.range(0,original.get(0).size()).mapToObj(
            // for each col vector
            rowIndex -> original.stream().map(
                    // apply mapping to each value
                    originalCol -> originalCol.get(rowIndex)).map(mapEachValue).collect(Collectors.toList())
            // add all rows to list
    ).collect(Collectors.toList());
  }

  /**
   * Transpose the given list of lists, e.g., [[1,2,3,4],["a","b","c","d"]] becomes [[1,"a"],[2,"b"],[3,"c"],[4,"d"]]
   *
   * @param original the original list of lists
   * @return transpose the transposed list of lists
   */
  public static List<? extends List<?>> transpose(List<? extends List<?>> original) {
    return IntStream.range(0,original.get(0).size()).mapToObj(
            // for each col vector
            rowIndex -> original.stream().map(
                    // format value and collect single row
                    originalCol -> originalCol.get(rowIndex)).collect(Collectors.toList())
            // add all rows to list
    ).collect(Collectors.toList());
  }

  /**
   * Update each entry of the list based on the mapping provided. The list is rebuilt rather than updated in place, so
   * the entries are replaced by the mapped objects themselves, even when these equal the originals
   *
   * @param <T> type of entry
   * @param list to update
   * @param entryToEntryMapping should contain the entry as currently in the list and then the value is the new entry to
   *                            replace it
   * @param removeMissingMappings when true an entry without a mapping is removed, otherwise it is left in-tact
   */
  public static <T> void updateMapping(
      List<T> list, Function<T, T> entryToEntryMapping, boolean removeMissingMappings){
    var mapped = new ArrayList<T>(list.size());
    for(var entry : list){
      var mappedEntry = entryToEntryMapping.apply(entry);
      if(mappedEntry != null){
        mapped.add(mappedEntry);
      }else if(!removeMissingMappings){
        mapped.add(entry);
      }
    }
    list.clear();
    list.addAll(mapped);
  }

  /**
   * Add an entry right after an existing one, found by identity, or at the end when the list does not hold it, so the
   * list keeps an order such as that of the link segments along a road; nothing happens when it holds the entry already
   *
   * @param <T> type of entry
   * @param list to add to
   * @param existing entry to add after, compared by identity
   * @param entry to add
   * @return true when added
   */
  public static <T> boolean addAfter(List<T> list, T existing, T entry) {
    if (containsByIdentity(list, entry)) {
      return false;
    }
    int index = 0;
    while (index < list.size() && list.get(index) != existing) {
      ++index;
    }
    list.add(Math.min(index + 1, list.size()), entry);
    return true;
  }

  /**
   * Verify if a list, or any other collection, holds this very instance, compared by identity rather than with equals.
   * Entities equal each other by id, and ids can be recreated in place, so a collection of entities is searched by
   * identity
   *
   * @param <T> type of entry
   * @param list to look in
   * @param entry to look for
   * @return true when the list holds this instance, false otherwise
   */
  public static <T> boolean containsByIdentity(Collection<? extends T> list, T entry){
    return list.stream().anyMatch(held -> held == entry);
  }

  /**
   * The position of each entry of a list, found by identity rather than with equals, as {@link #containsByIdentity}
   * searches: for an array indexed by position, such as a lookup table's amounts per section of a pricing scheme
   *
   * @param <T> type of entry
   * @param list to index
   * @return each entry with its position in the list, the first when it holds the entry twice
   */
  public static <T> Map<T, Integer> createIndexByIdentity(List<? extends T> list){
    var indexByIdentity = new IdentityHashMap<T, Integer>(2 * list.size());
    for (int position = list.size() - 1; position >= 0; --position) {
      indexByIdentity.put(list.get(position), position);
    }
    return indexByIdentity;
  }
}
