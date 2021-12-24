package org.goplanit.utils.network.layer.service;

import java.util.Collection;

import org.goplanit.utils.graph.Edge;
import org.goplanit.utils.graph.EdgeSegment;
import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.network.layer.physical.Node;

/**
 * Service node is a vertex but not all vertices are service nodes.
 * A service node represents a location in the service network where legs intersect, usually
 * representing for example stop locations of public transport.
 * 
 * @author markr
 *
 */
public interface ServiceNode extends DirectedVertex {
     
  /**
   * Identical to {@link #getEdges()}
   * 
   * @return legs 
   */
  public default  Collection<? extends Edge> getLegs() {
    return getEdges();
  }
  
  /**
   * Identical to {@link #getEntryEdgeSegments()}
   * 
   * @return entryLegSegments
   */
 
  public default  Iterable<EdgeSegment> getEntryLegSegments() {
    return getEntryEdgeSegments();
  }
  
  /**
   * Identical to {@link #getExitEdgeSegments()}
   * 
   * @return exitLegSegments
   */
 
  public default Iterable<EdgeSegment> getExitLegSegments() {
    return getExitEdgeSegments();
  }


  /** Identical to {@code #getEdgeSegment(DirectedVertex)}
   * 
   * @param endNode to use
   * @return first leg segment matching this signature
   */
  public default ServiceLegSegment getLegSegment(ServiceNode endNode) {
    return (ServiceLegSegment) getEdgeSegment(endNode);
  }


  /**
   * Collect the first available entry leg segment using the iterator internally. It is assumed
   * at least one entry is available
   * 
   * @return first entry available
   */
  public default ServiceLegSegment getFirstEntryLegSegment(){
    return (ServiceLegSegment) getEntryLegSegments().iterator().next();
  }
  
  /**
   * Collect the first available exit link segment using the iterator internally. It is assumed
   * at least one entry is available
   * 
   * @return first exit available
   */
  public default ServiceLegSegment getFirstExitLegSegment(){
    return (ServiceLegSegment) getExitLegSegments().iterator().next();
  }  
  
  /**
   * Provide access to the underlying network node
   * 
   * @return networkLayerNode
   */
  public abstract Node getParentNode();
  
}
