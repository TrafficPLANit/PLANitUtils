package org.goplanit.utils.pricing;

import java.util.List;

/**
 * A scheme charging a mode on a whole class of road or the whole network, such as a truck charge per km on motorways
 * <p>
 * The one kind of scheme with link segments of its own, given by layer, type and link segment and tested on demand,
 * so a charge over all motorways holds no list of link segments and stays right when links are broken. Its sections
 * hold the rates that differ by region or road type within one national charge. Each mode is in at most one mode
 * scheme.
 * </p>
 *
 * @author markr
 */
public interface ModePricingScheme extends PricingScheme {

  /** id class for generating the ids within mode schemes */
  public static final Class<ModePricingScheme> MODE_SCHEME_ID_CLASS = ModePricingScheme.class;

  /**
   * The id of the scheme within mode schemes, next to its id across all schemes
   *
   * @return the mode scheme id
   */
  public abstract long getModePricingSchemeId();

  /**
   * The link segments the scheme charges on, as given
   *
   * @return the link segment coverage
   */
  public abstract ModePricingSchemeLinkSegmentCoverage getLinkSegmentCoverage();

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
  public abstract ModePricingScheme shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ModePricingScheme deepClone();
}
