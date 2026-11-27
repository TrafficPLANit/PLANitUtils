package org.goplanit.utils.pricing;

import org.goplanit.utils.network.layer.macroscopic.tollroad.TollRoad;

import java.util.List;

/**
 * A scheme charging on one or more toll roads priced as one, by a table of amounts per pair of entry and exit gate
 * <p>
 * For example a closed motorway with a ticket system, as in France, Italy or Japan: a use runs from an entry gate to
 * an exit gate and costs the amount the operator's table gives for that pair.
 * </p>
 *
 * @author markr
 */
public interface EntryExitPricingScheme extends PricingScheme {

  /** id class for generating the ids within entry-exit schemes */
  public static final Class<EntryExitPricingScheme> ENTRY_EXIT_SCHEME_ID_CLASS = EntryExitPricingScheme.class;

  /**
   * The id of the scheme within entry-exit schemes, next to its id across all schemes
   *
   * @return the entry-exit scheme id
   */
  public abstract long getEntryExitSchemeId();

  /**
   * The toll roads the scheme charges on, priced as one, referenced, not owned: the toll roads of their layer own
   * them
   *
   * @return unmodifiable list of the toll roads, at least one
   */
  public abstract List<TollRoad> getTollRoads();

  /**
   * The scheme's gates, referenced, not owned: the network pricing's gates, {@link PricingSchemeGates}, own them
   *
   * @return unmodifiable list of the gates, at least one
   */
  public abstract List<PricingSchemeGate> getGates();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract SchemePricing<FromToGatesPriceRule> getPricing();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract EntryExitPricingScheme shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract EntryExitPricingScheme deepClone();
}
