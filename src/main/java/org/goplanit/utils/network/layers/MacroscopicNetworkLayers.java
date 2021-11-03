package org.goplanit.utils.network.layers;

import org.goplanit.utils.network.layer.MacroscopicNetworkLayer;

/**
 * interface to manage macroscopic physical network layers, i.e., layers that contain a topologically meaningful representation in the form of nodes and links
 * 
 * @author markr
 *
 */
public interface MacroscopicNetworkLayers extends UntypedPhysicalNetworkLayers<MacroscopicNetworkLayer> {
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicNetworkLayerFactory getFactory();   
  
  /**
   * clone container
   */
  @Override
  public abstract MacroscopicNetworkLayers clone(); 
}
