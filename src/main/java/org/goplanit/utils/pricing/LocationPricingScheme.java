package org.goplanit.utils.pricing;

import org.goplanit.utils.network.layer.macroscopic.tollroad.TollRoad;

import java.util.List;

/**
 * A scheme charging on one or more toll roads priced as one, by a start per use and a rate per km
 * <p>
 * For example WestConnex, one flagfall and one cap across the M4, Rozelle, M8 and M5 East, as a continuous run from
 * one onto another is one use; or a cordon, whose crossings are a toll road of the network. The network keeps its toll
 * roads as the OSM reader finds them, and which are priced as one is the scheme's to say. The toll roads of one scheme
 * price the same modes.
 * </p>
 *
 * @author markr
 */
public interface LocationPricingScheme extends PricingScheme {

  /** id class for generating the ids within location schemes */
  public static final Class<LocationPricingScheme> LOCATION_SCHEME_ID_CLASS = LocationPricingScheme.class;

  /**
   * The id of the scheme within location schemes, next to its id across all schemes
   *
   * @return the location scheme id
   */
  public abstract long getLocationSchemeId();

  /**
   * The toll roads the scheme charges on, priced as one, referenced, not owned: the toll roads of their layer own
   * them
   *
   * @return unmodifiable list of the toll roads, at least one
   */
  public abstract List<TollRoad> getTollRoads();

  /**
   * The scheme's sections, referenced, not owned: the network pricing's sections, {@link PricingSchemeSections},
   * own them
   *
   * @return unmodifiable list of the sections, empty when there are none
   */
  public abstract List<PricingSchemeSection> getSections();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract SchemePricing<StartWithPerKmPriceRule> getPricing();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract LocationPricingScheme shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract LocationPricingScheme deepClone();
}
