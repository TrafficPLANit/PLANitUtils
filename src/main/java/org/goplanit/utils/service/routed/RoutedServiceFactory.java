package org.goplanit.utils.service.routed;

import org.goplanit.utils.id.ManagedIdEntityFactory;

/**
 * Factory for creating routed service instances on underlying container
 *
 * @author markr
 */
public interface RoutedServiceFactory extends ManagedIdEntityFactory<RoutedService> {

  /**
   * Register a newly created instance on the underlying container
   *
   * @return created instance
   */
  public abstract RoutedService registerNew();
}
