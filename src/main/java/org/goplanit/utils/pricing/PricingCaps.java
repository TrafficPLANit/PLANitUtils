package org.goplanit.utils.pricing;

import org.goplanit.utils.id.ManagedIdEntities;

import java.util.List;

/**
 * The caps of a network pricing, found by the schemes they cover
 * <p>
 * One container for a scheme's own caps and for caps across schemes alike, so a scheme's caps are found without the
 * scheme holding them, and a cap across schemes is not copied into each, which would change its meaning.
 * </p>
 *
 * @author markr
 */
public interface PricingCaps extends ManagedIdEntities<PricingCap> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract PricingCapFactory getFactory();

  /**
   * The registered caps covering a scheme
   *
   * @param scheme to look up
   * @return the caps, empty when none covers it, unmodifiable and a copy: later changes to the container do not show in
   *     it
   */
  public abstract List<PricingCap> getByScheme(PricingScheme scheme);
}
