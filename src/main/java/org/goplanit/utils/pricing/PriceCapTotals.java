package org.goplanit.utils.pricing;

import org.goplanit.utils.mode.Mode;

import java.io.Serializable;

/**
 * Per cap over a period, what one traveller has paid and how many uses it has made in the cap's current period
 * <p>
 * A cumulative price of a use applies the caps over an hour, day, week or month from these totals: with a day cap of
 * 10.00 and 9.00 paid that day, a use costing 3.00 pays 1.00. A use is priced from the totals so far, and then added to
 * them by the caller, as pricing a use changes nothing. A time in the week has no date, so the caller says when a
 * period starts, such as a new day, which clears the caps of that period.
 * </p>
 *
 * @author markr
 */
public interface PriceCapTotals extends Serializable {

  /**
   * Add to the totals of every cap over a period covering a scheme and mode: the cumulative price to the current total,
   * and, when asked, one to the use count. A use is added with its use counted; a payment that is no use of its own,
   * such as a later part of a use already counted, is added without
   *
   * @param scheme used
   * @param mode used
   * @param cumulativePrice what was paid
   * @param incrementUseCount whether to add one to the use count of each cap
   */
  public abstract void addToTotal(PricingScheme scheme, Mode mode, double cumulativePrice, boolean incrementUseCount);

  /**
   * A period starts, such as a new day: clears the totals of the caps over that period and over the periods each of
   * its starts begins too, a new day beginning a new hour, a new week or month a new day and hour
   *
   * @param period that starts: hour, day, week or month
   */
  public abstract void startPeriod(PricingPeriod period);

  /**
   * What the traveller has paid in total in a cap's current period on the schemes it covers
   *
   * @param cap to look up
   * @return the current total in the pricing's currency, 0 when nothing yet
   */
  public abstract double getCurrentTotal(PriceCap cap);

  /**
   * How many uses the traveller has made in a cap's current period of the schemes it covers
   *
   * @param cap to look up
   * @return the use count, 0 when none yet
   */
  public abstract int getUseCount(PriceCap cap);
}
