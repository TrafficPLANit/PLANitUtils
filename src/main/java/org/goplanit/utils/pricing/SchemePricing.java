package org.goplanit.utils.pricing;

import org.goplanit.utils.mode.Mode;

import java.io.Serializable;
import java.util.List;
import org.goplanit.utils.time.TimeInWeekWithDayType;

/**
 * What a scheme charges: its base price rule, overrides for some modes, time bands, distance bands and minimums, asked
 * for the point price rule for a mode at a moment
 * <p>
 * The {@code <pricing>} of a scheme. A point price rule is the amounts in force at one point of a use: one moment and
 * one distance travelled. A route search asks for it as it goes, giving the distance it has travelled on the scheme so
 * far, and sums the prices along a path without knowing about bands or overrides. What a whole use costs, with its
 * minimum and caps, is more than a point price rule can say. The caps of a scheme are not here: they are held by the
 * network pricing, as a cap may cover several schemes. Its price rules, overrides and time bands are of its scheme's
 * kind of price rule: a mode or location scheme's pricing is a {@code SchemePricing<StartWithPerKmPriceRule>}.
 * </p>
 *
 * @param <P> the kind of price rule, {@link StartWithPerKmPriceRule} or {@link FromToGatesPriceRule}
 * @author markr
 */
public interface SchemePricing<P extends PriceRule<P>> extends Serializable {

  /**
   * The point price rule for a mode at a moment: the amounts in force there, with the distance band picked by the
   * distance already travelled on the scheme in its distance bands' period
   * <p>
   * The time band whose entries cover the moment, if any; its price rule, with what it leaves out taken from the base
   * price rule; the mode's override of the band, else its override of the base, an amount override replacing the
   * amounts it sets and a factor multiplying the whole price rule; then the factor of the distance band holding the
   * distance travelled. For example a truck on a bridge at 07:00 on a Tuesday pays the peak band's 4.41 times the base
   * override's factor of 3. A mode for which neither the band nor the base gives a price rule pays nothing. The
   * minimums and caps are not applied: they are for a whole use.
   * </p>
   *
   * @param mode priced
   * @param moment priced at
   * @param distanceTravelledForBinSelectionKm the distance travelled on the scheme in its distance bands' period
   *     before this moment, which picks the distance band
   * @return the price rule, with every amount it charges filled in
   */
  public abstract P getPointPriceRule(
      Mode mode,
      TimeInWeekWithDayType moment,
      double distanceTravelledForBinSelectionKm);

  /**
   * The price rule as the scheme gives it, before its time bands, price overrides and distance bands: the start charged
   * once per use, the rate per km, and the price rules of its sections and the amounts of its gate pairs, without the
   * minimums, which apply to what a whole use costs. Outside every time band it is the price rule in force, and inside
   * a band it gives each amount the band's price rule leaves out; the overrides for a mode and the distance bands then
   * change it, so the price rule for a mode at a moment, {@link #getPointPriceRule}, can differ from it
   *
   * @return the price rule as given, null when there is none, so nothing is charged outside the bands unless an
   *     override
   *     gives amounts of its own
   */
  public abstract P getBasePriceRule();

  /**
   * The overrides of the base price rule if any
   *
   * @return unmodifiable list of the overrides, empty when there are none
   */
  public abstract List<PriceOverride<P>> getPriceOverrides();

  /**
   * The time bands, if any
   *
   * @return unmodifiable list of the time bands, empty when there are none
   */
  public abstract List<PricingTimeBand<P>> getTimeBands();

  /**
   * The distance bands, for example for a search crossing a band edge inside a link segment
   *
   * @return unmodifiable list of the distance bands, empty when there are none
   */
  public abstract List<PricingDistanceBand> getDistanceBands();

  /**
   * The minimums of one use, each the least a whole use costs, for some modes. Neither the base price rule nor the
   * point price rule, {@link #getPointPriceRule}, includes them, as they apply to what a whole use costs
   *
   * @return unmodifiable list of the minimums, empty when there are none
   */
  public abstract List<ModesMinimumPrice> getModesMinimumPrices();

  /**
   * The time band covering a moment; the bands of a scheme never overlap, so there is at most one
   *
   * @param moment to look up
   * @return the band, null when the moment lies outside every band
   */
  public default PricingTimeBand<P> findTimeBand(TimeInWeekWithDayType moment) {
    return getTimeBands().stream()
        .filter(band -> band.covers(moment))
        .findFirst()
        .orElse(null);
  }

  /**
   * The override of the base price rule for a mode
   *
   * @param mode to find
   * @return the first override applying to the mode, null when none does
   */
  public default PriceOverride<P> findPriceOverride(Mode mode) {
    return PriceOverride.findApplyingTo(getPriceOverrides(), mode);
  }

  /**
   * The distance band factor at a distance travelled on the scheme in its distance bands' period: that of the band
   * holding it, as each band gives 1 outside itself
   *
   * @param distanceTravelledForBinSelectionKm the distance travelled, which picks the distance band
   * @return the distance band factor, 1 outside every distance band
   */
  public default double computeDistanceBandFactor(double distanceTravelledForBinSelectionKm) {
    return getDistanceBands().stream()
        .mapToDouble(band -> band.computeDistanceBandFactor(distanceTravelledForBinSelectionKm))
        .reduce(1, (factor, other) -> factor * other);
  }

  /**
   * The km of a use, each multiplied by the factor of the distance band it falls in, counting on from the distance
   * already travelled; a km outside every band counts once
   * <p>
   * A distance band edge may lie inside a use, such as the first 20 km of a day free: a use of 30 km after 15 km has 5
   * km free and 25 km at the full rate, 25 km in all here.
   * </p>
   *
   * @param distanceTravelledForBinSelectionKm the distance travelled before the use, in the distance bands' period
   * @param useDistanceKm the distance of the use
   * @return the km of the use times their distance band factors
   */
  public default double computeKmTimesDistanceBandFactors(
      double distanceTravelledForBinSelectionKm,
      double useDistanceKm) {
    if (useDistanceKm <= 0) {
      return 0;
    }
    double kmInBands = 0;
    double factoredKmInBands = 0;
    for (var band : getDistanceBands()) {
      double km = band.findKmInside(distanceTravelledForBinSelectionKm, useDistanceKm);
      kmInBands += km;
      factoredKmInBands += km * band.getDistanceBandFactor();
    }
    return factoredKmInBands + (useDistanceKm - kmInBands);
  }

  /**
   * The least one use costs a mode: the largest of the minimums applying to it, as each says a use costs at least that
   *
   * @param mode one the scheme charges
   * @return the minimum price, 0 when none applies to the mode
   */
  public default double findModesMinimumPrice(Mode mode) {
    return getModesMinimumPrices().stream()
        .filter(minimum -> minimum.appliesTo(mode))
        .mapToDouble(ModesMinimumPrice::getAmountInCurrency)
        .max()
        .orElse(0);
  }
}
