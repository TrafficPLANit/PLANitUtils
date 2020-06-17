package org.planit.utils.functionalinterface;

/**
 * Function Interface which can process three input objects and produce an output
 * 
 * Used in PlanItIO test cases.
 * 
 * There is no equivalent functional interface in the java.util.function library so we have created
 * this one.
 * 
 * @author gman6028
 *
 * @param <R> first object to be processed
 * @param <S> second object to be processed
 * @param <T> third object to be processed
 * @param <U> object generated
 */
@FunctionalInterface
public interface TriFunction<R, S, T, U> {

  /**
   * Performs this operation on the given arguments
   * 
   * @param r first object to be processed
   * @param s second object to be processed
   * @param t third object to be processed
   * @return object of specified type
   */
  public U apply(R r, S s, T t);

}
