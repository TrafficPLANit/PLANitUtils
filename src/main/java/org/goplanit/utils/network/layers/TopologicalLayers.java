package org.goplanit.utils.network.layers;

import org.goplanit.utils.network.layer.TopologicalLayer;

/**
 * interface to manage topological layers, i.e., layers that contain a topologically meaningful representation, without enforcing how this is implemented.
 * 
 * @author markr
 *
 */
public interface TopologicalLayers<T extends TopologicalLayer> extends NetworkLayers<T> {

  /**
   * clone container
   */
  @Override
  public abstract TopologicalLayers<T> clone();
}
