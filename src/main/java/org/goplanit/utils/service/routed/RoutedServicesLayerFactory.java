package org.goplanit.utils.service.routed;

import org.goplanit.utils.id.ManagedIdEntityFactory;
import org.goplanit.utils.network.layer.ServiceNetworkLayer;

/**
 * Factory for creating routed services layer instances (on container)
 * 
 * @author markr
 */
public interface RoutedServicesLayerFactory extends ManagedIdEntityFactory<RoutedServicesLayer> {

  /**
   * Register a newly created instance on the underlying container
   * 
   * @param parentLayer the parent layer these routed services are built upon
   * @return created instance
   */
  public abstract RoutedServicesLayer registerNew(final ServiceNetworkLayer parentLayer);

}
