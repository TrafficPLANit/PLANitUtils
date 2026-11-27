package org.goplanit.utils.pricing;

import org.goplanit.utils.id.ManagedIdEntityFactory;
import org.goplanit.utils.network.layer.macroscopic.tollroad.PricedModeSelection;

import java.util.List;

/**
 * Registers caps on the caps of a network pricing
 * <p>
 * It makes and registers what it is given. A caller checks first: a cap's modes are limited to those its schemes
 * charge, and a cap whose amount is below a minimum for one of its modes is refused, through the caps' utils in the
 * implementation (the pricing reader does so for each {@code <cap>}).
 * </p>
 *
 * @author markr
 */
public interface PriceCapFactory extends ManagedIdEntityFactory<PriceCap> {

  /**
   * Create a cap and register it on its container
   *
   * @param schemes the schemes covered, at least one
   * @param amountInCurrency the most paid over the period, null when the cap counts uses
   * @param activateAboveCount the number of uses after which the schemes charge nothing, null when the cap gives an
   *     amount; with neither, 1
   * @param period the period counted over
   * @param modes the modes capped, among those its schemes charge
   * @return the registered cap
   * @throws IllegalArgumentException when the cap covers no scheme, gives both an amount and a count, or counts per use
   */
  public abstract PriceCap registerNew(
      List<PricingScheme> schemes,
      Double amountInCurrency,
      Integer activateAboveCount,
      PricingPeriod period,
      PricedModeSelection modes);
}
