package org.goplanit.utils.graph.modifier.event;

import org.goplanit.utils.event.EventProducer;
import org.goplanit.utils.graph.modifier.GraphModifier;
import java.util.Set;

/**
 * Interface dedicated to the {@link GraphModifier} event related exposed methods. Specifies method
 * signatures for registering the appropriate listeners and event types that are supported on a graph modifier,
 * and lists the event types it fires, see {@link #FIRED_GRAPH_EVENT_TYPES}.
 *
 * @author markr
 *
 */
public interface GraphModifierEventProducer extends EventProducer{

  /**
   * The event types a graph modifier fires: {@link BreakEdgeEvent}, {@link RecreatedGraphEntitiesManagedIdsEvent},
   * {@link RemoveEdgeEvent}, {@link RemoveGraphEntityEvent}, {@link RemoveVertexEvent}
   */
  public static final Set<GraphModifierEventType> FIRED_GRAPH_EVENT_TYPES = Set.of(
      BreakEdgeEvent.EVENT_TYPE, RecreatedGraphEntitiesManagedIdsEvent.EVENT_TYPE, RemoveEdgeEvent.EVENT_TYPE,
      RemoveGraphEntityEvent.EVENT_TYPE, RemoveVertexEvent.EVENT_TYPE);

  /**
   * The event types of this kind the producer fires, see {@link #FIRED_GRAPH_EVENT_TYPES}
   *
   * @return unmodifiable set of the event types fired
   */
  public default Set<GraphModifierEventType> getFiredGraphEventTypes() {
    return FIRED_GRAPH_EVENT_TYPES;
  }
  
  /**
   * Register listener for all its supported types fired by the graph modifier
   * 
   *  @param listener to register
   */
  public abstract void addListener(GraphModifierListener listener);

  /**
   * Register listeners for events fired by the graph modifier
   * 
   *  @param listener to register
   *  @param eventType to register listener for
   */
  public abstract void addListener(GraphModifierListener listener, GraphModifierEventType eventType);
  
  /**
   * Remove listener for given event type
   * 
   *  @param listener to remove
   *  @param eventType to unregister listener for
   */
  public abstract void removeListener(GraphModifierListener listener, GraphModifierEventType eventType);  
  
  /**
   * Remove listener for all event types it is registered for
   * 
   *  @param listener to remove
   */
  public abstract void removeListener(GraphModifierListener listener);

  /**
   * Register a listener internal to the owner of the producer for all its supported types: it hears each event before
   * the other listeners and stays registered when they are removed, see {@link #removeAllNonInternalListeners()}
   *
   * @param listener to register
   */
  public abstract void addInternalListener(GraphModifierListener listener);

  /**
   * Remove a listener internal to the owner of the producer, for all event types it is registered for
   *
   * @param listener to remove
   */
  public abstract void removeInternalListener(GraphModifierListener listener);

  /**
   * Remove all registered listeners, apart from those internal to the owner of the producer
   */
  public abstract void removeAllNonInternalListeners();    
  
}
