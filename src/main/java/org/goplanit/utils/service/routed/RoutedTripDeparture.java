package org.goplanit.utils.service.routed;

import org.goplanit.utils.id.*;
import org.goplanit.utils.time.ExtendedLocalTime;

/**
 * A representation of a departure within a routed trip
 * 
 * @author markr
 *
 */
public interface RoutedTripDeparture extends ExternalIdAble, ManagedId {

  /** id class for generating ids */
  public static final Class<RoutedTripDeparture> ROUTED_TRIP_DEPARTURE_ID_CLASS = RoutedTripDeparture.class;

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<RoutedTripDeparture> getIdClass();

  /**
   * {@inheritDoc}
   */
  @Override
  public RoutedTripDeparture clone();
}
