package org.goplanit.utils.event;

import java.util.Set;
import java.util.function.BiConsumer;

/**
 * The producer of one kind of event, for an owner producing several kinds, so no kind needs a producer class of its
 * own
 * <p>
 * For example, the layer modifier produces intersection events and link segment type events. It holds one producer
 * per kind, each given the method its listeners hear events through and the event types it fires, its producer
 * interface's list:
 * </p>
 * <pre>
 * var intersectionEvents = new TypedEventProducer&lt;IntersectionModifierListener, IntersectionModificationEvent,
 *     IntersectionModifierEventType&gt;(IntersectionModifierListener::onIntersectionModifierEvent,
 *     IntersectionModifierEventProducer.FIRED_INTERSECTION_EVENT_TYPES);
 * intersectionEvents.addListener(listener);
 * intersectionEvents.fireEvent(new RemoveIntersectionEvent(modifier, intersection));
 * </pre>
 * <p>
 * One producer per kind, rather than one for all, as a listener of two kinds would otherwise be registered twice: it
 * would hear each event twice, and removing it through one kind would take it off the other. Each kind names its own
 * listener method, so one handler can listen to several kinds. The producer holds an {@link EventProducerImpl} rather
 * than extending it, as a typed {@code addListener(L)} would clash with the inherited
 * {@code addListener(EventListener)} once generics are erased.
 * </p>
 *
 * @author markr
 * @param <L> type of the listeners
 * @param <E> type of the events
 * @param <T> type of the event types
 */
public class TypedEventProducer<L extends EventListener, E extends Event, T extends EventType> {

  /** the producer holding the listeners and firing the events */
  private final EventProducerImpl producer;

  /** the event types fired, of this producer's kind */
  private final Set<T> firedEventTypes;

  /**
   * Constructor
   *
   * @param listenerMethod the method a listener hears an event through, such as
   *     {@code IntersectionModifierListener::onIntersectionModifierEvent}
   * @param firedEventTypes the event types the producer fires, such as its producer interface's list: it fires no
   *     other, and registers listeners for no other
   */
  public TypedEventProducer(BiConsumer<L, E> listenerMethod, Set<T> firedEventTypes) {
    this.firedEventTypes = Set.copyOf(firedEventTypes);
    this.producer = new EventProducerImpl(firedEventTypes) {

      /**
       * {@inheritDoc}
       */
      @SuppressWarnings("unchecked")
      @Override
      protected void fireEvent(EventListener listener, Event event) {
        listenerMethod.accept((L) listener, (E) event);
      }
    };
  }

  /**
   * Add a listener for every event type it names as supported
   *
   * @param listener to add
   */
  public void addListener(L listener) {
    producer.addListener(listener);
  }

  /**
   * Add a listener for one event type
   *
   * @param listener to add
   * @param eventType it listens to
   */
  public void addListener(L listener, T eventType) {
    producer.addListener(listener, eventType);
  }

  /**
   * Remove a listener from every event type
   *
   * @param listener to remove
   * @return true when it was registered
   */
  public boolean removeListener(L listener) {
    return producer.removeListener(listener);
  }

  /**
   * Remove a listener from one event type
   *
   * @param listener to remove
   * @param eventType it no longer listens to
   * @return true when it was registered for that type
   */
  public boolean removeListener(L listener, T eventType) {
    return producer.removeListener(listener, eventType);
  }

  /**
   * Add an internal listener, which hears an event before the other listeners do
   *
   * @param listener to add, for every event type it names as supported
   */
  public void addInternalListener(L listener) {
    producer.addInternalListener(listener);
  }

  /**
   * Remove an internal listener
   *
   * @param listener to remove
   * @return true when it was registered
   */
  public boolean removeInternalListener(L listener) {
    return producer.removeInternalListener(listener);
  }

  /**
   * Remove every listener but the internal ones, which stay registered
   */
  public void removeAllNonInternalListeners() {
    producer.removeAllNonInternalListeners();
  }

  /**
   * Whether any listener listens to an event type
   *
   * @param eventType to ask about
   * @return true when one does
   */
  public boolean hasListener(T eventType) {
    return producer.hasListener(eventType);
  }

  /**
   * Fire an event to the internal listeners of its type first, then the others
   *
   * @param event to fire
   */
  public void fireEvent(E event) {
    producer.fireEvent(event);
  }

  /**
   * The event types this producer fires, see {@link EventProducer#getFiredEventTypes()}
   *
   * @return unmodifiable set of the event types fired
   */
  public Set<T> getFiredEventTypes() {
    return firedEventTypes;
  }
}
