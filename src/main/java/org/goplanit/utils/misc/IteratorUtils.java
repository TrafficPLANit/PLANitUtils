package org.goplanit.utils.misc;

import org.goplanit.utils.network.layer.service.ServiceLegSegment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


/** Utils for iterator class
 * 
 * @author markr
 */
public class IteratorUtils {

  /**
   * Get iterable from iterator
   *
   * @param iterator to convert to iterable
   * @return iterable
   * @param <T> type of iterator values
   */
  public static <T> Iterable<T> toIterable(Iterator<T> iterator) {
    return () -> iterator;
  }

  /**
   * Map iterator to a map with a custom key obtained from the entries
   *
   * @param iterator to use
   * @param getKey function to extract key from entries
   * @param mapToFill to fill
   * @return populated map
   *
   * @param <T> type of iterable
   * @param <K> type of key
   */
  public static <T,K> Map<K,T> toMap(Iterator<T> iterator, Function<T,K> getKey, Map<K,T> mapToFill){
    T entry = null;
    while(iterator.hasNext()){
      entry = iterator.next();
      mapToFill.put(getKey.apply(entry),entry);
    }
    return mapToFill; // for chaining
  }

  /**
   * Map iterator to a list with a custom key obtained from the entries
   *
   * @param iterator to use
   * @return populated list
   *
   * @param <T> type of iterator
   */
  public static <T> List<T> toList(Iterator<T> iterator){
    List<T> theList = new ArrayList<>();
    while(iterator.hasNext()){
      theList.add(iterator.next());
    }
    return theList;
  }

  /**
   * Iterator as stream
   *
   * @param iterator to get stream for
   * @return stream
   * @param <T> type of entries
   */
  public static <T> Stream<T> asStream(Iterator<T> iterator) {
    return IterableUtils.asStream(toIterable(iterator));
  }
}
