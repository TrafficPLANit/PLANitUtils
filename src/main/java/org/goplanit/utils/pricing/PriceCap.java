package org.goplanit.utils.pricing;

import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.id.ManagedId;
import org.goplanit.utils.network.layer.macroscopic.tollroad.PricedModeSelection;

import java.io.Serializable;
import java.util.List;
import org.goplanit.utils.mode.Mode;

/**
 * A cap on what one or more schemes charge over a period: the most paid, or the number of uses after which they charge
 * nothing
 * <p>
 * For example 12.25 per use on WestConnex for cars, one charge an hour on a cordon, or 60 a week across all toll roads
 * of a city. Caps are not in time bands and factors do not scale them, and every cap covering a mode applies at once.
 * They are held by the network pricing in one container, whether they cover one scheme or several, so a later
 * assignment keeps a total per cap.
 * </p>
 *
 * @author markr
 */
public interface PriceCap extends ExternalIdAble, ManagedId, Serializable {

  /** id class for generating ids */
  public static final Class<PriceCap> CAP_ID_CLASS = PriceCap.class;

  /**
   * {@inheritDoc}
   */
  @Override
  public default Class<? extends PriceCap> getIdClass() {
    return CAP_ID_CLASS;
  }

  /**
   * The schemes the cap covers, referenced, not owned: the network pricing's schemes of each kind own them
   *
   * @return unmodifiable list of the schemes, at least one
   */
  public abstract List<PricingScheme> getSchemes();

  /**
   * The most paid over the period on the schemes covered
   *
   * @return the amount, null when the cap counts uses instead
   */
  public abstract Double getAmountInCurrency();

  /**
   * The number of uses in the period that are charged, after which the schemes covered charge nothing: the uses above
   * the count are free, the use reaching it is not
   * <p>
   * For example 3 over a day: the first, second and third uses of a day are charged, the fourth and later are free. A
   * cap giving neither an amount nor a count has 1, one charged use per period, such as a congestion charge paid once a
   * day.
   * </p>
   *
   * @return the count, null when the cap gives an amount instead
   */
  public abstract Integer getActivateAboveCount();

  /**
   * The period the cap counts over
   *
   * @return the period
   */
  public abstract PricingPeriod getPeriod();

  /**
   * The modes capped
   *
   * @return the priced mode selection, unspecified for every mode the schemes charge
   */
  public abstract PricedModeSelection getPricedModeSelection();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract PriceCap shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract PriceCap deepClone();

  /**
   * Whether the cap covers a mode its schemes charge: always when its modes are not given, else when it names the mode
   * or does not exempt it
   *
   * @param mode a mode the schemes charge
   * @return true when the cap applies to the mode
   */
  public default boolean appliesTo(Mode mode) {
    return getPricedModeSelection().appliesTo(mode);
  }

  /**
   * Whether the cap is per use, so it caps each use on its own and needs no traveller's totals
   *
   * @return true when its period is one use
   */
  public default boolean isPerUse() {
    return getPeriod() == PricingPeriod.USE;
  }

  /**
   * The capped price of a use: a cap with an amount leaves at most that amount less what the traveller has paid in
   * the cap's current period, and a cap counting uses leaves nothing once the use count reaches its count, such as a
   * day cap of 10.00 with 9.00 paid leaving 1.00 of a use costing 3.00
   *
   * @param cumulativePrice the price of the use so far
   * @param capTotals the traveller's totals per cap, null to count the use as the first in the cap's period
   * @return the capped price, the price itself when the cap leaves all of it
   */
  public default double computeCappedPrice(double cumulativePrice, PriceCapTotals capTotals) {
    if (getAmountInCurrency() != null) {
      double currentTotal = capTotals == null ? 0 : capTotals.getCurrentTotal(this);
      return Math.min(cumulativePrice, Math.max(0, getAmountInCurrency() - currentTotal));
    }
    return capTotals != null && capTotals.getUseCount(this) >= getActivateAboveCount() ? 0 : cumulativePrice;
  }
}
