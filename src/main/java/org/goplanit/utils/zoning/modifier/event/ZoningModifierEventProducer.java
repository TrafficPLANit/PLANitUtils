package org.goplanit.utils.zoning.modifier.event;

import org.goplanit.utils.event.EventProducer;

/** Interface dedicated to the {@link ZoningModifier} event related exposed methods. Specifies method signatures for registering
 * the appropriate listeners and event types that are supported on a zoning modifier.
 *  
 * @author markr
 *
 */
public interface ZoningModifierEventProducer extends EventProducer {

  /**
   * Register listeners for events fired by a zoning modifier
   * 
   *  @param listener to register
   *  @param eventType to register listener for
   */
  public abstract void addListener(ZoningModifierListener listener, ZoningModifierEventType eventType);
  
  /**
   * Remove listener for given event type
   * 
   *  @param listener to remove
   *  @param eventType to unregister listener for
   */
  public abstract void removeListener(ZoningModifierListener listener, ZoningModifierEventType eventType);  
  
  /**
   * Remove listener for all event types it is registered for
   * 
   *  @param listener to remove
   *  @param eventType to unregister listener for
   */
  public abstract void removeListener(ZoningModifierListener listener);    
}
