package org.goplanit.utils.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 * The EventProducer is the base class able to produce events. It provides protected methods to register listeners
 * and remove listeners as well as ways to fire a generic event. Event producing classes should either extend
 * this class to provide more user friendly public methods with the correct signatures that enforce the
 * appropriate event and listener combinations suitable for that particular class while internally
 * making use of this implementations functionality. 
 * 
 * @author markr
 */
public abstract class EventProducerImpl{
  
  /** the logger to use */
  private static final Logger LOGGER = Logger.getLogger(EventProducerImpl.class.getCanonicalName());
  
  /** The collection of interested listeners by event and priority */
  protected Map<EventType, Map<EventListenerPriority, List<EventListener>>> listeners;

  /** listeners internal to the owner of this producer by event type, in order of registration; they receive each
   * event before the other listeners and are not removed with them */
  private final Map<EventType, List<EventListener>> internalListeners = new HashMap<>();
  
  /** Add a listener for one or more event types (collected from listener's known supported types) that
   * are presumably triggered by this producer
   * 
   * @param listener to register
   * @param priority to apply for the combination of listener and event type(s)
   */
  protected final synchronized void addListener(final EventListener listener, EventListenerPriority priority){
    if(!listener.hasKnownSupportedEventTypes()) {
      LOGGER.severe("IGNORED: unable to identify listener's supported event types, "
          + "consider registering with explicit event types, or provide supported types by implementing " +
          "hasKnownSupportedEventTypes() on listener");
    }
    if(listener.getKnownSupportedEventTypes().length > 0) {
      addListener(listener, priority, listener.getKnownSupportedEventTypes());
    }
  }    
  
  /** Add a listener for one or more event types that are presumably triggered by this producer 
   * 
   * @param listener to register
   * @param priority to apply for the combination of listener and event type(s)
   * @param eventTypes to register the listener for
   */
  protected final synchronized void addListener(
          final EventListener listener, EventListenerPriority priority, final EventType... eventTypes){

      for (EventType type : eventTypes) {
          listeners.putIfAbsent(type, new TreeMap<>());
          Map<EventListenerPriority, List<EventListener>> listenersByEventType = listeners.get(type);
          listenersByEventType.putIfAbsent(priority, new ArrayList<>());
          listenersByEventType.get(priority).add(listener);
      }
  }  
    
  /** Add a listener for one or more event types that are presumably triggered by this producer, with the default
   *  priority (low)
   * 
   * @param listener to register
   * @param eventTypes to register the listener for
   */
  protected final synchronized void addListener(final EventListener listener, final EventType... eventTypes){
    addListener(listener, EventListenerPriority.LOW, eventTypes);
  }    
  
  /** add a listener for one or more event types that are presumably triggered by this producer based on its
   *  known supported types.
   *  If no known types are not available the listener will not be added and a warning is issued
   * 
   * @param listener to add
   */
  protected final synchronized void addListener(final EventListener listener){
    if(!listener.hasKnownSupportedEventTypes()) {
      LOGGER.severe("IGNORED: unable to identify listener's supported event types, "
          + "consider registering with explicit event types, or provide supported types " +
              "by implementing hasKnownSupportedEventTypes() on listener");
    }
    addListener(listener, listener.getKnownSupportedEventTypes());
  }

  /** Add a listener internal to the owner of this producer, for the event types it is known to support. It receives
   * each of these events before the other listeners do, and stays registered when they are removed. Without known
   * supported event types the listener is not added and a warning is issued
   *
   * @param listener to add
   */
  protected final synchronized void addInternalListener(final EventListener listener){
    if(!listener.hasKnownSupportedEventTypes()) {
      LOGGER.severe("IGNORED: unable to identify internal listener's supported event types, "
          + "provide them by implementing getKnownSupportedEventTypes() on listener");
      return;
    }
    for (EventType type : listener.getKnownSupportedEventTypes()) {
      internalListeners.computeIfAbsent(type, t -> new ArrayList<>()).add(listener);
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
    Map<EventListenerPriority, List<EventListener>> listenersByEventType = this.listeners.get(eventType);
    for(EventListenerPriority priority : listenersByEventType.keySet()) {
      for (Iterator<EventListener> i = listenersByEventType.get(priority).iterator(); i.hasNext();){
        if (listener.equals(i.next())) {
          i.remove();
          result = true;
          break;
        }
      }
      if (listenersByEventType.get(priority).isEmpty()){
          listenersByEventType.remove(priority);
      } 
    }
    if (this.listeners.get(eventType).isEmpty()){
      this.listeners.remove(eventType);
    }     
    return result;
  }    

  /** Remove listener from all event types it is registered on
   * 
   * @param listener to remove
   * @return true when removed from one or more event types it was registered for, false otherwise
   */
  protected final synchronized boolean removeListener(final EventListener listener) {
    if(listener==null) {
      throw new IllegalArgumentException("listener cannot be null");
    }      
    boolean result = false;
    List<EventType> registeredEventTypes = new ArrayList<EventType>(this.listeners.keySet());    
    for(EventType eventType : registeredEventTypes) {
      result = removeListener(listener, eventType) || result;
    }
    return result;
  }

  /** Let derived class deal with the handling of the listener, where based on the derived event implementation
   * the listener's concrete class can be determined which in turn allows for calling the right event callback method
   * which is unknown at this base level since this event mechanism does not force a particular notification
   * method signature on its listener interface
   * 
   * @param eventListener to notify for the event
   * @param event to process for the listener
   */
  protected abstract void fireEvent(final EventListener eventListener, final Event event);

  /**
   * Transmit an event to all interested listeners, those internal to the owner of this producer first.
   * 
   * @param event the event fired
   */
  protected synchronized void fireEvent(final Event event){
    if(event==null) {
      throw new IllegalArgumentException("event cannot be null");
    }
    if(event.getType()==null) {
      throw new IllegalArgumentException("event type cannot be null");
    }

    if (internalListeners.containsKey(event.getType())){
      for (EventListener listener : new ArrayList<>(internalListeners.get(event.getType()))){
        this.fireEvent(listener, event);
      }
    }
    if (this.listeners.containsKey(event.getType())){
      // copy containers while iterating in case removeListener() is called during this method processing
      Map<EventListenerPriority, List<EventListener>> listenersForEventType = this.listeners.get(event.getType());
      Set<EventListenerPriority> availablePriorities = new TreeSet<>(listenersForEventType.keySet());
      for(EventListenerPriority priority : availablePriorities) {
        ArrayList<EventListener> listenersForEventAndPriority = new ArrayList<>(listenersForEventType.get(priority));
        for (EventListener listener: listenersForEventAndPriority){
          this.fireEvent(listener, event);              
        }  
      }      
    }
  }

  /**
   * Constructs a new EventProducer and checks for duplicate values in event types.
   */
  protected EventProducerImpl(){
    this.listeners = new HashMap<>();
  }

  /**
   * Remove all the listeners from this producer, apart from those internal to its owner
   */
  public synchronized void removeAllNonInternalListeners(){
    this.listeners = null;
    this.listeners = new HashMap<>();
  }

  /**
   * Verify if any listeners, including internal ones, are available
   *
   * @return true when present, false otherwise
   */
  public boolean hasListeners(){
      return !this.listeners.isEmpty() || !internalListeners.isEmpty();
  }

  /** Verify if one or more listeners, including internal ones, are registered for given event type
   *
   * @param eventType to verify
   * @return true when present, false otherwise
   */
  public boolean hasListener(EventType eventType) {
    return this.listeners.containsKey(eventType) || internalListeners.containsKey(eventType);
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
