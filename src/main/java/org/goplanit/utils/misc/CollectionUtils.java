package org.goplanit.utils.misc;

import org.goplanit.utils.exceptions.PlanItRunTimeException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Lightweight collection utilities
 * 
 * @author markr
 *
 */
public class CollectionUtils {

  /**
   * Verify if collection is null or empty
   *
   * @param c to check
   * @return true when null or empty
   */
  public static boolean nullOrEmpty(Collection<?> c) {
    return c == null || c.isEmpty();
  }

  /**
   * Check equality on elements of collection and array by calling equals on each synchronised entry
   *
   * @param <T>        type of collection and array entries
   * @param collection to use
   * @param array      to use
   * @return true when equal false otherwise
   */
  public static <T> boolean equals(final Collection<T> collection, T[] array) {
    if (array.length != collection.size()) {
      return false;
    }
    var iter = collection.iterator();
    int index = 0;
    while (iter.hasNext() && iter.next().equals(array[index++])) {
    }

    return !iter.hasNext();
  }

  /**
   * Convert a collection of a generic type T to a raw array of this type
   *
   * @param <T>        type
   * @param collection to convert
   * @return the array
   */
  public static <T> T[] toArray(Collection<T> collection) {
    @SuppressWarnings("unchecked")
    T[] array = (T[]) java.lang.reflect.Array.newInstance(
            collection.iterator().next().getClass(), collection.size());
    return collection.toArray(array);
  }

  /**
   * An array of a component type holding the values, in order. Unlike {@link #toArray(Collection)}, the component type
   * can be primitive and there can be no values. For example, 1L and 2L with {@code long.class} give the
   * {@code long[]} {1, 2}, as a method taking {@code long... ids} needs when it is called through reflection
   *
   * @param values to put in the array, each of the component type, or of its wrapper class for a primitive type
   * @param componentType of the array
   * @return the array, as an Object since an array of a primitive type is no Object[]
   * @throws IllegalArgumentException when a value does not fit the component type
   */
  public static Object toArray(Collection<?> values, Class<?> componentType) {
    var array = Array.newInstance(componentType, values.size());
    int index = 0;
    for (var value : values) {
      Array.set(array, index++, value);
    }
    return array;
  }

  /**
   * A collection holding the values, in order, of a kind a declared collection type accepts: an ArrayList for a List,
   * Collection or Iterable, and a LinkedHashSet for a Set, which keeps each value once. For example, to make the
   * argument of a method taking {@code Set<String>} when it is called through reflection
   *
   * @param values to put in the collection
   * @param collectionType declared, such as List or Set
   * @return the collection
   * @throws PlanItRunTimeException when neither an ArrayList nor a LinkedHashSet is of the type, such as a TreeSet
   */
  public static Collection<Object> toCollectionAssignableTo(Collection<?> values, Class<?> collectionType) {
    if (collectionType.isAssignableFrom(ArrayList.class)) {
      return new ArrayList<>(values);
    }
    PlanItRunTimeException.throwIf(!collectionType.isAssignableFrom(LinkedHashSet.class),
        "Cannot make a %s, only a List or Set", collectionType.getName());
    return new LinkedHashSet<>(values);
  }
}
