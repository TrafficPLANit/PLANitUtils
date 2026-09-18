package org.goplanit.utils.network.virtual.physical.conjugate;

import org.goplanit.utils.network.layer.physical.ConjugateNode;
import org.goplanit.utils.network.layer.physical.Node;
import org.goplanit.utils.network.virtual.physical.ConnectoidNode;
import org.goplanit.utils.network.virtual.graph.CentroidVertex;
import org.goplanit.utils.network.virtual.graph.conjugate.ConjugateConnectoidDirectedVertex;
import org.goplanit.utils.network.virtual.physical.ConnectoidSegment;

import java.util.Collection;

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
    // the dummy node with no original segment is considered the one to have access to the original centroid vertex
    if(hasOriginalEdgeSegment()){
      return null;
    }
    var conjugateSegment = ((ConjugateConnectoidSegment)getFirstEdgeSegment());
    if(conjugateSegment == null){
      return null; // should not happen, dangling conjugate connectoid node
    }

    return conjugateSegment.getParent().getCentroidVertex();
  }

}
