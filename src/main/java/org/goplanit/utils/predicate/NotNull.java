package org.goplanit.utils.predicate;

import java.util.function.Predicate;

/** NotNull predicate
 * 
 * @author markr
 *
 */
public class NotNull implements Predicate<Object>{

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean test(Object t) {
    return t != null;
  }

}
