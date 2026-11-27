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

import org.goplanit.utils.exceptions.ExceptionUtils;
import org.goplanit.utils.exceptions.PlanItRunTimeException;

/**
 * Base class of event producers. It provides protected methods to register and remove listeners and to fire a generic
 * event. Event producing classes should extend it with more user friendly public methods, whose signatures enforce the
 * event and listener combinations that suit the class, and use this implementation internally.
 * <p>
 * A producer is given the event types it fires when made, usually its producer interface's list, and holds itself and
 * its listeners to them: it refuses to fire an event of another type, registers a listener only for the types it fires,
 * and refuses a listener supporting none of them or a type it never fires. So an event added without listing it fails
 * the first test firing it, and a listener registered for what is never fired, a coding mistake, is refused rather
 * than left waiting.
 * </p>
 * 
 * @author markr
 */
public abstract class EventProducerImpl{
  
  /** the logger to use */
  private static final Logger LOGGER = Logger.getLogger(EventProducerImpl.class.getCanonicalName());

  /** listeners internal to the owner of this producer by event type, in order of registration; they receive each
   * event before the other listeners and are not removed with them */
  private final Map<EventType, List<EventListener>> internalListeners = new HashMap<>();

  /** the event types this producer fires; it fires no other, and registers listeners for no other */
  private final Set<EventType> firedEventTypes;

  /**
   * The types among a listener's supported ones that this producer fires, refusing the listener when there are none
   *
   * @param listener being registered for every type it supports
   * @return the supported types fired, in the listener's order
   * @throws IllegalArgumentException when the producer fires none of them
   */
  private EventType[] findFiredSupportedEventTypes(final EventListener listener) {
    var fired = listener.findKnownSupportedEventTypesAmong(firedEventTypes);
    ExceptionUtils.throwIllegalArgumentIf(fired.length == 0,
        "Listener %s refused by producer %s: it supports none of the event types the producer fires, %s",
        listener.getClass().getName(), getClass().getName(), firedEventTypes);
    return fired;
  }
  
  /** The collection of interested listeners by event and priority */
  protected Map<EventType, Map<EventListenerPriority, List<EventListener>>> listeners;

  /** Add a listener for those of its known supported event types this producer fires
   *
   * @param listener to register
   * @param priority to apply for the combination of listener and event type(s)
   * @throws IllegalArgumentException when the producer fires none of the types the listener supports
   */
  protected final synchronized void addListener(final EventListener listener, EventListenerPriority priority){
    if(!listener.hasKnownSupportedEventTypes()) {
      LOGGER.severe("IGNORED: unable to identify listener's supported event types, "
          + "consider registering with explicit event types, or provide supported types by implementing " +
          "hasKnownSupportedEventTypes() on listener");
      return;
    }
    addListener(listener, priority, findFiredSupportedEventTypes(listener));
  }

  /** Add a listener for one or more event types this producer fires
   *
   * @param listener to register
   * @param priority to apply for the combination of listener and event type(s)
   * @param eventTypes to register the listener for
   * @throws IllegalArgumentException when the producer does not fire one of the types, registering it for none
   */
  protected final synchronized void addListener(
          final EventListener listener, EventListenerPriority priority, final EventType... eventTypes){

      for (EventType type : eventTypes) {
        ExceptionUtils.throwIllegalArgumentIf(!firedEventTypes.contains(type),
            "Listener %s refused for event type %s by producer %s: the producer does not fire it, only %s",
            listener.getClass().getName(), type, getClass().getName(), firedEventTypes);
      }
      for (EventType type : eventTypes) {
          listeners.putIfAbsent(type, new TreeMap<>());
          Map<EventListenerPriority, List<EventListener>> listenersByEventType = listeners.get(type);
          listenersByEventType.putIfAbsent(priority, new ArrayList<>());
          listenersByEventType.get(priority).add(listener);
      }
  }  
    
  /** Add a listener for one or more event types this producer fires, with the default priority (low)
   * 
   * @param listener to register
   * @param eventTypes to register the listener for
   * @throws IllegalArgumentException when the producer does not fire one of the types, registering it for none
   */
  protected final synchronized void addListener(final EventListener listener, final EventType... eventTypes){
    addListener(listener, EventListenerPriority.LOW, eventTypes);
  }    
  
  /** Add a listener for those of its known supported event types this producer fires, with the default priority
   *  (low). Without known supported types the listener is not added and a warning is issued
   *
   * @param listener to add
   * @throws IllegalArgumentException when the producer fires none of the types the listener supports
   */
  protected final synchronized void addListener(final EventListener listener){
    addListener(listener, EventListenerPriority.LOW);
  }

  /** Add a listener internal to the owner of this producer, for those of the event types it is known to support this
   * producer fires. It receives each of these events before the other listeners do, and stays registered when they are
   * removed. Without known supported event types the listener is not added and a warning is issued
   *
   * @param listener to add
   * @throws IllegalArgumentException when the producer fires none of the types the listener supports
   */
  protected final synchronized void addInternalListener(final EventListener listener){
    if(!listener.hasKnownSupportedEventTypes()) {
      LOGGER.severe("IGNORED: unable to identify internal listener's supported event types, "
          + "provide them by implementing getKnownSupportedEventTypes() on listener");
      return;
    }
    for (EventType type : findFiredSupportedEventTypes(listener)) {
      internalListeners.computeIfAbsent(type, t -> new ArrayList<>()).add(listener);
    }
  }

  /**
   * Remove a listener internal to the owner of this producer, for all event types it is registered for
   *
   * @param listener to remove
   * @return true when removed from one or more event types, false otherwise
   */
  protected final synchronized boolean removeInternalListener(final EventListener listener) {
    boolean removed = false;
    for (var listenersOfType : internalListeners.values()) {
      removed = listenersOfType.removeIf(registered -> registered == listener) || removed;
    }
    internalListeners.values().removeIf(List::isEmpty);
    return removed;
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

  /** Let the derived class notify the listener. From the derived event it can tell the listener's concrete class, and
   * so call the right callback method. That method is unknown here, as this event mechanism does not force a
   * notification method signature on its listener interface
   * 
   * @param eventListener to notify for the event
   * @param event to process for the listener
   */
  protected abstract void fireEvent(final EventListener eventListener, final Event event);

  /**
   * Transmit an event to all interested listeners, those internal to the owner of this producer first.
   *
   * @param event the event fired
   * @throws PlanItRunTimeException when the producer does not list the event's type among those it fires, before any
   *     listener hears it
   */
  protected synchronized void fireEvent(final Event event){
    if(event==null) {
      throw new IllegalArgumentException("event cannot be null");
    }
    if(event.getType()==null) {
      throw new IllegalArgumentException("event type cannot be null");
    }
    PlanItRunTimeException.throwIf(!firedEventTypes.contains(event.getType()),
        "Event of type %s refused by producer %s: it does not list the type among those it fires, %s",
        event.getType(), getClass().getName(), firedEventTypes);

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
   * Constructor
   *
   * @param firedEventTypes the event types this producer fires, usually its producer interface's list: it fires no
   *     other, and registers listeners for no other
   */
  protected EventProducerImpl(final Set<? extends EventType> firedEventTypes){
    this.listeners = new HashMap<>();
    this.firedEventTypes = Set.copyOf(firedEventTypes);
  }

  /**
   * The event types this producer fires, see {@link EventProducer#getFiredEventTypes()}
   *
   * @return unmodifiable set of the event types fired
   */
  public Set<EventType> getFiredEventTypes() {
    return firedEventTypes;
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
