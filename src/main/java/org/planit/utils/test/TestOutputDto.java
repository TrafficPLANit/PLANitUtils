package org.planit.utils.test;

/**
 * Data Transfer Object used to store results in unit testing for PlanItIO
 * 
 * @author gman6028
 *
 * @param <A> first property to store results (stores MemoryOutputFormatter in PlanItIO)
 * @param <B> second property to store results (stores CustomPlanItProject in PlanItIO)
 * @param <C> third property to store results (stores InputBuilderListener in PlanItIO)
 */
public class TestOutputDto<A, B, C> {

  private final A a;

  private final B b;

  private final C c;

  public TestOutputDto(A a, B b, C c) {
    super();
    this.a = a;
    this.b = b;
    this.c = c;
  }

  public A getA() {
    return a;
  }

  public B getB() {
    return b;
  }

  public C getC() {
    return c;
  }

}
