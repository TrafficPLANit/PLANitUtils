package org.goplanit.utils.service.routed;

import org.goplanit.utils.id.ManagedIdEntities;

import java.time.LocalTime;
import java.util.stream.Stream;

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
  public abstract RoutedTripDepartures shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedTripDepartures deepClone();

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

  /** stream over departures but by ascending departure time rather than the underlying ids, which might not be in
   * the temporal departure time order
   *
   * @return departure time stream in acending departure time order
   */
  public default Stream<RoutedTripDeparture> streamAscDepartureTime(){
    return streamSorted(RoutedTripDeparture::getDepartureTime);
  }

}
