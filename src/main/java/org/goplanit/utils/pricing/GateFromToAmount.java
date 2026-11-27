package org.goplanit.utils.pricing;

import java.io.Serializable;

/**
 * The amount for a use of an entry-exit scheme from one gate to another, in that direction
 * <p>
 * The cells of an operator's table for a closed motorway: from the north interchange to the south one 4.90. Amounts
 * may differ by direction, so each direction is a pair of its own.
 * </p>
 *
 * @author markr
 */
public interface GateFromToAmount extends Serializable {

  /**
   * The gate the use enters at
   *
   * @return the gate
   */
  public abstract PricingSchemeGate getFromGate();

  /**
   * The gate the use leaves at
   *
   * @return the gate
   */
  public abstract PricingSchemeGate getToGate();

  /**
   * The amount for the use
   *
   * @return the amount
   */
  public abstract double getAmountInCurrency();
}
