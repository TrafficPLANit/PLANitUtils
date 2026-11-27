package org.goplanit.utils.pricing.modifier.event;

import org.goplanit.utils.event.EventListener;

/**
 * Listener for events fired by the network pricing modifier
 * <p>
 * For example a later assignment listening for removed schemes, so the totals it keeps per scheme follow the pricing.
 * </p>
 *
 * @author markr
 */
public interface NetworkPricingModifierListener extends EventListener {

  /**
   * Invoked when a pricing event it is registered for is fired
   *
   * @param event fired
   */
  public abstract void onNetworkPricingModifierEvent(NetworkPricingModificationEvent event);
}
