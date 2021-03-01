package org.planit.utils.misc;

import java.util.Objects;

/**
 * Custom pair class similar to C++. By default we compare based on the first
 * value
 * 
 * @author markr
 *
 * @param <A> first object in pair
 * @param <B> second object in pair
 */
public class Pair<A, B> {

  /**
   * The first object in this Pair
   */
  protected final A first;

  /**
   * The second object in this Pair
   */
  protected final B second;

  /**
   * Constructor
   * 
   * @param first first object in pair
   * @param second second object in pair
   */
  protected Pair(A first, B second) {
    super();
    this.first = first;
    this.second = second;
  }
  
  /** Factory method
   * 
   * @param <A> type of valueA
   * @param <B> type of valueB
   * 
   * @param valueA first
   * @param valueB second
   * @return new pair
   */
  public static <A,B> Pair<A, B> of(A valueA, B valueB) {
    return new Pair<A,B>(valueA, valueB);
  }  

  /**
   * @see java.lang.Object#hashCode()
   * 
   * @return hashCode for this entity
   */
  public int hashCode() {
    return Objects.hash(first, second);
  }

  /**
   * Compare to another pair
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   * @param other pair being compared to
   */
  public boolean equals(Object other) {
    if (other instanceof Pair) {
      @SuppressWarnings("rawtypes") Pair otherPair = (Pair) other;
      return ((this.first == otherPair.first
          || (this.first != null && otherPair.first != null && this.first.equals(otherPair.first)))
          && (this.second == otherPair.second || (this.second != null && otherPair.second != null
              && this.second.equals(otherPair.second))));
    }
    return false;
  }

  /**
   * Convert to string
   * 
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return "(" + first + ", " + second + ")";
  }

  // Getters

  /**
   * Get first object in pair
   * 
   * @return first object in pair
   */
  public A first() {
    return first;
  }

  /**
   * Get second object in pair
   * 
   * @return second object in pair
   */
  public B second() {
    return second;
  }

  /** check if any of the two values is not null
   * @return true when one of the entries is not null
   */
  public boolean anyIsNotNull() {
    return first()!=null || second()!=null;
  }

  /** check if both values are not null
   * @return true when both entries are not null
   */  
  public boolean bothNotNull() {
    return first()!=null && second()!=null;
  }

  /**
   * @return true when exactly one of the two is not null
   */
  public boolean isExactlyOneNonNull() {
    return anyIsNotNull() && !bothNotNull();
  }
  
  /**
   * @return earliest entry (first before second) that is nonNull, when both are null, null is returned
   */
  public Object getEarliestNonNull() {
    if(first()!=null) {
      return first();
    }else {
      return second();
    }
  }  

}
