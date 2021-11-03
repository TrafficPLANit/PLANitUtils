package org.goplanit.utils.box;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class assisting in more complex (auto-)boxing of types in for example container classes
 * @author markr
 *
 */
public class BoxUtils {

  
  /** Convert an integer list to a long list
   * 
   * @param integerList to convert
   * @return created long list
   */
  public static List<Long> IntegerToLongList(List<Integer> integerList) {
    return integerList.stream().mapToLong(Integer::longValue).boxed().collect(Collectors.toList());
  }
}
