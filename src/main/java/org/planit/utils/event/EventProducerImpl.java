package org.planit.utils.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The EventProducer is the base class able to produce events. It provides protected methods to register listeners and remove listeners
 * as well as ways to fire a generic event. Event producing classes should either extend this class to provide more user friendly public methods
 * with the correct signatures that enforce the appropriate event and listener combinations suitable for that particular class while internally
 * making use of this implementations functionality. 
 * 
 * @author markr
 */
public abstract class EventProducerImpl
{
    /** The collection of interested listeners */
    protected Map<EventType, Set<EventListener>> listeners;
    
    /** add a listener for one or more event types that are presumably triggered by this producer 
     * 
     * @param listener
     * @param eventTypes
     */
    protected final synchronized void addListener(final EventListener listener, final EventType... eventTypes)
    {
      for(int index=0;index<eventTypes.length;++index) {
        EventType type = eventTypes[index];
        listeners.putIfAbsent(type, new HashSet<EventListener>());
        listeners.get(type).add(listener);
      }
    }    
    
    /**
     *  Remove a listener for a given event type
     *  
     * @param listener to remove
     * @param eventType to remove it for
     * @return true when succeeded, false otherwise
     */
    protected final synchronized boolean removeListener(final EventListener listener, final EventType eventType)
    {
      if(listener==null) {
        throw new IllegalArgumentException("listener cannot be null");
      }
      if(eventType==null) {
        throw new IllegalArgumentException("event type cannot be null");
      }
      
      if (!this.listeners.containsKey(eventType)){
          return false;
      }
      boolean result = false;
      for (Iterator<EventListener> i = this.listeners.get(eventType).iterator(); i.hasNext();){
        if (listener.equals(i.next())) {
          i.remove();
          result = true;
          break;
        }
      }
      if (this.listeners.get(eventType).size() == 0){
          this.listeners.remove(eventType);
      }
      return result;
    }    

    /** Let derived class deal with the handling of the listener, where based on the derived event implementation
     * the listener's concrete class can be determined which in turn allows for calling the right event callback method
     * which is unknown at this base level since this event mechanism does not force a particular notification method signature
     * on its listener interface
     * 
     * @param eventListener to notify for the event
     * @param event to process for the listener
     */
    protected abstract void fireEvent(final EventListener eventListener, final Event event);

    /**
     * Transmit an event to all interested listeners.
     * @param event EventInterface; the event
     * @return the event
     */
    protected synchronized void fireEvent(final Event event)
    {
      if(event==null) {
        throw new IllegalArgumentException("event cannot be null");
      }
      if(event.getType()==null) {
        throw new IllegalArgumentException("event type cannot be null");
      }
      
      if (this.listeners.containsKey(event.getType())){
        // copy listeners in case removeListener() is called during this method processing
        List<EventListener> listenerList = new ArrayList<>(this.listeners.get(event.getType()));
        for (EventListener listener: listenerList){
          this.fireEvent(listener, event);              
        }
      }
    }

    /**
     * Constructs a new EventProducer and checks for duplicate values in event types.
     */
    protected EventProducerImpl(){
      this.listeners = new HashMap<EventType, Set<EventListener>>();
    }

    /**
     * Remove all the listeners from this producer
     */
    public synchronized void removeAllListeners(){
      this.listeners = null;
      this.listeners = new HashMap<EventType, Set<EventListener>>();
    }

    /**
     * Verify if any listeners are available
     * 
     * @return true when present, false otherwise
     */
    public boolean hasListeners(){
        return !this.listeners.isEmpty();
    }

    /** Determine the number of listeners for a given event type
     * 
     * @param eventType to check
     * @return number of listeners
     */
    public synchronized int numberOfListeners(final EventType eventType){
      if (this.listeners.containsKey(eventType)){
        return this.listeners.get(eventType).size();
      }
      return 0;
    }

    /** Determine the event types have one or more listeners registered
     * 
     * @return event types with listeners
     */
    public synchronized Set<EventType> getEventTypesWithListeners(){
      return Collections.unmodifiableSet(this.listeners.keySet());
    }

}
