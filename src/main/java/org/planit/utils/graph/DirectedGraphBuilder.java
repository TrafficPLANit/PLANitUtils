package org.planit.utils.graph;

import org.planit.utils.exceptions.PlanItException;

/**
 * Build network elements based on chosen network view. Implementations are registered on the network class which uses it to construct network elements
 * 
 * @author markr
 *
 */
public interface DirectedGraphBuilder<V extends DirectedVertex, E extends DirectedEdge, ES extends EdgeSegment> extends GraphBuilder<V, E> {

  /**
   * Create a new physical link segment instance
   * 
   * @param parentEdge  the parent edge of the edge segment
   * @param directionAB direction of travel
   * @return edgeSegment the created edge segment
   * @throws PlanItException thrown if error
   */
  public abstract <W extends E> ES createEdgeSegment(W parentEdge, boolean directionAB) throws PlanItException;    
  
  /**
   * Create a new physical link segment instance, without setting the parent edge yet, this is required to be done manually afterwards
   * before the edge segment is considered valid
   * 
   * @param directionAB direction of travel
   * @return edgeSegment the created edge segment
   * @throws PlanItException thrown if error
   */
  public default ES createEdgeSegment(boolean directionAB) throws PlanItException {
    return createEdgeSegment(null, directionAB);
  }

  /**
   * recreate the ids for all passed in edge segments
   * 
   * @param edgeSegments to recreate ids for
   */
  public abstract <X extends ES> void recreateIds(EdgeSegments<X> edgeSegments);

  /**
   * Create a unique copy of the passed in edge segment. All members are copied as is, except for its ids which are created uniquely created so it remains identifiable, also the
   * parent edge is updated if required
   * 
   * @param edgeSegmentToCopy edge segment to copy
   * @param newParentEdge     use this as the new parent edge
   * @return created copy
   */
  public abstract <X extends ES> ES createUniqueCopyOf(X edgeSegmentToCopy, E newParentEdge);    

}
