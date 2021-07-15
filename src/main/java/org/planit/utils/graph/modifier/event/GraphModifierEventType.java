package org.planit.utils.graph.modifier.event;

/** graph modifier event type class.
 * 
 * @author markr
 *
 */
public class GraphModifierEventType extends org.planit.utils.event.EventType {
  
  public static final GraphModifierEventType test = new GraphModifierEventType("TEST"); 

  /** Constructor
   * 
   * @param eventTypeName
   */
  public GraphModifierEventType(String eventTypeName) {
    super(eventTypeName);
  }

}
