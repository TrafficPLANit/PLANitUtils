package org.goplanit.utils.network.layer.macroscopic.intersection.modifier.event;

import org.goplanit.utils.event.EventType;

/**
 * Event type for events fired by the macroscopic network layer modifier about intersections
 *
 * @author markr
 *
 */
public class IntersectionModifierEventType extends EventType {

  /**
   * Constructor
   *
   * @param eventTypeName name of the event type
   */
  public IntersectionModifierEventType(String eventTypeName) {
    super(eventTypeName);
  }
}
