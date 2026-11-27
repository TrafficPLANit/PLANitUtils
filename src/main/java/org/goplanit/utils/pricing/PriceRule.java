package org.goplanit.utils.pricing;

import java.io.Serializable;
import java.util.function.UnaryOperator;

/**
 * Amounts a scheme charges, of one of two kinds: a start per use and a rate per km, with price rules per section, see
 * {@link StartWithPerKmPriceRule}, or an amount per pair of gates, see {@link FromToGatesPriceRule}
 * <p>
 * The {@code <price>} of a pricing: on WestConnex a start of 1.73 per use and 0.64 per km; on a closed motorway an
 * amount per pair of entry and exit gate. A mode or location scheme prices by the first kind and an entry-exit scheme
 * by the second, never both, so this type holds no amounts of its own: it is what a pricing, its time bands and its
 * overrides hold, whichever kind their scheme uses. It says whether it gives any amount at all, as an override giving a
 * price rule with none gives nothing, and it makes the price rules worked out from it, of its own kind: multiplied by a
 * factor, filled in from another, with its left out amounts given, or referring to copied sections and gates. A price
 * rule in a time band leaves out what it does not change, which then comes from the base price rule; the point price
 * rule for a mode at a moment, see {@link SchemePricing#getPointPriceRule}, is given with everything filled in.
 * </p>
 *
 * @param <P> the price rule's own kind, which the price rules it makes are of
 * @author markr
 */
public interface PriceRule<P extends PriceRule<P>> extends Serializable {

  /**
   * Whether the price rule leaves every amount out, so it changes nothing of the price rule it would fill in
   *
   * @return true when it gives no amount
   */
  public abstract boolean givesNoAmount();

  /**
   * Create this price rule with every amount multiplied by a factor, those left out staying left out
   *
   * @param factor to multiply by
   * @return the new price rule
   */
  public abstract P createMultipliedWith(double factor);

  /**
   * Create this price rule filled in from another: each amount it leaves out is taken from the other
   *
   * @param fallback to fill in from
   * @return the new price rule
   */
  public abstract P createFilledInFrom(P fallback);

  /**
   * Create this price rule with what it leaves out given, so a caller reads no gaps
   *
   * @return the new price rule, holding no amount left out
   */
  public abstract P createWithLeftOutAmountsGiven();

  /**
   * Create this price rule referring to other sections and gates, such as their copies in a copy of the pricing; a
   * section or gate mapped to null drops what refers to it
   *
   * @param sectionMapping from each section to the one to refer to, null to drop it
   * @param gateMapping from each gate to the one to refer to, null to drop it
   * @return the new price rule
   */
  public abstract P createWithSectionsAndGatesMapped(
      UnaryOperator<PricingSchemeSection> sectionMapping,
      UnaryOperator<PricingSchemeGate> gateMapping);
}
