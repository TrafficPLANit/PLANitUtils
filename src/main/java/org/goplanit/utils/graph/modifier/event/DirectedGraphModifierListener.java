package org.goplanit.utils.graph.modifier.event;

/** To serve as base listener class for all directed graph modification events, where its onX method provides any
 *  graph modification event as parameter
 *  
 * @author markr
 *
 */
public interface DirectedGraphModifierListener extends GraphModifierListener {
  
  /** Notify method for directed graph modification events
   * 
   * @param event representing the directed graph modification event at hand
   */
  public abstract void onDirectedGraphModificationEvent(DirectedGraphModificationEvent event);
  
}
