package org.goplanit.utils.pricing;

import org.goplanit.utils.id.ManagedIdEntityFactory;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.network.layer.macroscopic.tollroad.PricedModeSelection;
import org.goplanit.utils.network.layer.macroscopic.tollroad.TollRoad;
import org.goplanit.utils.problem.Problems;

import java.util.List;

/**
 * Registers location schemes, once their checks pass
 * <p>
 * The one way to make a location scheme, so it is always made on toll roads of the network. In one order: its modes
 * are resolved against its roads' priced modes, what does not fit is dropped, its own rules and the rules across
 * schemes are checked, and it is added unless a problem refuses it. It is returned with its problems, for the caller to
 * log once under the id the user wrote.
 * </p>
 *
 * @author markr
 */
public interface LocationPricingSchemeFactory extends ManagedIdEntityFactory<LocationPricingScheme> {

  /**
   * Create a location scheme, check it, and register it unless a problem refuses it
   *
   * @param name of the scheme, null for none
   * @param modes the modes it charges, as given
   * @param tollRoads the toll roads it charges on, priced as one, at least one
   * @param sections its sections, registered before
   * @param pricing what it charges
   * @return the scheme, null when a problem refuses it, and the problems found
   */
  public abstract Pair<LocationPricingScheme, Problems<PricingProblemKind>> registerNew(
      String name,
      PricedModeSelection modes,
      List<TollRoad> tollRoads,
      List<PricingSchemeSection> sections,
      SchemePricing<StartWithPerKmPriceRule> pricing);
}
