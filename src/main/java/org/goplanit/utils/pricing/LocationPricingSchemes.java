package org.goplanit.utils.pricing;

import org.goplanit.utils.id.ManagedIdEntities;

/**
 * The location schemes of a network pricing
 * <p>
 * A container per kind, as the XML has one per kind, with ids within the kind next to the ids across all schemes.
 * </p>
 *
 * @author markr
 */
public interface LocationPricingSchemes extends ManagedIdEntities<LocationPricingScheme> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract LocationPricingSchemeFactory getFactory();
}
