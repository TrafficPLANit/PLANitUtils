package org.goplanit.utils.pricing;

import org.goplanit.utils.id.ManagedIdEntityFactory;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.network.layer.macroscopic.tollroad.PricedModeSelection;
import org.goplanit.utils.network.layer.macroscopic.tollroad.TollRoad;
import org.goplanit.utils.problem.Problems;

import java.util.List;

/**
 * Registers entry-exit schemes, once their checks pass
 * <p>
 * The one way to make an entry-exit scheme, so it is always made on toll roads of the network, with gates. In one
 * order, as for location schemes, and a gate whose entry or exit is not where the scheme's link segments are entered
 * or left is dropped, as a search spotting entries as it goes would never meet it.
 * </p>
 *
 * @author markr
 */
public interface EntryExitPricingSchemeFactory extends ManagedIdEntityFactory<EntryExitPricingScheme> {

  /**
   * Create an entry-exit scheme, check it, and register it unless a problem refuses it
   *
   * @param name of the scheme, null for none
   * @param modes the modes it charges, as given
   * @param tollRoads the toll roads it charges on, priced as one, at least one
   * @param gates its gates, registered before
   * @param pricing what it charges
   * @return the scheme, null when a problem refuses it, and the problems found
   */
  public abstract Pair<EntryExitPricingScheme, Problems<PricingProblemKind>> registerNew(
      String name,
      PricedModeSelection modes,
      List<TollRoad> tollRoads,
      List<PricingSchemeGate> gates,
      SchemePricing<FromToGatesPriceRule> pricing);
}
