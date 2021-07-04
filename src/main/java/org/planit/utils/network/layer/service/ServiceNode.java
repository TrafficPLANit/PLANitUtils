package org.planit.utils.network.layer.service;

import java.util.Collection;
import java.util.Set;

import org.planit.utils.graph.EdgeSegment;
import org.planit.utils.graph.directed.DirectedVertex;

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
   * It is expected that service nodes are used in conjunction with service legs. If so, this method will cast the edges of the service node to 
   * a service leg collection for readability.
   * 
   * @param <L> service leg type
   * @return edges cast as collection of service legs
   */
  @SuppressWarnings("unchecked")
  public default <L extends ServiceLeg> Collection<L> getLegs() {
    return (Collection<L>) getEdges();
  }
  
  /**
   * It is expected that service nodes are used in conjunction with leg segments. If so, this method will cast the leg segments of the service node 
   * to leg segments for readability.
   * 
   * @param <LS> leg segment type
   * @return edgeSegments as collection of leg Segments
   */
  @SuppressWarnings("unchecked")  
  public default <SLS extends ServiceLegSegment> Set<SLS> getEntryLegSegments() {
    return (Set<SLS>) getEntryEdgeSegments();
  }
  
  /**
   * It is expected that service nodes are used in conjunction with leg segments. If so, this method will cast the leg segments of the service node 
   * to leg segments for readability.
   * 
   * @param <LS> leg segment type
   * @return edgeSegments as collection of leg Segments
   */
  @SuppressWarnings("unchecked")  
  public default <SLS extends ServiceLegSegment> Set<SLS> getExitLegSegments() {
    return (Set<SLS>) getExitEdgeSegments();
  }


  /** Collect the first leg segment corresponding to the provided end node
   * 
   * @param <LS> leg segment type
   * @param endNode to use
   * @return first leg segment matching this signature
   */
  @SuppressWarnings("unchecked")
  public default <SLS extends ServiceLegSegment> SLS getLegSegment(ServiceNode endNode) {
    return (SLS) getEdgeSegment(endNode);
  }


  /**
   * Collect the first available entry leg segment using the iterator internally. It is assumed
   * at least one entry is available
   * 
   * @param <LS> leg segment type used
   * @return first entry available
   */
  @SuppressWarnings("unchecked")
  public default <SLS extends ServiceLegSegment> SLS getFirstEntryLegSegment(){
    return (SLS) getEntryLegSegments().iterator().next();
  }
  
  /**
   * Collect the first available exit link segment using the iterator internally. It is assumed
   * at least one entry is available
   * 
   * @param <LS> link segment type used
   * @return first exit available
   */
  @SuppressWarnings("unchecked")
  public default <SLS extends EdgeSegment> SLS getFirstExitLegSegment(){
    return (SLS) getExitLegSegments().iterator().next();
  }  
  
}
