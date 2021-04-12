package org.planit.utils.graph;

import java.util.ArrayList;
import java.util.Collection;

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
  public abstract EdgeSegment registerEdgeSegment(final EdgeSegment edgeSegment, final boolean directionAB) throws PlanItException;
  
  /**
   * Edge segment in the direction from A to B
   * 
   * @return edge segment AB
   */
  public abstract EdgeSegment getEdgeSegmentAb();
  
  /**
   * Edge segment in the direction from B to A
   * 
   * @return edge segment BA
   */
  public abstract EdgeSegment getEdgeSegmentBa();
  
  /** replace passed in edge segment (if present) with the passed in one
   * 
   * @param edgeSegmentToReplace the one to replace
   * @param edgeSegmentToReplaceWith the one to replace it with
   */
  public abstract void replace(EdgeSegment edgeSegmentToReplace, EdgeSegment edgeSegmentToReplaceWith);  
  
  /**
   * Edge segment in the direction indicated
   * 
   * @param directionAb direction of segment
   * @return edge segment if present
   */
  public default EdgeSegment getEdgeSegment(boolean directionAb) {
    return directionAb ? getEdgeSegmentAb() : getEdgeSegmentBa();
  }
 
  /** Verify if edge segment BA exists
   * 
   * @return true if present, false otherwise
   */
  public default boolean hasEdgeSegmentBa() {
    return getEdgeSegmentBa() != null;
  }
  
  /** Verify if edge segment BA exists
   * 
   * @return true if present, false otherwise
   */
  public default boolean hasEdgeSegmentAb() {
    return getEdgeSegmentAb() != null;
  }
  
  /** collect all edge segments available on the edge 
   * 
   * @return available edge segments
   */
  public default Collection<? extends EdgeSegment> getEdgeSegments(){
    ArrayList<EdgeSegment> edgeSegments = null;
    if(hasEdgeSegmentAb() || hasEdgeSegmentBa()) {
      edgeSegments = new ArrayList<EdgeSegment>(2);
      if(hasEdgeSegmentAb()) {
        edgeSegments.add(getEdgeSegmentAb());
      }
      if(hasEdgeSegmentBa()) {
        edgeSegments.add(getEdgeSegmentBa());
      }
    }
    return edgeSegments;
  }

}
