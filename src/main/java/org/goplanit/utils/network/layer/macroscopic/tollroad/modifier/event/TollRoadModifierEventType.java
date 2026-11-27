package org.goplanit.utils.network.layer.macroscopic.tollroad.modifier.event;

import org.goplanit.utils.event.EventType;

/**
 * Event type for events fired by the macroscopic network layer modifier about toll roads
 * <p>
 * A type of its own, so a listener registers for the toll road events alone, as for the intersection events.
 * </p>
 *
 * @author markr
 */
public class TollRoadModifierEventType extends EventType {

  /**
   * Constructor
   *
   * @param eventTypeName name of the event type
   */
  public TollRoadModifierEventType(String eventTypeName) {
    super(eventTypeName);
  }
}
