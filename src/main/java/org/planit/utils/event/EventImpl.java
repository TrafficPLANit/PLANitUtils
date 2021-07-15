package org.planit.utils.event;

import org.planit.utils.id.IdAbleImpl;
import org.planit.utils.id.IdGenerator;
import org.planit.utils.id.IdGroupingToken;

/**
 * The Base implementation for LocalEvent interface. Relies on globally unique id across all events to determine if events are equal
 * 
 * @author markr
 */
public abstract class EventImpl extends IdAbleImpl implements Event{
  
    /** Type of the event */
    private final EventType type;

    /** The content of the event */
    private final Object content;

    /** the source id of the event */
    private final Object sourceId;
    
    /**
     * Access to the content for derived events
     */
    protected Object getContent() {
      return content;
    }

    /**
     * Constructor
     * 
     * @param type EventType of the Event
     * @param source source of the event sender
     * @param content content of the event
     */
    public EventImpl(final EventType type, final Object source, final Object content){
      super(IdGenerator.generateId(IdGroupingToken.collectGlobalToken(), Event.class));
      this.type = type;
      this.sourceId = source;
      this.content = content;
    }

    /** 
     * {@inheritDoc} 
     */
    @Override
    public final Object getSource(){
      return this.sourceId;
    }

    /** 
     * {@inheritDoc} 
     */
    @Override
    public final EventType getType(){
      return this.type;
    }

    /** 
     * {@inheritDoc} 
     */
    @Override
    public String toString()
    {
        return ""+ this.getClass().getName() + "-" + this.getType() + "-id:"+this.getId();
    }

}

