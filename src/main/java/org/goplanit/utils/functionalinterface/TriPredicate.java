package org.goplanit.utils.functionalinterface;

 /** Predicate Interface which can process three input objects and produce an output
  * There is no equivalent functional interface in the java.util.function library yet
  *
  * @author markr
  *
  * @param <T> first object to be processed
  * @param <U> second object to be processed
  * @param <V> third object to be processed
  */
@FunctionalInterface
public interface TriPredicate<T, U, V> {

  /**
   * Performs this operation on the given arguments
   *
   * @param t first object to be processed
   * @param u second object to be processed
   * @param v third object to be processed
   */
  public abstract boolean test(T t, U u, V v);
}
