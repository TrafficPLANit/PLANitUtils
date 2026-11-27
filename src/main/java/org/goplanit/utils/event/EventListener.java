package org.goplanit.utils.event;

import java.util.Arrays;
import java.util.Set;

/** Base interface for event listeners.
 * 
 * @author markr
 *
 */
public interface EventListener extends java.util.EventListener{
  
  /** Check if explicitly supported event types are available. It not, this does not mean
   * it does not support types, just they are not explicitly known or defined by the listener
   * 
   * @return true when available, false otherwise
   */
  public default boolean hasKnownSupportedEventTypes() {
    return getKnownSupportedEventTypes()!=null;
  }

  /** Collect explicitly supported event types that are known. When not defined the user has to explicitly
   * provide them upon registering the listener on an event producer, otherwise they can be extracted from here
   * 
   * @return the supported event types
   */
  public default EventType[] getKnownSupportedEventTypes() {
    return null;
  }

  /**
   * The known supported event types that are among the given ones, for example among those a producer fires
   *
   * @param eventTypes to look among
   * @return the known supported event types among them, in the listener's order; empty when there are none, or when
   *     the supported event types are not known
   */
  public default EventType[] findKnownSupportedEventTypesAmong(final Set<? extends EventType> eventTypes) {
    if (!hasKnownSupportedEventTypes()) {
      return new EventType[0];
    }
    return Arrays.stream(getKnownSupportedEventTypes()).filter(eventTypes::contains).toArray(EventType[]::new);
  }
}
