package org.goplanit.utils.network.layers;

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
   * clone container
   */
  @Override
  public abstract ServiceNetworkLayers clone(); 
}
