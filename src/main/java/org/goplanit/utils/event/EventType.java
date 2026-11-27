package org.goplanit.utils.event;

import java.util.HashSet;
import java.util.Set;

import org.goplanit.utils.misc.StringUtils;

/** Event type wraps a String representing the unique name identifier for an event. It is used as a quick
 *  way to identify listeners and events when dealing with events in a type safe manner. It should be unique across all
 *  available events. Each derived event implementation makes its type publicly available, and each event producer
 *  lists the event types it fires, see {@link EventProducer#getFiredEventTypes()}.
 * <p>
 *  The type is what producers and listeners agree on: a listener names the types it handles, a producer the types it
 *  fires, and a listener is registered on a producer for the types both name. For example the removal of a toll road
 *  has one type, which the layer modifier fires and the pricing's network change handler handles. Types are compared by
 *  name, and a name may be registered once, so two events cannot share a type by accident.
 * </p>
 * 
 * @author markr
 *
 */
public class EventType {
  
  /** unique name reflecting the eventType */
  private final String name;
  
  /** track s all event types */
  private static final Set<EventType> eventTypes = new HashSet<>();
  
  /** Register event type
   * 
   * @param newType to register
   */
  private static final void registerEventType(final EventType newType){
    boolean newEntry = eventTypes.add(newType);
    if(!newEntry) {
      throw new RuntimeException(String.format("EventType %s already exists, not allowed",newType.toString()));
    }
  }
  
  /** Constructor 
   * 
   * @param eventTypeName to use
   * 
   */
  public EventType(final String eventTypeName){
    if (StringUtils.isNullOrBlank(eventTypeName)){
        throw new IllegalArgumentException("eventTypeName null or blank");
    }
    this.name = eventTypeName;
    registerEventType(this);
  }
  
  /**
   * Hash code based on name
   */
  @Override
  public int hashCode(){
      return name.hashCode();
  }
  
  /** 
   * {@inheritDoc} 
   */
  @Override
  public boolean equals(final Object object){
    if (!(object instanceof EventType)){
        return false;
    }
    return hashCode() == ((EventType) object).hashCode();
  }  

  /**
   * @return the event type name
   */
  public String getName(){
    return this.name;
  }

  /**  
   * {@inheritDoc}
   */
  @Override
  public String toString(){
    return this.name;
  }  
}
