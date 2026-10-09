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

  /**
   * uses layer modifier instead of plain recreation in isolation to allow for interactions between users indexing
   * by id
   */
  @Override
  public default void recreateIds() {
    for (var layer : this) {
      layer.getLayerModifier().recreateManagedIdEntities();
    }
  }
}
