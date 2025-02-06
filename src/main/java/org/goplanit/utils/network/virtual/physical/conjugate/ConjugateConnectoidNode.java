package org.goplanit.utils.network.virtual.physical.conjugate;

import org.goplanit.utils.network.layer.physical.ConjugateNode;
import org.goplanit.utils.network.virtual.graph.ConnectoidDirectedEdge;
import org.goplanit.utils.network.virtual.physical.ConnectoidNode;
import org.goplanit.utils.network.virtual.graph.CentroidVertex;
import org.goplanit.utils.network.virtual.graph.conjugate.ConjugateConnectoidDirectedVertex;
import org.goplanit.utils.network.virtual.physical.ConnectoidSegment;

/**
 * Conjugate Node is the conjugate of a normal link. It is expected that its id is synced with the original link it
 * represents.
 * 
 * @author markr
 *
 */
public interface ConjugateConnectoidNode extends ConjugateConnectoidDirectedVertex, ConjugateNode, ConnectoidNode {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidNode shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ConjugateConnectoidNode deepClone();

  /** Original in original directed graph this conjugate represents
   * @return original connectoid segment
   */
  @Override
  public abstract ConnectoidSegment getOriginalEdgeSegment();

  /**
   * Access to original centroid vertex of the zone if available
   *
   * @return centroid vertex when available, null otherwise
   */
  public default CentroidVertex getCentroidVertex(){
    // the dummy node with only an outgoing original connectoid edge segment is where we provide the
    // access to the original centroid vertex
    if(hasOriginalEdgeSegment()){
      return null;
    }
    for(var es : getExitEdgeSegments()){
      // any exit conjugate segment's downstream conjugate vertex, then obtain its original centroid vertex
      return ((ConnectoidSegment)es.getDownstreamVertex().getOriginalEdgeSegment()).getParent().getCentroidVertex();
    }
    return null;
  }
}
