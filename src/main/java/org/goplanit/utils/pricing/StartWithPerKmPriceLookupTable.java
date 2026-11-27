package org.goplanit.utils.pricing;

import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.physical.LinkSegment;

/**
 * The lookup table of a mode or location scheme: the start per use, and by link segment the rate per km and the start
 * of the section it lies in
 * <p>
 * The table finds a link segment's section itself, so a route search never needs to know sections: on WestConnex at
 * 08:00 a car pays a start of 1.73 per use and 0.64 per km on a link segment outside every section.
 * </p>
 *
 * @author markr
 */
public interface StartWithPerKmPriceLookupTable extends PriceLookupTable {

  /**
   * The start a mode pays once per use
   *
   * @param mode priced
   * @param band index, see {@link #findBandIndex}
   * @param distanceBand index, see {@link #findDistanceBandIndex}
   * @return the start, 0 when the scheme does not charge the mode
   */
  public abstract double getStartAmountInCurrency(Mode mode, int band, int distanceBand);

  /**
   * The rate per km a mode pays on a link segment: that of the section it lies in, else the scheme's
   *
   * @param mode priced
   * @param band index, see {@link #findBandIndex}
   * @param distanceBand index, see {@link #findDistanceBandIndex}
   * @param linkSegment one of the scheme's priced link segments
   * @return the rate per km, 0 when the scheme does not charge the mode
   */
  public abstract double getPerKmAmountInCurrency(Mode mode, int band, int distanceBand, LinkSegment linkSegment);

  /**
   * The start a mode pays each time it passes the section a link segment lies in
   *
   * @param mode priced
   * @param band index, see {@link #findBandIndex}
   * @param distanceBand index, see {@link #findDistanceBandIndex}
   * @param linkSegment one of the scheme's priced link segments
   * @return the section's start, 0 when the link segment lies in no section or the scheme does not charge the mode
   */
  public abstract double getSectionStartAmountInCurrency(Mode mode, int band, int distanceBand,
      LinkSegment linkSegment);
}
