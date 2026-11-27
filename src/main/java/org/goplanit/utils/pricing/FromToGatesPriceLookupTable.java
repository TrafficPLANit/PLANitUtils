package org.goplanit.utils.pricing;

import org.goplanit.utils.mode.Mode;

/**
 * The lookup table of an entry-exit scheme: the amount per pair of gates
 * <p>
 * For example a truck from the north interchange to the south one of a closed motorway in the morning band pays the
 * amount the table holds for that mode, band and pair.
 * </p>
 *
 * @author markr
 */
public interface FromToGatesPriceLookupTable extends PriceLookupTable {

  /**
   * The amount a mode pays for a use from one gate to another
   *
   * @param mode priced
   * @param band index, see {@link #findBandIndex}
   * @param distanceBand index, see {@link #findDistanceBandIndex}
   * @param fromGate entered, one of the scheme's gates
   * @param toGate left, one of the scheme's gates
   * @return the amount, 0 when the scheme gives none for the pair or does not charge the mode
   */
  public abstract double getAmountInCurrency(
      Mode mode,
      int band,
      int distanceBand,
      PricingSchemeGate fromGate,
      PricingSchemeGate toGate);
}
