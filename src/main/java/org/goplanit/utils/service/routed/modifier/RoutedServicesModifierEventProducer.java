package org.goplanit.utils.service.routed.modifier;

import org.goplanit.utils.event.EventProducer;
import java.util.Set;

/**
 * Interface dedicated to the {@code RoutedServicesLayerModifier} event related exposed methods. Specifies method
 * signatures for registering the appropriate listeners and event types that are supported on a routed services
 * modifier, and lists the event types it fires, see {@link #FIRED_ROUTED_SERVICES_EVENT_TYPES}.
 *
 * @author markr
 *
 */
public interface RoutedServicesModifierEventProducer extends EventProducer {

  /**
   * The event types a routed services layer modifier fires: {@link ModifiedRoutedServicesIdsEvent},
   * {@link ModifiedRoutedTripIdsEvent}, {@link ModifiedTripScheduleDepartureIdsEvent}
   */
  public static final Set<RoutedServicesModifierEventType> FIRED_ROUTED_SERVICES_EVENT_TYPES = Set.of(
      ModifiedRoutedServicesIdsEvent.EVENT_TYPE, ModifiedRoutedTripIdsEvent.EVENT_TYPE,
      ModifiedTripScheduleDepartureIdsEvent.EVENT_TYPE);

  /**
   * The event types of this kind the producer fires, see {@link #FIRED_ROUTED_SERVICES_EVENT_TYPES}
   *
   * @return unmodifiable set of the event types fired
   */
  public default Set<RoutedServicesModifierEventType> getFiredRoutedServicesEventTypes() {
    return FIRED_ROUTED_SERVICES_EVENT_TYPES;
  }

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
  public abstract void addListener(
      final RoutedServicesModifierListener listener,
      final RoutedServicesModifierEventType eventType);

  /**
   * Remove listener for given event type
   *
   *  @param listener to remove
   *  @param eventType to unregister listener for
   */
  public abstract void removeListener(
      final RoutedServicesModifierListener listener,
      final RoutedServicesModifierEventType eventType);

  /**
   * Remove listener for all event types it is registered for
   *
   *  @param listener to remove
   */
  public abstract void removeListener(final RoutedServicesModifierListener listener);
}
