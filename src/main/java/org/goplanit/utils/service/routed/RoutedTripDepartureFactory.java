package org.goplanit.utils.service.routed;

import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.id.ManagedIdEntityFactory;
import org.goplanit.utils.id.ManagedIdEntityFactoryImpl;
import org.goplanit.utils.time.ExtendedLocalTime;

/**
 * Factory for creating routed trip departure instances (on container)
 * 
 * @author markr
 */
public interface RoutedTripDepartureFactory extends ManagedIdEntityFactory<RoutedTripDeparture> {

  /**
   * Register a newly created instance on the underlying container
   * 
   * @param departureTime the departure time (which is allowed to be beyond midnight of that day)
   * @return created instance
   */
  public abstract RoutedTripDeparture registerNew(final ExtendedLocalTime departureTime);

}
