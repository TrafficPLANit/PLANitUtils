package org.goplanit.utils.pricing.modifier.event;

import org.goplanit.utils.event.Event;

/**
 * Event fired by the network pricing modifier about a change to the pricing
 * <p>
 * For example the removal of a scheme left without toll roads, so a later assignment holding totals per scheme can
 * follow.
 * </p>
 *
 * @author markr
 */
public interface NetworkPricingModificationEvent extends Event {

}
