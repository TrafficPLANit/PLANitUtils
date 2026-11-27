package org.goplanit.utils.pricing;

import org.goplanit.utils.id.ManagedIdEntityFactory;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.network.layer.macroscopic.tollroad.PricedModeSelection;
import org.goplanit.utils.problem.Problems;

import java.util.List;

/**
 * Registers mode schemes, once their checks pass
 * <p>
 * The one way to make a mode scheme, so it is always made with link segments of its own and never on toll roads. In
 * one order: its modes are resolved, what does not fit is dropped, its own rules and the rules across schemes are
 * checked, and it is added unless a problem refuses it. It is returned with its problems, for the caller to log once
 * under the id the user wrote.
 * </p>
 *
 * @author markr
 */
public interface ModePricingSchemeFactory extends ManagedIdEntityFactory<ModePricingScheme> {

  /**
   * Create a mode scheme, check it, and register it unless a problem refuses it
   *
   * @param name of the scheme, null for none
   * @param modes the modes it charges, as given
   * @param pricedLinkSegments the link segments it charges on, as given
   * @param sections its sections, registered before
   * @param pricing what it charges
   * @return the scheme, null when a problem refuses it, and the problems found
   */
  public abstract Pair<ModePricingScheme, Problems<PricingProblemKind>> registerNew(
      String name,
      PricedModeSelection modes,
      ModePricingSchemeLinkSegmentCoverage pricedLinkSegments,
      List<PricingSchemeSection> sections,
      SchemePricing<StartWithPerKmPriceRule> pricing);
}
