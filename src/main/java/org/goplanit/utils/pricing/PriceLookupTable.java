package org.goplanit.utils.pricing;

import org.goplanit.utils.mode.Mode;

import java.io.Serializable;
import org.goplanit.utils.time.TimeInWeekWithDayType;

/**
 * One scheme's price rules worked out in advance, for an assignment or a route search asking for them many times
 * <p>
 * {@link SchemePricing#getPointPriceRule} combines the parts of a pricing on every call. An assignment asks per link
 * segment, mode and iteration, so the network pricing builds this table once, when asked, with every amount already
 * combined per mode, time band and distance band. A caller first finds the band and the distance band, once per moment
 * and distance, then reads the amounts of its scheme's kind through {@link StartWithPerKmPriceLookupTable} or
 * {@link FromToGatesPriceLookupTable}. A table holds the prices as they were when it was built: after a change to the
 * pricing or the network a new one is built. A mode the scheme does not charge pays nothing.
 * </p>
 *
 * @author markr
 */
public interface PriceLookupTable extends Serializable {

  /**
   * The time band whose entries cover a moment
   *
   * @param moment to look up
   * @return the band's index, the last index when the moment lies outside every band
   */
  public abstract int findTimeBandIndex(TimeInWeekWithDayType moment);

  /**
   * The distance band holding a distance travelled on the scheme in its distance bands' period
   *
   * @param distanceTravelledForBinSelectionKm the distance travelled, which picks the distance band
   * @return the distance band's index, the last index when the distance lies outside every distance band
   */
  public abstract int findDistanceBandIndex(double distanceTravelledForBinSelectionKm);

  /**
   * The least one use costs a mode, to apply at the end of a use
   *
   * @param mode to look up
   * @return the minimum, 0 when there is none or the scheme does not charge the mode
   */
  public abstract double getModesMinimumPrice(Mode mode);

  /**
   * The most one use costs a mode, the lowest of the caps per use covering it, to apply at the end of a use
   *
   * @param mode to look up
   * @return the price cap per use, {@code Double.POSITIVE_INFINITY} when there is none or the scheme does not charge
   *     the mode
   */
  public abstract double getPriceCapPerUse(Mode mode);
}
