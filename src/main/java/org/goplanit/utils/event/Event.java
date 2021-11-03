package org.goplanit.utils.event;

import org.goplanit.utils.id.IdAble;

/**
 * The Event interface defines a typed event (using the EventType). The sender of the event can be identified via its source. This is a local
 * event not appropriate to send across a network as it allows the use of local references regarding its source and content.
 */
public interface Event extends IdAble
{   
    /**
     * returns a reference to the source of the event however the derived event decides on how to expose its source, either as a pointer
     * or simply an id or something else. 
     * 
     * @return source of the event
     */
    Object getSource();    

    /**
     * returns the type of the event.
     * @return EbentType; the eventType of the event
     */
    EventType getType();
}
