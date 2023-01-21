package org.goplanit.utils.service.routed;

import org.goplanit.utils.id.ManagedIdEntities;

import java.time.LocalTime;

/**
 * A container class for departures registered on a schedule based routed trip
 * 
 * @author markr
 *
 */
public interface RoutedTripDepartures extends ManagedIdEntities<RoutedTripDeparture> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedTripDepartures clone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedTripDepartureFactory getFactory();

  /**
   * Make all departures of the routed trip departure depart later by the given amount
   *
   * @param departureTimeIncrease to apply
   */
  public abstract void allDepartLaterBy(LocalTime departureTimeIncrease);

  /**
   * Make all departures of the routed trip departure depart later by the given amount
   *
   * @param departureTimeIncrease to apply
   */
  public abstract void allDepartEarlierBy(LocalTime departureTimeIncrease);
}
