package org.goplanit.utils.network.virtual.graph;

import org.goplanit.utils.graph.Vertex;
import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.zoning.Centroid;

/**
 * Centroid vertex represents manifestation of a centroid on a particular layer, where it materisalises as a vertex in the virtual network
 */
public interface CentroidVertex extends DirectedVertex {

  public abstract Centroid getParent();

  public abstract void setParent(Centroid parent);

  /**
   * Verify if the centroid vertex acts as a source (one can enter the network via here)
   *
   * @return true when source (has exit segments), false otherwise
   */
  public default boolean isSourceVertex(){
    return hasExitEdgeSegments();
  }

  /**
   * Verify if the centroid vertex acts as a sink (one can exit the network via here)
   *
   * @return true when sink (has entry segments), false otherwise
   */
  public default boolean isSinkVertex(){
    return hasEntryEdgeSegments();
  }

  /**
   * Verify if the centroid vertex acts as a sink and a source
   *
   * @return true when sink and source (has entry and exit segments), false otherwise
   */
  public default boolean isSinkAndSourceVertex(){
    return hasExitEdgeSegments() && hasEntryEdgeSegments();
  }
}
