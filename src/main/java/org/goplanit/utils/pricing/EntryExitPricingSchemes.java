package org.goplanit.utils.pricing;

import org.goplanit.utils.id.ManagedIdEntities;

/**
 * The entry-exit schemes of a network pricing
 * <p>
 * A container per kind, as the XML has one per kind, with ids within the kind next to the ids across all schemes.
 * </p>
 *
 * @author markr
 */
public interface EntryExitPricingSchemes extends ManagedIdEntities<EntryExitPricingScheme> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract EntryExitPricingSchemeFactory getFactory();
}
