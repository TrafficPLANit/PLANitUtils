package org.planit.utils.graph;

import org.planit.utils.exceptions.PlanItException;

/**
 * Directed Edge interface connecting two vertices in a directional fashion. Each edge has one or
 * two underlying edge segments in a particular direction which may carry
 * additional information for each particular direction of the edge.
 * 
 * @author markr
 *
 */
public interface DirectedEdge extends Edge {

  /**
   * Register EdgeSegment.
   *
   * If there already exists an edgeSegment for that direction it is replaced and returned
   *
   * @param edgeSegment the edgeSegment to be registered
   * @param directionAB direction of travel
   * @return replaced egeSegment (if any)
   * @throws PlanItException thrown if there is an error
   */
  public EdgeSegment registerEdgeSegment(final EdgeSegment edgeSegment, final boolean directionAB) throws PlanItException;
  
  /**
   * Edge segment in the direction from A to B
   * 
   * @return edge segment AB
   */
  public EdgeSegment getEdgeSegmentAb();
  
  /**
   * Edge segment in the direction from B to A
   * 
   * @return edge segment BA
   */
  public EdgeSegment getEdgeSegmentBa();
  
  /**
   * Edge segment in the direction indicated
   * 
   * @return edge segment if present
   */
  default public EdgeSegment getEdgeSegment(boolean directionAb) {
    return directionAb ? getEdgeSegmentAb() : getEdgeSegmentBa();
  }
 
  /** Verify if edge segment BA exists
   * 
   * @return true if present, false otherwise
   */
  default public boolean hasEdgeSegmentBa() {
    return getEdgeSegmentBa() != null;
  }
  
  /** Verify if edge segment BA exists
   * 
   * @return true if present, false otherwise
   */
  default public boolean hasEdgeSegmentAb() {
    return getEdgeSegmentAb() != null;
  }

  /** replace passed in edge segment (if present) with the passed in one
   * 
   * @param edgeSegmentToReplace the one to replace
   * @param edgeSegmentToReplaceWith the one to replace it with
   */
  public void replace(EdgeSegment edgeSegmentToReplace, EdgeSegment edgeSegmentToReplaceWith);


}
