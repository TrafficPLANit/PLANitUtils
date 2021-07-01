package org.planit.utils.graph;

import org.planit.utils.exceptions.PlanItException;

/** Factory interface for creating edge segment instances
 * 
 * @author markr
 *
 * @param <ES> type of edge segment
 */
public interface EdgeSegmentFactory<ES extends EdgeSegment> extends GraphEntityFactory<ES> {

  /**
   * Create edge segment
   *
   * @param parentEdge  the parent edge of this edge segment
   * @param directionAB direction of travel
   * @return the created edge segment
   * @throws PlanItException thrown if there is an error
   */
  public abstract ES create(final DirectedEdge parentEdge, final boolean directionAB) throws PlanItException;
  
  /**
   * Create directional edge segment and register it
   *
   * @param parentEdge            the parent edge of this edge segment
   * @param directionAb           direction of travel
   * @param registerOnVertexAndEdge option to register the new edge segment on the underlying edge and its vertices
   * @return the created edge segment
   * @throws PlanItException thrown if there is an error
   */
  public abstract ES registerNew(final DirectedEdge parentEdge, final boolean directionAb, final boolean registerOnVertexAndEdge) throws PlanItException;
  
  /** copy the passed in edge segment and register it. 
   * 
   * @param edgeSegmentToCopy as is except for its ids which will be updated to make it uniquely identifiable
   * @param newParent update the parent edge to passed in edge
   * @return copy of edge segment now registered
   */  
  public abstract ES registerUniqueCopyOf(ES edgeSegmentToCopy, DirectedEdge newParent);  
  
}
