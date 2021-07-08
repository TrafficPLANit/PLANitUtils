package org.planit.utils.network.layers;

import org.planit.utils.id.ContainerisedManagedIdEntityFactory;
import org.planit.utils.network.layer.MacroscopicNetworkLayer;
import org.planit.utils.network.layer.ServiceNetworkLayer;

/** Factory interface for creating service network layers
 * 
 * @author markr
 *
 */
public interface ServiceNetworkLayerFactory extends ContainerisedManagedIdEntityFactory<ServiceNetworkLayer> {
 
  /** Create a new service network layer  
   * 
   * @param parentLayer this service layer is built upon 
   * @return created service layer
   */
  public abstract ServiceNetworkLayer registerNew(MacroscopicNetworkLayer parentLayer);   
  
}
