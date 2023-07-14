package org.goplanit.utils.misc;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Custom pair class similar to C++. By default we compare based on the first
 * value
 * 
 * @author markr
 *
 * @param <A> first object in pair
 * @param <B> second object in pair
 */
public class ComparablePair<A extends Comparable, B extends Comparable> extends Pair<A, B> implements Comparable<Pair<A,B>>{

  /** Factory method
   *
   * @param <A> type of valueA
   * @param <B> type of valueB
   *
   * @param valueA first
   * @param valueB second
   * @return new pair
   */
  public static <A extends Comparable,B extends Comparable> ComparablePair<A, B> of(A valueA, B valueB) {
    return new ComparablePair<>(valueA, valueB);
  }

  /**
   * Constructor
   *
   * @param first  first object in pair
   * @param second second object in pair
   */
  protected ComparablePair(A first, B second) {
    super(first, second);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(Pair<A, B> o) {
    if(this.first == null){
      return o.first == null ? 0 : -1;
    }
    if(o.first == null){
      return 1;
    }

    int firstCompare = this.first.compareTo(o.first);
    if( firstCompare != 0) {
      return firstCompare;
    }

    if(this.second == null){
      return o.second == null ? 0 : -1;
    }
    if(o.second == null){
      return 1;
    }

    return this.second.compareTo(o.second);
  }
}
