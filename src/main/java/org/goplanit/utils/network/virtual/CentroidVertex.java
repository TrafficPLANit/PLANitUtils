package org.goplanit.utils.network.virtual;

import org.goplanit.utils.graph.Vertex;
import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.zoning.Centroid;

/**
 * Centroid vertex represents manifestation of a centroid on a particular layer, where it materisalises as a vertex in the virtual network
 */
public interface CentroidVertex extends DirectedVertex {

  public abstract Centroid getParent();

  public abstract void setParent(Centroid parent);
}
