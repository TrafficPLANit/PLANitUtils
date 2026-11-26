package org.goplanit.utils.functionalinterface;

/**
 * Functional interface processing three input objects, used to register initial costs in test cases. The
 * java.util.function library has no equivalent.
 *
 * @author gman6028
 *
 * @param <T> first object to be processed
 * @param <U> second object to be processed
 * @param <V> third object to be processed
 */
@FunctionalInterface
public interface TriConsumer<T, U, V> {

  /**
   * Performs this operation on the given arguments
   * 
   * @param t first object to be processed
   * @param u second object to be processed
   * @param v third object to be processed
   */
  public void accept(T t, U u, V v);
}
