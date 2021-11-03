package org.goplanit.utils.network.layers;

import org.goplanit.utils.network.layer.physical.UntypedPhysicalLayer;

/**
 * Interface to manage physical network layers, i.e., layers that contain a topologically meaningful representation in the form of nodes and links
 * 
 * 
 * @author markr
 *
 */
public interface UntypedPhysicalNetworkLayers<L extends UntypedPhysicalLayer<?,?,?>> extends UntypedDirectedGraphLayers<L> {
  
  /**
   * Number of nodes across all layers
   * 
   * @return number of nodes
   */
  @SuppressWarnings("rawtypes")
  public default long getNumberOfNodes() {
    long sum = 0;
    for (UntypedPhysicalLayer layer : this) {
      sum += layer.getNumberOfNodes();
    }
    return sum;
  }

  /**
   * Number of links across all layers
   * 
   * @return number of links
   */
  @SuppressWarnings("rawtypes")
  public default long getNumberOfLinks() {
    long sum = 0;
    for (UntypedPhysicalLayer layer : this) {
      sum += layer.getNumberOfLinks();
    }
    return sum;
  }

  /**
   * Number of link segments across all layers
   * 
   * @return number of link segments
   */
  @SuppressWarnings("rawtypes")
  public default long getNumberOfLinkSegments() {
    long sum = 0;
    for (UntypedPhysicalLayer layer : this) {
      sum += layer.getNumberOfLinkSegments();
    }
    return sum;
  }
  
  /**
   * clone container
   */
  @Override
  public abstract UntypedPhysicalNetworkLayers<L> clone();  

}
