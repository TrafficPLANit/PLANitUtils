package org.goplanit.utils.event;

/** Interface to signify event producers. In reality derived interfaces are to be used to define the appropriate method signatures for 
 * adding and removing listeners for certain event types, whereas the {@code EventProducerImpl can be used by classes implementing these
 * interfaces to provide the base functionality to which these signature methods can delegate to to their less restrictive protected 
 * counterparts that have been made available. 
 * 
 * @author markr
 *
 */
public interface EventProducer {

}
