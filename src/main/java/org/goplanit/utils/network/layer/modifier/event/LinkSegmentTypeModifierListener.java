package org.goplanit.utils.network.layer.modifier.event;

import org.goplanit.utils.event.EventListener;

/**
 * Listener for events fired by the macroscopic network layer modifier about link segment types
 *
 * @author markr
 */
public interface LinkSegmentTypeModifierListener extends EventListener {

  /**
   * Invoked when a link segment type event it is registered for is fired
   *
   * @param event fired
   */
  public abstract void onLinkSegmentTypeModifierEvent(LinkSegmentTypeModificationEvent event);
}
