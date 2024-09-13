package org.goplanit.utils.misc;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Custom Triple class similar to Pair, only with three entries.
 * value
 * 
 * @author markr
 *
 * @param <A> first object in triple
 * @param <B> second object in triple
 * @param <C> second object in triple
 */
public class Triple<A, B, C> {

  /**
   * The first object in this Triple
   */
  protected final A first;

  /**
   * The second object in this Triple
   */
  protected final B second;

  /**
   * The third object in this Triple
   */
  protected final C third;

  /**
   * Constructor
   *
   * @param first first object in triple
   * @param second second object in triple
   * @param third second object in triple
   */
  protected Triple(A first, B second, C third) {
    super();
    this.first = first;
    this.second = second;
    this.third = third;
  }
  
  /** Factory method
   * 
   * @param <A> type of valueA
   * @param <B> type of valueB
   * @param <C> type of valueC
   * 
   * @param valueA first
   * @param valueB second
   * @param valueC third
   * @return new triple
   */
  public static <A,B,C> Triple<A, B, C> of(A valueA, B valueB, C valueC) {
    return new Triple<>(valueA, valueB, valueC);
  }

  /** Factory method
   *
   * @param <A> type of valueA
   * @param <B> type of valueB
   * @param <C> type of valueC
   *
   * @param valueAb first and second
   * @param valueC third
   * @return new triple
   */
  public static <A,B,C> Triple<A, B, C> of(Pair<A,B> valueAb, C valueC) {
    return new Triple<>(valueAb.first(), valueAb.second(), valueC);
  }

  /** Factory method
   *
   * @param <A> type of valueA
   * @param <B> type of valueB
   * @param <C> type of valueC
   *
   * @param valueA first
   * @param valueBc second and third
   * @return new triple
   */
  public static <A,B,C> Triple<A, B, C> of(A valueA, Pair<B,C> valueBc) {
    return new Triple<>(valueA, valueBc.first(), valueBc.second());
  }


  /**
   * @see Object#hashCode()
   * 
   * @return hashCode for this entity
   */
  public int hashCode() {
    return Objects.hash(first, second, third);
  }

  /**
   * Check equality to another triple
   * 
   * @see Object#equals(Object)
   * @param other triple being compared to
   */
  public boolean equals(Object other) {
    if (other instanceof Triple) {
      @SuppressWarnings("rawtypes") Triple otherPair = (Triple) other;
      return (
              (this.first == otherPair.first || (this.first != null && otherPair.first != null && this.first.equals(otherPair.first))) &&
                      (this.second == otherPair.second || (this.second != null && otherPair.second != null && this.second.equals(otherPair.second))) &&
                      (this.third == otherPair.third || (this.third != null && otherPair.third != null && this.third.equals(otherPair.third)))
      );
    }
    return false;
  }

  /**
   * Convert to string
   * 
   * @see Object#toString()
   */
  @Override
  public String toString() {
    return "(" + first + ", " + second + ", " + third + ")";
  }
  
  /** shallow copy of this triple
   * @return shallow copy
   */
  public Triple<A,B, C> copy(){
    return Triple.of(first, second, third);
  }

  // Getters

  /**
   * Get first object in triple
   * 
   * @return first object in triple
   */
  public A first() {
    return first;
  }

  /**
   * Get second object in triple
   * 
   * @return second object in triple
   */
  public B second() {
    return second;
  }

  /**
   * Get second object in triple
   *
   * @return second object in triple
   */
  public C third() {
    return third;
  }  

  /** check if any of the two values is not null
   * @return true when one of the entries is not null
   */
  public boolean anyIsNotNull() {
    return first()!=null || second()!=null || third()!=null;
  }

  /** check if all values are not null
   * @return true when both entries are not null
   */  
  public boolean allNotNull() {
    return first()!=null && second()!=null && third() != null;
  }

  /** check if any values are null
   * @return true when any are null, false otherwise
   */  
  public boolean anyIsNull() {
    return !allNotNull();
  }

  /**
   * @return earliest entry (first before second) that is nonNull, when both are null, null is returned
   */
  public Object getEarliestNonNull() {
    if(first()!=null) {
      return first();
    }else if(secondNotNull()){
      return second();
    }else {
      return third();
    }
  }

  /** Apply consumer all entries. Throws ClassCastException when triple contains entries not compatible with type parameter of consumer
   * 
   * @param <T> consumer type assumed to be compatible with both triple entries
   * @param tripleEntryConsumer to apply
   */
  @SuppressWarnings("unchecked")
  public <T> void both(Consumer<T> tripleEntryConsumer) {
    tripleEntryConsumer.accept( (T) first);
    tripleEntryConsumer.accept( (T) second);
    tripleEntryConsumer.accept( (T) third);
  }

  /**
   * Check if first is non-null
   * @return true when the case, false otherwise
   */
  public boolean firstNotNull() {
    return first() != null;
  }

  /**
   * Check if second is non-null
   * @return true when the case, false otherwise
   */
  public boolean secondNotNull() {
    return second() != null;
  }

  /**
   * Check if third is non-null
   * @return true when the case, false otherwise
   */
  public boolean thirdNotNull() {
    return third() != null;
  }

  /**
   * Check if entries are not equal
   * @return true when first does not equal second, false otherwise
   */
  public boolean different(){
    return !first().equals(second()) || !second().equals(third());
  }

  /**
   * check all are null
   * @return true when both null, false otherwise
   */
  public boolean allNull() {
    return !firstNotNull() && !secondNotNull() && !thirdNotNull();
  }
}
