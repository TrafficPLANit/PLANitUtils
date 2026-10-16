package org.goplanit.utils.network.virtual.graph;

import org.goplanit.utils.graph.directed.DirectedVertex;

/**
 * Connectoid directed vertex
 * <p>
 *   Typically these are only used in conjugate form as
 *  * virtual networks typically run from a centroid vertex to a physical vertex obviating the need for this type of vertex
 * </p>
 * 
 * @author markr
 *
 */
public interface ConnectoidDirectedVertex extends DirectedVertex {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidDirectedVertex shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidDirectedVertex deepClone();

}
