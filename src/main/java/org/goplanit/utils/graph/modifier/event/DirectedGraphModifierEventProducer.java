package org.goplanit.utils.graph.modifier.event;

import java.util.Set;

/**
 * Interface dedicated to the {@code DirectedGraphModifier} event related exposed methods. Specifies method
 * signatures for registering the appropriate listeners and event types that are supported on a graph modifier,
 * and lists the event types it fires, see {@link #FIRED_DIRECTED_GRAPH_EVENT_TYPES}.
 *
 * @author markr
 *
 */
public interface DirectedGraphModifierEventProducer extends GraphModifierEventProducer {

  /**
   * The event types a directed graph modifier, on top of the graph events of the graph modifier it holds fires:
   * {@link BreakEdgeSegmentEvent}, {@link MoveEdgeEndsAtVertexEvent},
   * {@link RecreatedDirectedGraphEntitiesManagedIdsEvent}, {@link RemoveBannedTurnEvent},
   * {@link RemoveEdgeSegmentEvent}
   */
  public static final Set<DirectedGraphModifierEventType> FIRED_DIRECTED_GRAPH_EVENT_TYPES = Set.of(
      BreakEdgeSegmentEvent.EVENT_TYPE, MoveEdgeEndsAtVertexEvent.EVENT_TYPE,
      RecreatedDirectedGraphEntitiesManagedIdsEvent.EVENT_TYPE, RemoveBannedTurnEvent.EVENT_TYPE,
      RemoveEdgeSegmentEvent.EVENT_TYPE);

  /**
   * The event types of this kind the producer fires, see {@link #FIRED_DIRECTED_GRAPH_EVENT_TYPES}
   *
   * @return unmodifiable set of the event types fired
   */
  public default Set<DirectedGraphModifierEventType> getFiredDirectedGraphEventTypes() {
    return FIRED_DIRECTED_GRAPH_EVENT_TYPES;
  }
    
}
