package org.planit.utils.graph;

import org.planit.utils.exceptions.PlanItException;

/**
 * Container and factory class for edge segments in a graph, also to be used to create and register edge segments of any
 * (derived) type
 * 
 * @author markr
 */
public interface EdgeSegments<ES extends EdgeSegment> extends Iterable<ES> {

  /**
   * Create edge segment
   *
   * @param parentEdge  the parent edge of this edge segment
   * @param directionAB direction of travel
   * @return the created edge segment
   * @throws PlanItException thrown if there is an error
   */
  public ES createEdgeSegment(final Edge parentEdge, final boolean directionAB) throws PlanItException;

  /**
   * Register a edge segment
   *
   * @param parentEdge  the parent edge which specified edge segment will be registered on
   * @param edgeSegment edge segment to be registered
   * @param directionAB direction of travel
   * @throws PlanItException thrown if there is an error
   */
  public void registerEdgeSegment(final Edge parentEdge, final ES edgeSegment, final boolean directionAB) throws PlanItException;

  /**
   * Get edge segment by id
   *
   * @param id id of the edge segment
   * @return retrieved edge Segment
   */
  public ES getEdgeSegment(final long id);

  /**
   * Return number of registered edge segments
   *
   * @return number of registered edge segments
   */
  public int getNumberOfEdgeSegments();
}
