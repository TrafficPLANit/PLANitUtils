package org.planit.utils.function;

import org.ojalgo.function.NullaryFunction;

/**
 * Lightweight Nullaryfunction implementation to supply doubles in the form of a {@code NullaryFunction<Double>} compatible with
 * OjAlgo
 * 
 * @author markr
 */
public class NullaryDoubleSupplier implements NullaryFunction<Double> {

  private final double theDouble;

  /** Create supplier that delivers 1 value */
  public static NullaryDoubleSupplier ONE = new NullaryDoubleSupplier(1);

  /**
   * Constructor for double supplier compatible with {@code Nullaryfunction<Double>} of OjAlgo
   * 
   * @param theDouble to supply
   */
  public NullaryDoubleSupplier(final double theDouble) {
      super();
      this.theDouble = theDouble;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double doubleValue() {
    return theDouble;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Double invoke() {
    return theDouble;
  }

}