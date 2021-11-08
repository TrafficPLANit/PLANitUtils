package org.goplanit.utils.event;

import java.util.logging.Logger;

import org.goplanit.utils.id.IdAbleImpl;
import org.goplanit.utils.id.IdGenerator;
import org.goplanit.utils.id.IdGroupingToken;

/**
 * The Base implementation for LocalEvent interface. Relies on globally unique id across all events to determine if events are equal
 * 
 * @author markr
 */
public abstract class EventImpl extends IdAbleImpl implements Event{
  
  /** logger to use */
  private static final Logger LOGGER = Logger.getLogger(EventImpl.class.getCanonicalName()); 
  
  /** Type of the event */
  private final EventType type;

  /** The content of the event */
  private final Object[] content;

  /** the source id of the event */
  private final Object sourceId;
    
  /**
   * Access to the content for derived events
   * 
   * @return content object array
   */
  protected Object[] getContent() {
    return content;
  }

  /**
   * Constructor
   * 
   * @param type EventType of the Event
   * @param source source of the event sender
   * @param content content array of the event
   */
  public EventImpl(final EventType type, final Object source, final Object[] content){
    super(IdGenerator.generateId(IdGroupingToken.collectGlobalToken(), Event.class));
    this.type = type;
    this.sourceId = source;
    this.content = content;
  }
  
  /**
   * Constructor
   * 
   * @param type EventType of the Event
   * @param source source of the event sender
   * @param content single entry content of the event
   */
  public EventImpl(final EventType type, final Object source, final Object content){
    super(IdGenerator.generateId(IdGroupingToken.collectGlobalToken(), Event.class));
    this.type = type;
    this.sourceId = source;
    this.content = new Object[] {content};
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
   * While events are id able, they cannot be cloned. and null is always returned
   * upon calling this method
   */
  @Override
  public EventImpl clone() {
    LOGGER.warning("IGNORED, Events are not cloneable");
    return null;
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

