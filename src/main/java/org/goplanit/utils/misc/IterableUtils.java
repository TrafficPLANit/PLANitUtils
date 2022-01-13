package org.goplanit.utils.misc;

import java.util.stream.StreamSupport;


/** Utils for iterable class
 * 
 * @author markr
 */
public class IterableUtils {

  /**
   * Collect the number of entry edge segments of this vertex
   * <p>
   * slow method because it requires iterating over the underlying iterable since it is not a collection we are obtain the count from
   *
   * @param <T> iterable type
   * @param iterable to count number of entries on by looping over the entire iterable
   * @return number of entry edge segments
   */
  public static <T> long  sizeOfUsingStream(Iterable<T> iterable) {
    return (int) StreamSupport.stream(iterable.spliterator(), false).count();
  }
  
  /**
   * Collect the number of entry edge segments of this vertex
   * <p>
   * slow method because it requires iterating over the underlying iterable since it is not a collection we are obtain the count from
   *
   * @param <T> iterable type
   * @param iterable to count number of entries on by looping over the entire iterable
   * @return number of entry edge segments
   */
  public static <T> long sizeOfUsingLoop(Iterable<T> iterable) {
    long count=0;
    for(@SuppressWarnings("unused") var entry : iterable) {
      ++count;
    }
    return count;
  }  
}
