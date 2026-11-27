package org.goplanit.utils.graph.modifier.event;

import org.goplanit.utils.event.EventImpl;
import org.goplanit.utils.graph.directed.BannedTurn;
import org.goplanit.utils.graph.modifier.DirectedGraphModifier;

/**
 * Event for when a movement has been removed from a (sub) graph
 * 
 * @author markr
 *
 */
public class RemoveBannedTurnEvent extends EventImpl implements DirectedGraphModificationEvent {

  /** event type fired off when a movement has been removed */
  public static final DirectedGraphModifierEventType EVENT_TYPE =
      new DirectedGraphModifierEventType("DIRECTEDGRAPHMODIFIER.MOVEMENT.REMOVE");

  /**
   * Constructor
   *
   * @param source             directed graph modifier firing the event
   * @param removedMovement    movement that is removed
   */
  public RemoveBannedTurnEvent(DirectedGraphModifier source, BannedTurn removedMovement) {
    super(EVENT_TYPE, source, removedMovement);
  }

  /**
   * The removed movement
   * 
   * @return removed movement
   */
  public BannedTurn getRemovedBannedTurn() {
    return (BannedTurn) getContent()[0];
  }

}
