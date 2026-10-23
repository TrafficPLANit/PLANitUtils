package org.goplanit.utils.function;

import java.util.function.Function;

/**
 * A functional interface that executes a side-effect action on an element
 * and strictly returns the original element to maintain stream pass-through continuity.
 *
 * @param <T> the type of element flowing through the stream
 */
@FunctionalInterface
public interface Tap<T> extends Function<T, T> {

  /**
   * Executes the side-effect action on the element.
   *
   * @param element the element to process
   */
  void execute(T element);

  /**
   * Overrides the standard Function apply method to force the pass-through behavior.
   * This method cannot be overridden by implementing lambdas.
   *
   * @param element the stream element
   * @return the exact same stream element
   */
  @Override
  default T apply(T element) {
    execute(element);
    return element;
  }
}

