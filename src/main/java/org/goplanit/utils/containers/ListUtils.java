package org.goplanit.utils.containers;

import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.output.OutputUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
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
   * Verify if a list holds this very instance, compared by identity rather than with equals. Entities equal each other
   * by id, and ids can be recreated in place, so a list of entities is searched by identity
   *
   * @param <T> type of entry
   * @param list to look in
   * @param entry to look for
   * @return true when the list holds this instance, false otherwise
   */
  public static <T> boolean containsByIdentity(List<? extends T> list, T entry){
    return list.stream().anyMatch(held -> held == entry);
  }
}
