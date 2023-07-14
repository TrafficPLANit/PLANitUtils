package org.goplanit.utils.service.routed.modifier;

import org.goplanit.utils.event.EventProducer;

/** Interface dedicated to the {@code RoutedServicesLayerModifier} event related exposed methods. Specifies method signatures for registering
 * the appropriate listeners and event types that are supported on a routed services modifier.
 *  
 * @author markr
 *
 */
public interface RoutedServicesModifierEventProducer extends EventProducer {

  /**
   * Register listeners for events fired by a routed services modifier
   *
   *  @param listener to register
   */
  public abstract void addListener(final RoutedServicesModifierListener listener);

  /**
   * Register listeners for events fired by a routed services modifier
   *
   *  @param listener to register
   *  @param eventType to register listener for
   */
  public abstract void addListener(final RoutedServicesModifierListener listener, final RoutedServicesModifierEventType eventType);

  /**
   * Remove listener for given event type
   *
   *  @param listener to remove
   *  @param eventType to unregister listener for
   */
  public abstract void removeListener(final RoutedServicesModifierListener listener, final RoutedServicesModifierEventType eventType);

  /**
   * Remove listener for all event types it is registered for
   *
   *  @param listener to remove
   */
  public abstract void removeListener(final RoutedServicesModifierListener listener);
}
