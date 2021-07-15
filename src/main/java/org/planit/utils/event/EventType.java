package org.planit.utils.event;

import java.util.HashSet;
import java.util.Set;
import org.planit.utils.misc.StringUtils;

/** Event type wraps a String representing the unique name identifier for an event. It is used as a quick
 *  way to identify listeners and events when dealing with events in a type safe manner. It should be uique across all available events.
 *  Each derived event implementation should make their type publicly available and each event producer should list the event types it triggers.
 * 
 * @author markr
 *
 */
public class EventType {
  
  /** unique name reflecting the eventType */
  private final String name;
  
  /** track s all event types */
  private static final Set<EventType> eventTypes = new HashSet<EventType>();
  
  /** Register event type
   * 
   * @param newType to register
   */
  private static final void registerEventType(final EventType newType){
    boolean exists = eventTypes.add(newType);
    if(exists) {
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
