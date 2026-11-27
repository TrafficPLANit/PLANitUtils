package org.goplanit.utils.network.layer.macroscopic.tollroad.modifier.event;

import org.goplanit.utils.event.EventListener;

/**
 * Listener for events fired by the macroscopic network layer modifier about toll roads
 * <p>
 * For example a network pricing listening for removed toll roads, so its schemes follow the network they charge on.
 * </p>
 *
 * @author markr
 */
public interface TollRoadModifierListener extends EventListener {

  /**
   * Invoked when a toll road event it is registered for is fired
   *
   * @param event fired
   */
  public abstract void onTollRoadModifierEvent(TollRoadModificationEvent event);
}
