package org.goplanit.utils.service.routed;

import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.id.ManagedIdEntityFactory;
import org.goplanit.utils.id.ManagedIdEntityFactoryImpl;

/**
 * Factory for creating routed trips of type T
 * 
 * @param <T> type of routed trip
 * 
 * @author markr
 */
public interface RoutedTripFactory<T extends RoutedTrip> extends ManagedIdEntityFactory<T> {

  /**
   * Register a newly created instance on the underlying container
   *
   * @return created instance
   */
  public abstract T registerNew();

}
