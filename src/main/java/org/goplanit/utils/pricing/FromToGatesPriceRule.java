package org.goplanit.utils.pricing;

import java.util.List;

/**
 * The price rule of an entry-exit scheme: an amount per pair of entry and exit gate
 * <p>
 * For example a closed motorway with a ticket system, 4.90 from the north interchange to the south one. A pair may be
 * left out: in a time band or an override it then comes from the price rule it changes.
 * </p>
 *
 * @author markr
 */
public interface FromToGatesPriceRule extends PriceRule<FromToGatesPriceRule> {

  /**
   * The amounts per pair of gates
   *
   * @return unmodifiable list of the pairs, empty when there are none
   */
  public abstract List<GateFromToAmount> getPricedFromToGates();

  /**
   * The amount for a use from one gate to another, as this price rule gives it
   *
   * @param fromGate entered, compared by identity
   * @param toGate left, compared by identity
   * @return the pair, null when this price rule gives none for it
   */
  public abstract GateFromToAmount findByFromToGate(PricingSchemeGate fromGate, PricingSchemeGate toGate);

  /**
   * {@inheritDoc}
   */
  @Override
  public default boolean givesNoAmount() {
    return getPricedFromToGates().isEmpty();
  }

  /**
   * The amount charged from one gate to another
   *
   * @param fromGate entered at
   * @param toGate left at
   * @return the pair's amount in the pricing's currency, 0 when the rule does not list the pair
   */
  public default double findAmountInCurrency(PricingSchemeGate fromGate, PricingSchemeGate toGate) {
    var gatePair = findByFromToGate(fromGate, toGate);
    return gatePair == null ? 0 : gatePair.getAmountInCurrency();
  }
}
