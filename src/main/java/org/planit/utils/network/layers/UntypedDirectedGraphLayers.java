package org.planit.utils.network.layers;

import org.planit.utils.network.layer.UntypedDirectedGraphLayer;

/**
 * Interface to manage physical network layers, i.e., layers that contain a topologically meaningful representation in the form of nodes and links
 * 
 * 
 * @author markr
 *
 */
public interface UntypedDirectedGraphLayers<L extends UntypedDirectedGraphLayer<?,?,?,?,?,?>> extends TopologicalLayers<L> {
  
  /**
   * clone container
   */
  @Override
  public abstract UntypedDirectedGraphLayers<L> clone();
}
