package org.goplanit.utils.zoning.modifier.event;

import org.goplanit.utils.event.EventProducer;
import java.util.Set;

/**
 * Interface dedicated to the {@code ZoningModifier} event related exposed methods. Specifies method
 * signatures for registering the appropriate listeners and event types that are supported on a zoning modifier,
 * and lists the event types it fires, see {@link #FIRED_ZONING_EVENT_TYPES}.
 *
 * @author markr
 *
 */
public interface ZoningModifierEventProducer extends EventProducer {

  /**
   * The event types a zoning modifier fires: {@link ModifiedZoneIdsEvent},
   * {@link RecreatedZoningEntitiesManagedIdsEvent}, {@link RemoveConnectoidAccessLinkSegmentEvent},
   * {@link RemoveConnectoidAccessZoneEntryEvent}, {@link RemoveConnectoidAllowedModeEvent},
   * {@link RemoveConnectoidEvent}
   */
  public static final Set<ZoningModifierEventType> FIRED_ZONING_EVENT_TYPES = Set.of(
      ModifiedZoneIdsEvent.EVENT_TYPE, RecreatedZoningEntitiesManagedIdsEvent.EVENT_TYPE,
      RemoveConnectoidAccessLinkSegmentEvent.EVENT_TYPE, RemoveConnectoidAccessZoneEntryEvent.EVENT_TYPE,
      RemoveConnectoidAllowedModeEvent.EVENT_TYPE, RemoveConnectoidEvent.EVENT_TYPE);

  /**
   * The event types of this kind the producer fires, see {@link #FIRED_ZONING_EVENT_TYPES}
   *
   * @return unmodifiable set of the event types fired
   */
  public default Set<ZoningModifierEventType> getFiredZoningEventTypes() {
    return FIRED_ZONING_EVENT_TYPES;
  }

  /**
   * Register listener for all its supported event types fired by a zoning modifier
   *
   *  @param listener to register
   */
  public abstract void addListener(final ZoningModifierListener listener);

  /**
   * Register listeners for events fired by a zoning modifier
   * 
   *  @param listener to register
   *  @param eventType to register listener for
   */
  public abstract void addListener(final ZoningModifierListener listener, final ZoningModifierEventType eventType);
  
  /**
   * Remove listener for given event type
   * 
   *  @param listener to remove
   *  @param eventType to unregister listener for
   */
  public abstract void removeListener(final ZoningModifierListener listener, final ZoningModifierEventType eventType);  
  
  /**
   * Remove listener for all event types it is registered for
   * 
   *  @param listener to remove
   */
  public abstract void removeListener(final ZoningModifierListener listener);    
}
