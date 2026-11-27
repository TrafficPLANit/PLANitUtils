package org.goplanit.utils.pricing;

import org.goplanit.utils.network.layer.macroscopic.tollroad.PricedModeSelection;

import java.io.Serializable;
import org.goplanit.utils.mode.Mode;

/**
 * The least one use of a scheme costs, for some modes
 * <p>
 * For example a toll road where every use costs at least 2.00, so a short trip of 1.76 pays 2.00. A start is added to
 * every use; a minimum only lifts the short ones.
 * </p>
 *
 * @author markr
 */
public interface ModesMinimumPrice extends Serializable {

  /**
   * The least one use costs
   *
   * @return the amount
   */
  public abstract double getAmountInCurrency();

  /**
   * The modes the minimum is for
   *
   * @return the priced mode selection, unspecified for every mode the scheme charges
   */
  public abstract PricedModeSelection getPricedModeSelection();

  /**
   * Whether the minimum covers a mode its scheme charges: always when its modes are not given, else when it names the
   * mode or does not exempt it
   *
   * @param mode a mode the scheme charges
   * @return true when the minimum applies to the mode
   */
  public default boolean appliesTo(Mode mode) {
    return getPricedModeSelection().appliesTo(mode);
  }
}
