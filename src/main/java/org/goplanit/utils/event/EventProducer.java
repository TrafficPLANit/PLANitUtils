package org.goplanit.utils.event;

import java.util.Set;

/** Marks event producers. Derived interfaces define the methods to add and remove listeners for certain event types.
 * Classes implementing them can use {@code EventProducerImpl} for the base functionality, their methods delegating to
 * its less restrictive protected counterparts.
 * 
 * @author markr
 *
 */
public interface EventProducer {

  /**
   * The event types the producer fires, of every kind it fires, as a listener gives the types it supports. The same
   * for as long as the producer exists; it fires no other type, and registers a listener for no other
   *
   * @return unmodifiable set of the event types fired
   */
  public abstract Set<EventType> getFiredEventTypes();
}
