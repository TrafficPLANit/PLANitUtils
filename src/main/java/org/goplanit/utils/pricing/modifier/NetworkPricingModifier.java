package org.goplanit.utils.pricing.modifier;

import org.goplanit.utils.pricing.PricingScheme;
import org.goplanit.utils.pricing.modifier.event.NetworkPricingModifierEventProducer;

/**
 * Makes the changes to a network pricing that span several of its entities, and tells those listening
 * <p>
 * For example removing a scheme whose last toll road the network removed: its caps lose it, a cap covering no scheme
 * any more goes, and a {@code RemovePricingSchemeEvent} tells listeners such as a later assignment. It also holds the
 * handlers keeping the mode schemes' own link segments, the sections and the gates in step with the network, as the
 * zoning modifier does for the connectoids.
 * </p>
 *
 * @author markr
 */
public interface NetworkPricingModifier extends NetworkPricingModifierEventProducer {

  /**
   * Remove a scheme, with its sections and gates, from the pricing, take it off the caps covering it, remove a cap left
   * covering no scheme, and fire a remove scheme event
   *
   * @param scheme to remove
   */
  public abstract void removePricingScheme(PricingScheme scheme);

  /**
   * Recreate the ids of all schemes, across all kinds and within each kind, so they are contiguous again after
   * removals
   */
  public abstract void recreatePricingSchemeIds();
}
