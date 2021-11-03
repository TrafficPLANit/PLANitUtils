package org.goplanit.utils.zoning.modifier.event;

import org.goplanit.utils.event.EventListener;

/** To serve as base listener class for all zoning modification events, where its onX method provides any
 *  zoning modification event as parameter
 *  
 * @author markr
 *
 */
public interface ZoningModifierListener extends EventListener {
  
  /** Notify method for zoning modification events
   * 
   * @param event representing the zoning modification event at hand
   */
  public abstract void onZoningModifierEvent(ZoningModificationEvent event);
  
}
