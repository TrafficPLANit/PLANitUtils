package org.goplanit.utils.network.layer.modifier.event;

import org.goplanit.utils.event.EventProducer;

/** Interface dedicated to the link segment type related events of the macroscopic network layer modifier. Specifies
 * method signatures for registering the appropriate listeners and event types that are supported.
 *
 * @author markr
 */
public interface LinkSegmentTypeModifierEventProducer extends EventProducer {

  /**
   * Register listener for all its supported event types fired about link segment types
   *
   *  @param listener to register
   */
  public abstract void addListener(final LinkSegmentTypeModifierListener listener);

  /**
   * Register listener for events fired about link segment types
   *
   *  @param listener to register
   *  @param eventType to register listener for
   */
  public abstract void addListener(
      final LinkSegmentTypeModifierListener listener, final LinkSegmentTypeModifierEventType eventType);

  /**
   * Remove listener for given event type
   *
   *  @param listener to remove
   *  @param eventType to unregister listener for
   */
  public abstract void removeListener(
      final LinkSegmentTypeModifierListener listener, final LinkSegmentTypeModifierEventType eventType);

  /**
   * Remove listener for all event types it is registered for
   *
   *  @param listener to remove
   */
  public abstract void removeListener(final LinkSegmentTypeModifierListener listener);

  /**
   * Register a listener internal to the owner of the producer for all its supported event types: it hears each event
   * before the other listeners and stays registered when they are removed
   *
   * @param listener to register
   */
  public abstract void addInternalListener(final LinkSegmentTypeModifierListener listener);

  /**
   * Remove a listener internal to the owner of the producer, for all event types it is registered for
   *
   * @param listener to remove
   */
  public abstract void removeInternalListener(final LinkSegmentTypeModifierListener listener);
}
