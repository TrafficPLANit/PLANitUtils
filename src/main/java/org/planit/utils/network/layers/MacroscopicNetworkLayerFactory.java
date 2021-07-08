package org.planit.utils.network.layers;

import org.planit.utils.id.ContainerisedManagedIdEntityFactory;
import org.planit.utils.network.layer.MacroscopicNetworkLayer;

/** Factory interface for creating macroscopic network layers
 * 
 * @author markr
 *
 */
public interface MacroscopicNetworkLayerFactory extends ContainerisedManagedIdEntityFactory<MacroscopicNetworkLayer> {
 
  /** Create a new macroscopic network layer instance 
   *  
   * @return created instance
   */
  public abstract MacroscopicNetworkLayer registerNew();   
  
}
