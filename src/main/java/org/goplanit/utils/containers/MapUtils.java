package org.goplanit.utils.containers;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MapUtils {

  /**
   * Invert map
   *
   * @param map original
   * @return inverted map
   * @param <K> key type
   * @param <V> value type
   */
  public static <K,V> Map<V, Set<K>> invertMap(Map<K,V> map) {
    return map.entrySet().stream().collect(Collectors.groupingBy(
        Map.Entry::getValue, Collectors.mapping(Map.Entry::getKey, Collectors.toSet())));
  }
}
