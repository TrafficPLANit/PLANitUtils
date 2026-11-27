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
public interface PriceCaps extends ManagedIdEntities<PriceCap> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract PriceCapFactory getFactory();

  /**
   * The registered caps covering a scheme
   *
   * @param scheme to look up
   * @return the caps, empty when none covers it, unmodifiable and a copy: later changes to the container do not show in
   *     it
   */
  public abstract List<PriceCap> getByScheme(PricingScheme scheme);

  /**
   * Create a traveller's totals per cap over a period, with nothing paid yet, for the cumulative prices of its uses
   *
   * @return the totals
   */
  public abstract PriceCapTotals createPriceCapTotals();
}
