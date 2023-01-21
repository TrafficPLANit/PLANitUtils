package org.goplanit.utils.network.layers;

import org.goplanit.utils.id.ManagedIdEntityFactory;
import org.goplanit.utils.network.layer.MacroscopicNetworkLayer;
import org.goplanit.utils.network.layer.RoutedServiceLayer;

/** Factory interface for creating service network layers
 * 
 * @author markr
 *
 */
public interface ServiceNetworkLayerFactory extends ManagedIdEntityFactory<RoutedServiceLayer> {
 
  /** Create a new service network layer  
   *
   * @param parentLayer the physical network layer this service layer is built upon
   * @return created service layer
   */
  public abstract RoutedServiceLayer registerNew(MacroscopicNetworkLayer parentLayer);
  
}
