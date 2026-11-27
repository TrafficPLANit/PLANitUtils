package org.goplanit.utils.pricing.modifier.event;

import org.goplanit.utils.event.EventProducer;
import java.util.Set;

/**
 * The events of the network pricing modifier: registering listeners for them
 * <p>
 * The typed methods a listener to the pricing's changes registers through, as the zoning modifier's producer gives
 * for the zoning.
 * </p>
 *
 * @author markr
 */
public interface NetworkPricingModifierEventProducer extends EventProducer {

  /**
   * The event types the network pricing modifier fires: {@link RemovePricingSchemeEvent}
   */
  public static final Set<NetworkPricingModifierEventType> FIRED_NETWORK_PRICING_EVENT_TYPES = Set.of(
      RemovePricingSchemeEvent.EVENT_TYPE);

  /**
   * The event types of this kind the producer fires, see {@link #FIRED_NETWORK_PRICING_EVENT_TYPES}
   *
   * @return unmodifiable set of the event types fired
   */
  public default Set<NetworkPricingModifierEventType> getFiredNetworkPricingEventTypes() {
    return FIRED_NETWORK_PRICING_EVENT_TYPES;
  }

  /**
   * Register listener for all its supported event types fired by the network pricing modifier
   *
   * @param listener to register
   */
  public abstract void addListener(final NetworkPricingModifierListener listener);

  /**
   * Register listener for events fired by the network pricing modifier
   *
   * @param listener to register
   * @param eventType to register listener for
   */
  public abstract void addListener(final NetworkPricingModifierListener listener,
      final NetworkPricingModifierEventType eventType);

  /**
   * Remove listener for given event type
   *
   * @param listener to remove
   * @param eventType to unregister listener for
   */
  public abstract void removeListener(final NetworkPricingModifierListener listener,
      final NetworkPricingModifierEventType eventType);

  /**
   * Remove listener for all event types it is registered for
   *
   * @param listener to remove
   */
  public abstract void removeListener(final NetworkPricingModifierListener listener);
}
