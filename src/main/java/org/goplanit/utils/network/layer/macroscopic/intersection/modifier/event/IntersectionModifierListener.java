package org.goplanit.utils.network.layer.macroscopic.intersection.modifier.event;

import org.goplanit.utils.event.EventListener;

/**
 * Listener for events fired by the macroscopic network layer modifier about intersections
 *
 * @author markr
 *
 */
public interface IntersectionModifierListener extends EventListener {

  /**
   * Invoked when an intersection event it is registered for is fired
   *
   * @param event fired
   */
  public abstract void onIntersectionModifierEvent(IntersectionModificationEvent event);
}
