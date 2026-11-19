package org.goplanit.utils.containers;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utilities for maps: changing their shape and protecting them from change
 * <p>
 * Maps are often handed out of a class, inverted or wrapped, and the same few lines would otherwise be repeated
 * </p>
 */
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

  /**
   * A map of maps that cannot be changed through it, at either level: each inner map is wrapped as well. A later
   * change to an inner map shows through it, a key added to the outer map later does not
   *
   * @param mapOfMaps to wrap
   * @return the map of maps that cannot be changed, in the order of the outer map
   * @param <K> outer key type
   * @param <K2> inner key type
   * @param <V> value type
   */
  public static <K, K2, V> Map<K, Map<K2, V>> unmodifiableMapOfMaps(Map<K, ? extends Map<K2, V>> mapOfMaps) {
    var view = new LinkedHashMap<K, Map<K2, V>>();
    mapOfMaps.forEach((key, map) -> view.put(key, Collections.unmodifiableMap(map)));
    return Collections.unmodifiableMap(view);
  }
}
