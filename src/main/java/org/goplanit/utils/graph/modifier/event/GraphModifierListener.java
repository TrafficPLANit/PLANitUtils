package org.goplanit.utils.graph.modifier.event;

import org.goplanit.utils.event.EventListener;

/** To serve as base listener class for all graph modification events, where its onX method provides any
 *  graph modification event as parameter
 *  
 * @author markr
 *
 */
public interface GraphModifierListener extends EventListener {
  
  /** Notify method for graph modification events
   * 
   * @param event representing the graph modification event at hand
   */
  public abstract void onGraphModificationEvent(GraphModificationEvent event);
  
}
