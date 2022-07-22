package org.goplanit.utils.network.layers;

import org.goplanit.utils.network.layer.NetworkLayer;
import org.goplanit.utils.network.layer.ServiceNetworkLayer;

/**
 * interface to manage service network layers, i.e., layers that contain service networks on top of other network layers
 * 
 * @author markr
 *
 */
public interface ServiceNetworkLayers extends UntypedDirectedGraphLayers<ServiceNetworkLayer> {
    
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ServiceNetworkLayerFactory getFactory();

  /**
   * Check if each layer itself is empty
   *
   * @return true when all empty false otherwise
   */
  public default boolean isEachLayerEmpty() {
    boolean eachLayerEmpty = true;
    for (var layer : this) {
      if (!layer.isEmpty()) {
        eachLayerEmpty = false;
        break;
      }
    }
    return eachLayerEmpty;
  }
  
  /**
   * clone container
   */
  @Override
  public abstract ServiceNetworkLayers clone(); 
}
