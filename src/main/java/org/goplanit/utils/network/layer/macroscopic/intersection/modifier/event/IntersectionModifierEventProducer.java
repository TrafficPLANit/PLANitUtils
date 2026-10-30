package org.goplanit.utils.network.layer.macroscopic.intersection.modifier.event;

import org.goplanit.utils.event.EventProducer;

/** Interface dedicated to the intersection related events of the macroscopic network layer modifier. Specifies method
 * signatures for registering the appropriate listeners and event types that are supported.
 *
 * @author markr
 *
 */
public interface IntersectionModifierEventProducer extends EventProducer {

  /**
   * Register listener for all its supported event types fired about intersections
   *
   *  @param listener to register
   */
  public abstract void addListener(final IntersectionModifierListener listener);

  /**
   * Register listener for events fired about intersections
   *
   *  @param listener to register
   *  @param eventType to register listener for
   */
  public abstract void addListener(final IntersectionModifierListener listener, final IntersectionModifierEventType eventType);

  /**
   * Remove listener for given event type
   *
   *  @param listener to remove
   *  @param eventType to unregister listener for
   */
  public abstract void removeListener(final IntersectionModifierListener listener, final IntersectionModifierEventType eventType);

  /**
   * Remove listener for all event types it is registered for
   *
   *  @param listener to remove
   */
  public abstract void removeListener(final IntersectionModifierListener listener);
}
