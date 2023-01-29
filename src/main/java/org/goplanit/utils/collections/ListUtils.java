package org.goplanit.utils.collections;

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
   * @teturn first entry, null if empty or null
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
   * @teturn last entry, null if empty or null
   */
  public static <T> T getLastValue(List<T> list){
    if(list == null || list.isEmpty()){
      return null;
    }
    return list.get(list.size()-1);
  }
}
