package org.goplanit.utils.collections;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Utilities for integer lists
 */
public class IntegerListUtils {

  /**
   * extract a sublist of the first consecutive range of integers found in the given list, e.g., without gaps
   * so for 0,2,4,6,7,8,9,10,12 with offset 5, we find ,8,9,10. In case offset is 0, we only get 0 back
   *
   * @param offsetIndex (inclusive)
   * @param intList to extract longest sublist from
   * @return sublist, empty if out of range
   */
  public static List<Integer> getLongestConsecutiveSubList(int offsetIndex, List<Integer> intList){
    if(offsetIndex < 0 || offsetIndex >= intList.size()){
      return List.of();
    }

    final int expectedGapBetweenIndexAndIndexValue = intList.get(offsetIndex) - offsetIndex;
    return IntStream.range(offsetIndex, intList.size()).takeWhile( // take while no gap is found
            index -> intList.get(index) - index == expectedGapBetweenIndexAndIndexValue).map(
            index -> intList.get(index)).boxed().collect(Collectors.toList()); // map indices in to be removed list to its values
  }

  /**
   * Create a range between start and end
   *
   * @param start inclusive
   * @param end exclusive
   * @return integer list in ascending order
   */
  public static List<Integer> rangeOf(int start, int end){
    return IntStream.range(start, end).boxed().collect(Collectors.toList());
  }
}
