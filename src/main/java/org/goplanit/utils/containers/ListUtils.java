package org.goplanit.utils.containers;

import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.mode.Mode;

import java.util.ArrayList;
import java.util.List;

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
  public static <T> T getFirst(List<T> list){
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
      permutations.add(Pair.of(getFirst(list), getFirst(list)));
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

}
