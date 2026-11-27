package org.goplanit.utils.pricing;

import java.io.Serializable;
import java.util.List;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.time.TimeInWeekWithDayType;

/**
 * A time band of a scheme: during the times its entries give, its price rule and overrides replace what they list of
 * the base, the rest staying as in the base
 * <p>
 * For example a morning and evening peak on weekdays with a start of 4.41, against 2.76 at other times. The entries of
 * a scheme's bands never overlap, so at any moment at most one band applies. A band's price rule and overrides are of
 * its scheme's kind of price rule.
 * </p>
 * <p>
 * A band cannot work out what a mode pays during it on its own. Its price rule leaves out what it does not change,
 * which comes from the base price rule, such as the rate per km of a peak band giving a start only; a mode it has no
 * override for takes the base's override, such as heavy goods paying three times in every band not saying otherwise;
 * and filling in and multiplying are each kind's own. The scheme's pricing holds all of these, so ask it,
 * {@link SchemePricing#getPointPriceRule}; a band answers only what is its own, such as whether it covers a moment
 * and which of its overrides is for a mode.
 * </p>
 *
 * @param <P> the kind of price rule, {@link StartWithPerKmPriceRule} or {@link FromToGatesPriceRule}
 * @author markr
 */
public interface PricingTimeBand<P extends PriceRule> extends Serializable {

  /**
   * The id of the band in the file, unique within its scheme, such as {@code peak}
   *
   * @return the XML id
   */
  public abstract String getXmlId();

  /**
   * When the band applies
   *
   * @return unmodifiable list of the entries, at least one
   */
  public abstract List<PricingTimeBandEntry> getEntries();

  /**
   * The band's price rule, with what it leaves out taken from the base price rule
   *
   * @return the price rule, null when the band gives none
   */
  public abstract P getPriceRule();

  /**
   * The band's overrides of its price rule for some modes
   *
   * @return unmodifiable list of the overrides, empty when there are none
   */
  public abstract List<PriceOverride<P>> getPriceOverrides();

  /**
   * Whether the band covers a moment: one of its entries does
   *
   * @param moment to check
   * @return true when the band covers it
   */
  public default boolean covers(TimeInWeekWithDayType moment) {
    return getEntries().stream().anyMatch(entry -> entry.covers(moment));
  }

  /**
   * The band's override for a mode
   *
   * @param mode to find
   * @return the first override applying to the mode, null when none does
   */
  public default PriceOverride<P> findPriceOverride(Mode mode) {
    return PriceOverride.findApplyingTo(getPriceOverrides(), mode);
  }
}
