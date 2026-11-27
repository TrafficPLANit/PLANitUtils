package org.goplanit.utils.service.routed.modifier;

import org.goplanit.utils.event.EventImpl;
import org.goplanit.utils.service.routed.RoutedServicesLayer;

/**
 * Base wrapper for all events fired with only routed service layer as a source
 *
 *
 * @author markr
 *
 */
public abstract class ModifiedRoutedServicesLayerEventImpl extends EventImpl
    implements RoutedServicesModificationEvent {

  /**
   * Wrapper for a modified routed service layer event
   *
   * @param eventType type of the event
   * @param source zoning modifier firing the event
   * @param routedServicesLayer to use
   */
  protected ModifiedRoutedServicesLayerEventImpl(
      RoutedServicesModifierEventType eventType,
      RoutedServicesLayerModifier source,
      RoutedServicesLayer routedServicesLayer) {
    super(eventType, source, routedServicesLayer);
  }

  /**
   * The zoning on which the modification took place
   *
   * @return modified routed service layer this pertains to
   */
  public RoutedServicesLayer getModifiedRoutedServicesLayer() {
    return (RoutedServicesLayer) getContent()[0];
  }

}