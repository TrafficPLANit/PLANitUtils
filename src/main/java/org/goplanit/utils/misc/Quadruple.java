package org.goplanit.utils.misc;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * Custom quadruple class.
 *
 * @author markr
 *
 * @param <A> first object
 * @param <B> second object
 * @param <C> third object
 * @param <D> third object
 */
public class Quadruple<A, B, C, D> {

  /**
   * The first object
   */
  protected final A first;

  /**
   * The second object
   */
  protected final B second;

  /**
   * The third object
   */
  protected final C third;

  /**
   * The fourth object
   */
  protected final D fourth;

  /**
   * Constructor
   *
   * @param first first object
   * @param second second object
   * @param third third object
   * @param fourth fourth object
   */
  protected Quadruple(A first, B second, C third, D fourth) {
    super();
    this.first = first;
    this.second = second;
    this.third = third;
    this.fourth = fourth;
  }
  
  /** Factory method
   * 
   * @param <A> type of valueA
   * @param <B> type of valueB
   * @param <C> type of valueC
   * @param <D> type of valueD
   * 
   * @param valueA first
   * @param valueB second
   * @param valueC first
   * @param valueD second
   * @return new quadruple
   */
  public static <A,B,C,D> Quadruple<A, B, C, D> of(A valueA, B valueB, C valueC, D valueD) {
    return new Quadruple<>(valueA, valueB, valueC, valueD);
  }  
  

  /**
   * @see Object#hashCode()
   * 
   * @return hashCode for this entity
   */
  public int hashCode() {
    return Objects.hash(first, second, third, fourth);
  }

  /**
   * Compare to another pair
   * 
   * @see Object#equals(Object)
   * @param other pair being compared to
   */
  public boolean equals(Object other) {
    if (other instanceof Quadruple) {
      @SuppressWarnings("rawtypes") Quadruple otherQ = (Quadruple) other;
      return ((this.first == otherQ.first || (this.first != null && otherQ.first != null && this.first.equals(otherQ.first)))
          && (this.second == otherQ.second || (this.second != null && otherQ.second != null && this.second.equals(otherQ.second)))
          && (this.third == otherQ.third || (this.third != null && otherQ.third != null && this.third.equals(otherQ.third)))
          && (this.fourth == otherQ.fourth || (this.fourth != null && otherQ.fourth != null && this.fourth.equals(otherQ.fourth))));
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
    return "(" + first + ", " + second + ", " + third + ", " + fourth + ")";
  }
  
  /** shallow copy of this pair
   * @return shallow copy
   */
  public Quadruple<A,B, C, D> copy(){
    return Quadruple.of(first, second, third, fourth);
  }

  // Getters

  /**
   * Get first object
   * 
   * @return first object
   */
  public A first() {
    return first;
  }

  /**
   * Get second object
   * 
   * @return second object
   */
  public B second() {
    return second;
  }

  /**
   * Get third object
   *
   * @return third object
   */
  public C third() {
    return third;
  }

  /**
   * Get fourth object
   *
   * @return fourth object
   */
  public D fourth() {
    return fourth;
  }

  /** check if any of the  values is not null
   * @return true when one of the entries is not null
   */
  public boolean anyIsNotNull() {
    return (first()!=null || second()!=null || third()!=null || fourth()!=null);
  }

  /** check if all values are not null
   * @return true when both entries are not null
   */  
  public boolean allNotNull() {
    return first()!=null && second()!=null && third()!=null && fourth()!=null ;
  }

  /** check if any values are null
   * @return true when any are null, false otherwise
   */  
  public boolean anyIsNull() {
    return !allNotNull();
  }

  /** Apply consumer to all entries. Throws ClassCastException when pair contains entries not compatible with type parameter of consumer
   * 
   * @param <T> consumer type assumed to be compatible with all  entries
   * @param consumer to apply
   */
  @SuppressWarnings("unchecked")
  public <T> void all(Consumer<T> consumer) {
    consumer.accept( (T) first);
    consumer.accept( (T) second);
    consumer.accept( (T) third);
    consumer.accept( (T) fourth);
  }  
   
}
