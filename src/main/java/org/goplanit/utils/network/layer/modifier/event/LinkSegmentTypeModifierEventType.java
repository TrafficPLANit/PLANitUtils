package org.goplanit.utils.network.layer.modifier.event;

import org.goplanit.utils.event.EventType;

/**
 * Event type for events fired by the macroscopic network layer modifier about link segment types
 *
 * @author markr
 */
public class LinkSegmentTypeModifierEventType extends EventType {

  /**
   * Constructor
   *
   * @param eventTypeName name of the event type
   */
  public LinkSegmentTypeModifierEventType(String eventTypeName) {
    super(eventTypeName);
  }
}
