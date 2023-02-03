package org.goplanit.utils.network.layers;

import org.goplanit.utils.network.layer.UntypedDirectedGraphLayer;

/**
 * Interface to manage physical network layers, i.e., layers that contain a topologically meaningful representation in the form of nodes and links
 * 
 * 
 * @author markr
 *
 */
public interface UntypedDirectedGraphLayers<L extends UntypedDirectedGraphLayer<?,?,?>> extends TopologicalLayers<L> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UntypedDirectedGraphLayers<L> shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UntypedDirectedGraphLayers<L> deepClone();
}
