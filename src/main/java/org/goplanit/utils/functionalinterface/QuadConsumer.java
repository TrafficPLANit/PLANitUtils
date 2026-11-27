package org.goplanit.utils.functionalinterface;

import org.goplanit.utils.exceptions.PlanItException;

/**
 * Functional interface processing four input objects, used to register initial costs in test cases. The
 * java.util.function library has no equivalent.
 *
 * @author gman6028
 *
 * @param <T> first object to be processed
 * @param <U> second object to be processed
 * @param <V> third object to be processed
 * @param <W> fourth object to be processed
 */
@FunctionalInterface
public interface QuadConsumer<T, U, V, W> {

  /**
   * Performs this operation on the given arguments
   * 
   * @param t first object to be processed
   * @param u second object to be processed
   * @param v third object to be processed
   * @param w fourth object to be processed
   * @throws PlanItException thrown if there is an error
   */
  public void accept(T t, U u, V v, W w) throws PlanItException;

}
