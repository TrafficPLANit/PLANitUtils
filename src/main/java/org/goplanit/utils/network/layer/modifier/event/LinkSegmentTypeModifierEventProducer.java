package org.goplanit.utils.network.layer.modifier.event;

import org.goplanit.utils.event.EventProducer;
import java.util.Set;

/** Interface dedicated to the link segment type related events of the macroscopic network layer modifier. Specifies
 * method signatures for registering the appropriate listeners and event types that are supported.
 *
 * @author markr
 */
public interface LinkSegmentTypeModifierEventProducer extends EventProducer {

  /**
   * The event types the network layer modifier fires: {@link ChangeLinkSegmentTypeEvent},
   * {@link RemoveLinkSegmentTypeEvent}
   */
  public static final Set<LinkSegmentTypeModifierEventType> FIRED_LINK_SEGMENT_TYPE_EVENT_TYPES = Set.of(
      ChangeLinkSegmentTypeEvent.EVENT_TYPE, RemoveLinkSegmentTypeEvent.EVENT_TYPE);

  /**
   * The event types of this kind the producer fires, see {@link #FIRED_LINK_SEGMENT_TYPE_EVENT_TYPES}
   *
   * @return unmodifiable set of the event types fired
   */
  public default Set<LinkSegmentTypeModifierEventType> getFiredLinkSegmentTypeEventTypes() {
    return FIRED_LINK_SEGMENT_TYPE_EVENT_TYPES;
  }

  /**
   * Register listener for all its supported event types fired about link segment types
   *
   *  @param listener to register
   */
  public abstract void addListener(final LinkSegmentTypeModifierListener listener);

  /**
   * Register listener for events fired about link segment types
   *
   *  @param listener to register
   *  @param eventType to register listener for
   */
  public abstract void addListener(
      final LinkSegmentTypeModifierListener listener, final LinkSegmentTypeModifierEventType eventType);

  /**
   * Remove listener for given event type
   *
   *  @param listener to remove
   *  @param eventType to unregister listener for
   */
  public abstract void removeListener(
      final LinkSegmentTypeModifierListener listener, final LinkSegmentTypeModifierEventType eventType);

  /**
   * Remove listener for all event types it is registered for
   *
   *  @param listener to remove
   */
  public abstract void removeListener(final LinkSegmentTypeModifierListener listener);

  /**
   * Register a listener internal to the owner of the producer for all its supported event types: it hears each event
   * before the other listeners and stays registered when they are removed
   *
   * @param listener to register
   */
  public abstract void addInternalListener(final LinkSegmentTypeModifierListener listener);

  /**
   * Remove a listener internal to the owner of the producer, for all event types it is registered for
   *
   * @param listener to remove
   */
  public abstract void removeInternalListener(final LinkSegmentTypeModifierListener listener);
}
