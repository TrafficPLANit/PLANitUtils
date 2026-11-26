package org.goplanit.utils.event;

/** Marks event producers. Derived interfaces define the methods to add and remove listeners for certain event types.
 * Classes implementing them can use {@code EventProducerImpl} for the base functionality, their methods delegating to
 * its less restrictive protected counterparts.
 * 
 * @author markr
 *
 */
public interface EventProducer {

}
