package org.planit.utils.event;

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

  /** Collect explicitly supported event types that are known. When not defined the user has to explicitly provide them upon registering
   * the listener on an event producer, otherwise they can be extracted from here
   * 
   * @return the supported event types
   */
  public default EventType[] getKnownSupportedEventTypes() {
    return null;
  }
}
