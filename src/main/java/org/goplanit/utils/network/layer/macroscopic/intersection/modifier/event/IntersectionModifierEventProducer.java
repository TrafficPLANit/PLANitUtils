package org.goplanit.utils.network.layer.macroscopic.intersection.modifier.event;

import org.goplanit.utils.event.EventProducer;
import java.util.Set;

/** Interface dedicated to the intersection related events of the macroscopic network layer modifier. Specifies method
 * signatures for registering the appropriate listeners and event types that are supported.
 *
 * @author markr
 *
 */
public interface IntersectionModifierEventProducer extends EventProducer {

  /**
   * The event types the network layer modifier fires: {@link CollapseSpatialIntersectionEvent},
   * {@link RecreatedIntersectionsManagedIdsEvent}, {@link RemoveIntersectionEvent}, {@link RemoveSpatialMovementEvent}
   */
  public static final Set<IntersectionModifierEventType> FIRED_INTERSECTION_EVENT_TYPES = Set.of(
      CollapseSpatialIntersectionEvent.EVENT_TYPE, RecreatedIntersectionsManagedIdsEvent.EVENT_TYPE,
      RemoveIntersectionEvent.EVENT_TYPE, RemoveSpatialMovementEvent.EVENT_TYPE);

  /**
   * The event types of this kind the producer fires, see {@link #FIRED_INTERSECTION_EVENT_TYPES}
   *
   * @return unmodifiable set of the event types fired
   */
  public default Set<IntersectionModifierEventType> getFiredIntersectionEventTypes() {
    return FIRED_INTERSECTION_EVENT_TYPES;
  }

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
  public abstract void addListener(
      final IntersectionModifierListener listener,
      final IntersectionModifierEventType eventType);

  /**
   * Remove listener for given event type
   *
   *  @param listener to remove
   *  @param eventType to unregister listener for
   */
  public abstract void removeListener(
      final IntersectionModifierListener listener,
      final IntersectionModifierEventType eventType);

  /**
   * Remove listener for all event types it is registered for
   *
   *  @param listener to remove
   */
  public abstract void removeListener(final IntersectionModifierListener listener);

  /**
   * Register a listener internal to the owner of the producer for all its supported event types: it hears each event
   * before the other listeners and stays registered when they are removed
   *
   * @param listener to register
   */
  public abstract void addInternalListener(final IntersectionModifierListener listener);

  /**
   * Remove a listener internal to the owner of the producer, for all event types it is registered for
   *
   * @param listener to remove
   */
  public abstract void removeInternalListener(final IntersectionModifierListener listener);
}
