package org.goplanit.utils.event;

import java.util.Optional;
import java.util.logging.Logger;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
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

  /** The end of an operation the event reports, null when it reports neither */
  private final EventState state;

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
    this(type, source, content, null);
  }

  /**
   * Constructor of an event reporting one end of an operation
   *
   * @param type EventType of the Event
   * @param source source of the event sender
   * @param content content array of the event
   * @param state the end of the operation the event reports
   */
  public EventImpl(final EventType type, final Object source, final Object[] content, final EventState state){
    super(IdGenerator.generateId(IdGroupingToken.collectGlobalToken(), Event.class));
    this.type = type;
    this.sourceId = source;
    this.content = content;
    this.state = state;
  }
  
  /**
   * Constructor
   * 
   * @param type EventType of the Event
   * @param source source of the event sender
   * @param content single entry content of the event
   */
  public EventImpl(final EventType type, final Object source, final Object content){
    this(type, source, new Object[] {content}, null);
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
  public final Optional<EventState> getState(){
    return Optional.ofNullable(state);
  }

  /**
   * While events are id able, they cannot be cloned. and null is always returned
   * upon calling this method
   */
  public EventImpl shallowClone() {
    LOGGER.warning("IGNORED, Events are not cloneable");
    return null;
  }

  /**
   * While events are id able, they cannot be cloned. and null is always returned
   * upon calling this method
   */
  @Override
  public EventImpl deepClone() {
    return shallowClone();
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

