package org.goplanit.utils.pricing.modifier.event;

import org.goplanit.utils.event.EventImpl;
import org.goplanit.utils.pricing.PricingScheme;
import org.goplanit.utils.pricing.modifier.NetworkPricingModifier;

/**
 * Event fired when a scheme has been removed from its network pricing, such as one left without toll roads
 * <p>
 * So whoever refers to schemes can follow, for example an assignment holding the charges of each scheme.
 * </p>
 *
 * @author markr
 */
public class RemovePricingSchemeEvent extends EventImpl implements NetworkPricingModificationEvent {

  /** event type fired off when a scheme has been removed */
  public static final NetworkPricingModifierEventType EVENT_TYPE =
      new NetworkPricingModifierEventType("NETWORKPRICINGMODIFIER.SCHEME.REMOVE");

  /**
   * Constructor
   *
   * @param source pricing modifier firing the event
   * @param removedScheme the scheme removed
   */
  public RemovePricingSchemeEvent(final NetworkPricingModifier source, final PricingScheme removedScheme) {
    super(EVENT_TYPE, source, new Object[] {removedScheme});
  }

  /**
   * The removed scheme
   *
   * @return removed scheme
   */
  public PricingScheme getRemovedScheme() {
    return (PricingScheme) getContent()[0];
  }
}
