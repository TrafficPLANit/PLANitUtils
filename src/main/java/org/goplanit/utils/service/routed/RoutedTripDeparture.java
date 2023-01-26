package org.goplanit.utils.service.routed;

import org.goplanit.utils.id.*;
import org.goplanit.utils.time.ExtendedLocalTime;

import java.time.LocalTime;

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
   * Departure time of the trip this instance is stored on
   *
   * @return departure time
   */
  public abstract ExtendedLocalTime getDepartureTime();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedTripDeparture clone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedTripDeparture deepClone();

  /**
   * Depart later by the given amount
   *
   * @param departureTimeIncrease to apply
   */
  public abstract void departLater(LocalTime departureTimeIncrease);

  /**
   * Depart earlier by the given amount
   *
   * @param departureTimeDecrease to apply
   */
  public abstract void departEarlier(LocalTime departureTimeDecrease);
}
