package org.goplanit.utils.event;

import java.util.Optional;

import org.goplanit.utils.id.IdAble;

/**
 * A typed event (using the EventType), whose sender is identified by its source. It is a local event, not suited to
 * sending across a network, as its source and content may be local references.
 */
public interface Event extends IdAble
{   
    /**
     * returns a reference to the source of the event however the derived event decides on how to expose its source,
     * either as a pointer or simply an id or something else.
     * 
     * @return source of the event
     */
    Object getSource();    

    /**
     * returns the type of the event.
     * @return EventType; the eventType of the event
     */
    EventType getType();

    /**
     * Which end of an operation the event reports, for an event fired both when the operation starts and when it is
     * done. Most events report neither
     *
     * @return the state, empty by default
     */
    default Optional<EventState> getState(){
      return Optional.empty();
    }
}
