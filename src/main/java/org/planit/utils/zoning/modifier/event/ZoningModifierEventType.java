package org.planit.utils.zoning.modifier.event;

import org.planit.utils.event.EventType;

/** zoning modifier event type class.
 * 
 * @author markr
 *
 */
public class ZoningModifierEventType extends EventType {
  
  public static final ZoningModifierEventType test = new ZoningModifierEventType("TEST"); 

  /** Constructor
   * 
   * @param eventTypeName
   */
  public ZoningModifierEventType(String eventTypeName) {
    super(eventTypeName);
  }

}
