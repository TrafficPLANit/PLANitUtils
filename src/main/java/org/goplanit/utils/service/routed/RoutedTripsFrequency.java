package org.goplanit.utils.service.routed;

import java.util.function.BiConsumer;

/**
 * Interface for wrapper container class around RoutedTrip instances that define a frequency based schedule.
 * 
 * @author markr
 *
 */
public interface RoutedTripsFrequency extends RoutedTrips<RoutedTripFrequency> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedTripFrequencyFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedTripsFrequency shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedTripsFrequency deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract RoutedTripsFrequency deepCloneWithMapping(BiConsumer<RoutedTripFrequency, RoutedTripFrequency> mapper);

}
