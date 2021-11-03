package org.goplanit.utils.network.layers;

import org.goplanit.utils.id.ManagedIdEntityFactory;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.MacroscopicNetworkLayer;

/** Factory interface for creating macroscopic network layers
 * 
 * @author markr
 *
 */
public interface MacroscopicNetworkLayerFactory extends ManagedIdEntityFactory<MacroscopicNetworkLayer> {
 
  /** Create a new macroscopic network layer instance 
   *  
   * @return created instance
   */
  public abstract MacroscopicNetworkLayer registerNew();   
  
  /** Create a new macroscopic network layer instance 
   *  
   * @param supportedModes to use 
   * @return created instance
   */
  public abstract MacroscopicNetworkLayer registerNew(Mode... supportedModes);   
  
}
