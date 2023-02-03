package org.goplanit.utils.network.layers;

import org.goplanit.utils.network.layer.MacroscopicNetworkLayer;
import org.goplanit.utils.network.layer.UntypedDirectedGraphLayer;

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
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicNetworkLayers shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicNetworkLayers deepClone();
}
