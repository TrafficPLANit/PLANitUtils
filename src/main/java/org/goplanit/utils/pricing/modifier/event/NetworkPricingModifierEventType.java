package org.goplanit.utils.pricing.modifier.event;

import org.goplanit.utils.event.EventType;

/**
 * Event type for events fired by the network pricing modifier
 * <p>
 * A type of its own, so a listener registers for the pricing events alone, as for the zoning events.
 * </p>
 *
 * @author markr
 */
public class NetworkPricingModifierEventType extends EventType {

  /**
   * Constructor
   *
   * @param eventTypeName name of the event type
   */
  public NetworkPricingModifierEventType(String eventTypeName) {
    super(eventTypeName);
  }
}
