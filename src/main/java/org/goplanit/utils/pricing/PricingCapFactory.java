package org.goplanit.utils.pricing;

import org.goplanit.utils.id.ManagedIdEntityFactory;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.network.layer.macroscopic.tollroad.PricedModeSelection;
import org.goplanit.utils.problem.Problems;

import java.util.List;

/**
 * Registers caps on the caps of a network pricing, once their checks pass
 * <p>
 * A cap naming modes its schemes do not charge loses those modes, and one left without modes is refused; these
 * problems are returned with the cap, for the caller to log once, as the scheme factories do.
 * </p>
 *
 * @author markr
 */
public interface PricingCapFactory extends ManagedIdEntityFactory<PricingCap> {

  /**
   * Create a cap, check it, and register it unless a problem refuses it
   *
   * @param schemes the schemes covered, at least one
   * @param amountInCurrency the most paid over the period, null when the cap counts uses
   * @param activateAboveCount the number of uses after which the schemes charge nothing, null when the cap gives an
   *     amount; with neither, 1
   * @param period the period counted over
   * @param modes the modes capped
   * @return the cap, null when a problem refuses it, and the problems found
   * @throws IllegalArgumentException when the cap covers no scheme, gives both an amount and a count, or counts per use
   */
  public abstract Pair<PricingCap, Problems<PricingProblemKind>> registerNew(
      List<PricingScheme> schemes,
      Double amountInCurrency,
      Integer activateAboveCount,
      PricingPeriod period,
      PricedModeSelection modes);
}
