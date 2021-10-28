package org.planit.utils.predicate;

import java.util.function.Predicate;

/** NotNull predicate
 * 
 * @author markr
 *
 * @param <T> type to check
 */
public class NotNull implements Predicate<Object>{

  @Override
  public boolean test(Object t) {
    return t != null;
  }

}
