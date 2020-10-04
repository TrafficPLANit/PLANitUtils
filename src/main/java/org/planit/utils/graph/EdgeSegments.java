package org.planit.utils.graph;

import org.planit.utils.exceptions.PlanItException;

/**
 * Container and factory class for edge segments in a graph, also to be used to create and register edge segments of any
 * (derived) type
 * 
 * @author markr
 */
public interface EdgeSegments<E extends Edge, ES extends EdgeSegment> extends Iterable<ES> {
  
  /**
   * Remove an edges segment
   * 
   * @param edgeSegment to remove
   */
  public void remove(ES edgeSegment);  

  /**
   * Create edge segment
   *
   * @param parentEdge  the parent edge of this edge segment
   * @param directionAB direction of travel
   * @return the created edge segment
   * @throws PlanItException thrown if there is an error
   */
  public ES create(final E parentEdge, final boolean directionAB) throws PlanItException;

  /**
   * Register a edge segment
   *
   * @param parentEdge  the parent edge which specified edge segment will be registered on
   * @param edgeSegment edge segment to be registered
   * @param directionAB direction of travel
   * @throws PlanItException thrown if there is an error
   */
  public void createAndRegister(final E parentEdge, final ES edgeSegment, final boolean directionAB) throws PlanItException;

  /**
   * Get edge segment by id
   *
   * @param id id of the edge segment
   * @return retrieved edge Segment
   */
  public ES get(final long id);

  /**
   * Return number of registered edge segments
   *
   * @return number of registered edge segments
   */
  public int size();

}
