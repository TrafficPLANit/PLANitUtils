package org.goplanit.utils.network.layer.macroscopic.tollroad.modifier.event;

import org.goplanit.utils.event.EventProducer;
import java.util.Set;

/**
 * The toll road events of the macroscopic network layer modifier: registering listeners for them
 * <p>
 * The layer modifier produces events of several kinds; this interface gives the typed methods for the toll road ones,
 * as {@code IntersectionModifierEventProducer} does for intersections.
 * </p>
 *
 * @author markr
 */
public interface TollRoadModifierEventProducer extends EventProducer {

  /**
   * The event types the network layer modifier fires: {@link RecreatedTollRoadsManagedIdsEvent},
   * {@link RemoveTollRoadEvent}
   */
  public static final Set<TollRoadModifierEventType> FIRED_TOLL_ROAD_EVENT_TYPES = Set.of(
      RecreatedTollRoadsManagedIdsEvent.EVENT_TYPE, RemoveTollRoadEvent.EVENT_TYPE);

  /**
   * The event types of this kind the producer fires, see {@link #FIRED_TOLL_ROAD_EVENT_TYPES}
   *
   * @return unmodifiable set of the event types fired
   */
  public default Set<TollRoadModifierEventType> getFiredTollRoadEventTypes() {
    return FIRED_TOLL_ROAD_EVENT_TYPES;
  }

  /**
   * Register listener for all its supported event types fired about toll roads
   *
   * @param listener to register
   */
  public abstract void addListener(final TollRoadModifierListener listener);

  /**
   * Register listener for events fired about toll roads
   *
   * @param listener to register
   * @param eventType to register listener for
   */
  public abstract void addListener(final TollRoadModifierListener listener, final TollRoadModifierEventType eventType);

  /**
   * Remove listener for given event type
   *
   * @param listener to remove
   * @param eventType to unregister listener for
   */
  public abstract void removeListener(
      final TollRoadModifierListener listener,
      final TollRoadModifierEventType eventType);

  /**
   * Remove listener for all event types it is registered for
   *
   * @param listener to remove
   */
  public abstract void removeListener(final TollRoadModifierListener listener);

  /**
   * Register a listener internal to the owner of the producer for all its supported event types: it hears each event
   * before the other listeners and stays registered when they are removed
   *
   * @param listener to register
   */
  public abstract void addInternalListener(final TollRoadModifierListener listener);

  /**
   * Remove a listener internal to the owner of the producer, for all event types it is registered for
   *
   * @param listener to remove
   */
  public abstract void removeInternalListener(final TollRoadModifierListener listener);
}
