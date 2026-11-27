package org.goplanit.utils.pricing;

import org.goplanit.utils.misc.binning.Bin;

import java.io.Serializable;

/**
 * A distance band of a scheme: while the distance travelled on the scheme in a period lies in its range, everything
 * the scheme charges is multiplied by its factor
 * <p>
 * For example a charge whose first 20 km a day are free, factor 0 from 0 to 20 km per day, and a quarter less beyond
 * 200 km a day, factor 0.75 from 200 km. Outside every band the factor is 1. A scheme's distance bands do not overlap
 * and count over one period.
 * </p>
 * <p>
 * A distance band cannot work out what a mode pays on its own: its factor multiplies the point price rule in force,
 * which the scheme's pricing works out from its base price rule, time bands and overrides, and the distance travelled
 * in its period is kept by whoever asks, as it has no date. So ask the pricing, {@link SchemePricing#getPointPriceRule}
 * for one point or the network pricing's cumulative price for a use; a band answers only what is its own: whether it
 * holds a distance, its factor there, and the km of a use inside it.
 * </p>
 *
 * @author markr
 */
public interface PricingDistanceBand extends Serializable {

  /**
   * The period the distance is counted over
   *
   * @return use, day, week or month
   */
  public abstract PricingPeriod getPeriod();

  /**
   * The range of distance travelled on the scheme in the period, in km, from its lower bound up to its upper bound
   *
   * @return the range, its upper bound {@code Double.POSITIVE_INFINITY} when it has no end
   */
  public abstract Bin<Double> getRange();

  /**
   * The factor multiplying what the scheme charges in the range
   *
   * @return the factor
   */
  public abstract double getDistanceBandFactor();

  /**
   * Whether a distance travelled in the band's period lies in its range
   *
   * @param distanceTravelledForBinSelectionKm the distance travelled on the scheme in the band's period
   * @return true when the band holds it
   */
  public default boolean contains(double distanceTravelledForBinSelectionKm) {
    return getRange().contains(distanceTravelledForBinSelectionKm);
  }

  /**
   * The distance band factor at a distance travelled in the band's period: its factor inside its range, 1 outside, so
   * the factors of a pricing's bands, which do not overlap, multiply to that of the band holding the distance
   *
   * @param distanceTravelledForBinSelectionKm the distance travelled on the scheme in the band's period
   * @return the distance band factor, 1 when the band does not hold the distance
   */
  public default double computeDistanceBandFactor(double distanceTravelledForBinSelectionKm) {
    return contains(distanceTravelledForBinSelectionKm) ? getDistanceBandFactor() : 1;
  }

  /**
   * The km of a use that lie inside the band, counting on from the distance already travelled in its period, such as 5
   * km of a use of 30 km after 15 km in a band of the first 20 km
   *
   * @param distanceTravelledForBinSelectionKm the distance travelled on the scheme in the band's period before the use
   * @param useDistanceKm the distance of the use
   * @return the km inside the band, 0 when none
   */
  public default double findKmInside(double distanceTravelledForBinSelectionKm, double useDistanceKm) {
    return useDistanceKm <= 0 ? 0 : getRange().findOverlapLength(Bin.of(distanceTravelledForBinSelectionKm,
        distanceTravelledForBinSelectionKm + useDistanceKm, true, false, null));
  }
}
