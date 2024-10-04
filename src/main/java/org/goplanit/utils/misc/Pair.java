package org.goplanit.utils.misc;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Custom pair class similar to C++.
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
    return new Pair<>(valueA, valueB);
  }

  /**
   * Create empty pair of nulls
   * @return null pair
   * @param <A> typeA
   * @param <B> typeB
   */
  public static <A,B> Pair<A,B> empty() {
    return Pair.of(null, null);
  }

  /** check if pair is null or empty
   *
   * @param <A> typeA
   * @param <B> typeB
   * @param pair to check
   * @return true when null or empty, false otherwise
   */
  public static <A,B> boolean isNullOrEmpty(Pair<A, B> pair) {
    return pair == null || pair.bothNull();
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
   * Check equality to another pair
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
  @Override
  public String toString() {
    return "(" + first + ", " + second + ")";
  }
  
  /** shallow copy of this pair
   * @return shallow copy
   */
  public Pair<A,B> copy(){
    return Pair.of(first, second);
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

  /** check if any values are null
   * @return true when any are null, false otherwise
   */  
  public boolean anyIsNull() {
    return !bothNotNull();
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

  /** Apply consumer to both entries. Throws ClassCastException when pair contains entries not compatible with type parameter of consumer
   * 
   * @param <T> consumer type assumed to be compatible with both pair entries
   * @param pairEntryConsumer to apply
   */
  @SuppressWarnings("unchecked")
  public <T> void both(Consumer<T> pairEntryConsumer) {
    if(first != null){
      pairEntryConsumer.accept( (T) first);
    }
    if(second != null) {
      pairEntryConsumer.accept((T) second);
    }
  }

  /**
   * Check if first is non null
   * @return true when the case, false otherwise
   */
  public boolean firstNotNull() {
    return first() != null;
  }

  /**
   * Check if second is non null
   * @return true when the case, false otherwise
   */
  public boolean secondNotNull() {
    return second() != null;
  }

  /**
   * Check if entries are not equal
   * @return true when first does not equal second, false otherwise
   */
  public boolean different(){
    return !first().equals(second());
  }

  /**
   * check both entries are null
   * @return true when both null, false otherwise
   */
  public boolean bothNull() {
    return first()==null && second() == null;
  }
}
