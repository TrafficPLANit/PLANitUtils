package org.goplanit.utils.pricing;

import java.io.Serializable;

/**
 * Amounts a scheme charges, of one of two kinds: a start per use and a rate per km, with price rules per section, see
 * {@link StartWithPerKmPriceRule}, or an amount per pair of gates, see {@link FromToGatesPriceRule}
 * <p>
 * The {@code <price>} of a pricing: on WestConnex a start of 1.73 per use and 0.64 per km; on a closed motorway an
 * amount per pair of entry and exit gate. A mode or location scheme prices by the first kind and an entry-exit scheme
 * by the second, never both, so this type holds no amounts of its own: it is what a pricing, its time bands and its
 * overrides hold, whichever kind their scheme uses. It only says whether it gives any amount at all, as an override
 * giving a price rule with none gives nothing. A price rule in a time band leaves out what it does not change, which
 * then comes from the base price rule; the point price rule for a mode at a moment, see {@link
 * SchemePricing#getPointPriceRule}, is given with everything filled in.
 * </p>
 *
 * @author markr
 */
public interface PriceRule extends Serializable {

  /**
   * Whether the price rule leaves every amount out, so it changes nothing of the price rule it would fill in
   *
   * @return true when it gives no amount
   */
  public abstract boolean givesNoAmount();
}
