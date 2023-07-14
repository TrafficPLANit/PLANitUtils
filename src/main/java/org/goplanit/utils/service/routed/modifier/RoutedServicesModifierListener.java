package org.goplanit.utils.service.routed.modifier;

import org.goplanit.utils.event.EventListener;

/** To serve as base listener class for all routed services modification events, where its onX method provides any
 *  routed services modification event as parameter
 *  
 * @author markr
 *
 */
public interface RoutedServicesModifierListener extends EventListener {
  
  /** Notify method for zoning modification events
   * 
   * @param event representing the zoning modification event at hand
   */
  public abstract void onRoutedServicesModifierEvent(RoutedServicesModificationEvent event);
  
}
