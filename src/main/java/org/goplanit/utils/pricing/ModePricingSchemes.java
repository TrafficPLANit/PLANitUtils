package org.goplanit.utils.pricing;

import org.goplanit.utils.id.ManagedIdEntities;
import org.goplanit.utils.mode.Mode;

/**
 * The mode schemes of a network pricing
 * <p>
 * Each mode is in at most one mode scheme, so the container finds the scheme charging a mode, as the check of a new
 * mode scheme against the others needs.
 * </p>
 *
 * @author markr
 */
public interface ModePricingSchemes extends ManagedIdEntities<ModePricingScheme> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ModePricingSchemeFactory getFactory();

  /**
   * The registered mode scheme charging a mode, of which there is at most one
   *
   * @param mode to look up
   * @return the mode scheme, null when none charges it
   */
  public abstract ModePricingScheme getByPricedMode(Mode mode);
}
