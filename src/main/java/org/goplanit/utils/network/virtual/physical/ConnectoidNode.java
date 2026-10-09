package org.goplanit.utils.network.virtual.physical;

import org.goplanit.utils.network.layer.physical.Node;
import org.goplanit.utils.network.virtual.graph.ConnectoidDirectedVertex;
import org.goplanit.utils.network.virtual.physical.conjugate.ConjugateConnectoidLink;

import java.util.Collection;

/**
 * Connectoid Node is a node but not all nodes are connectoid nodes.
 * Connectoid nodes represent vertices in the virtual network.
 * <p>
 *   Typically these are only used in conjugate form as
 *  * virtual networks typically run from a centroid vertex to a physical vertex obviating the need for this type of node
 * </p>
 * 
 * @author markr
 *
 */
public interface ConnectoidNode extends ConnectoidDirectedVertex, Node {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidNode shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConnectoidNode deepClone();

}
