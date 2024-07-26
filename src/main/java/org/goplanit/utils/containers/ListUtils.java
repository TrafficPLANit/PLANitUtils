package org.goplanit.utils.containers;

import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.output.OutputUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utilities for lists
 */
public class ListUtils {

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
   * Transpose the given list of lists, e.g., [[1,2,3,4],["a","b","c","d"]] becomes [[1,"a"],[2,"b"],[3,"c"],[4,"d"]]
   *
   * @param <V> value type of list
   * @param <R> mapped return value of transposed list
   * @param original the original list of lists
   * @param mapEachValue apply map function to each value while transposing
   * @return transpose the transposed list of lists
   */
  public static <V,R> List<? extends List<R>> transpose(List<? extends List<V>> original, Function<V,R> mapEachValue) {
    return IntStream.range(0,original.get(0).size()).mapToObj(
            rowIndex -> original.stream().map( // for each col vector
                    originalCol -> originalCol.get(rowIndex)).map(mapEachValue).collect(Collectors.toList()) // apply mapping to each value
    ).collect(Collectors.toList()); // add all rows to list
  }

  /**
   * Transpose the given list of lists, e.g., [[1,2,3,4],["a","b","c","d"]] becomes [[1,"a"],[2,"b"],[3,"c"],[4,"d"]]
   *
   * @param original the original list of lists
   * @return transpose the transposed list of lists
   */
  public static List<? extends List<?>> transpose(List<? extends List<?>> original) {
    return IntStream.range(0,original.get(0).size()).mapToObj(
            rowIndex -> original.stream().map( // for each col vector
                    originalCol -> originalCol.get(rowIndex)).collect(Collectors.toList()) // format value and collect single row
    ).collect(Collectors.toList()); // add all rows to list
  }
}
