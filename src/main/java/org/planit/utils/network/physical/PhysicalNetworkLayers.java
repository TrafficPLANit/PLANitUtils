package org.planit.utils.network.physical;

import org.planit.utils.network.TopologicalLayers;
import org.planit.utils.network.layer.physical.PhysicalLayer;

/**
 * interface to manage topological layers, i.e., layers that contain a topologically meaningful representation in the form of nodes and links
 * 
 * @author markr
 *
 */
public interface PhysicalNetworkLayers<T extends PhysicalLayer<?,?,?>> extends TopologicalLayers<T> {

  /**
   * Number of nodes across all layers
   * 
   * @return number of nodes
   */
  public default long getNumberOfNodes() {
    long sum = 0;
    for (T layer : this) {
      sum += layer.getNumberOfNodes();
    }
    return sum;
  }

  /**
   * Number of links across all layers
   * 
   * @return number of links
   */
  public default long getNumberOfLinks() {
    long sum = 0;
    for (T layer : this) {
      sum += layer.getNumberOfLinks();
    }
    return sum;
  }

  /**
   * Number of link segments across all layers
   * 
   * @return number of link segments
   */
  public default long getNumberOfLinkSegments() {
    long sum = 0;
    for (T layer : this) {
      sum += layer.getNumberOfLinkSegments();
    }
    return sum;
  }

}
