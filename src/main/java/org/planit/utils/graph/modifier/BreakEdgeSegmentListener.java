package org.planit.utils.graph.modifier;

import org.planit.utils.graph.EdgeSegment;
import org.planit.utils.graph.directed.DirectedEdge;
import org.planit.utils.graph.directed.DirectedVertex;

/**
 * A listener for whenever link s are broken on a graph. allows for exogenous classes to perform actions whenever
 * links are broken in the network
 * 
 * @author markr
 *
 */
public interface BreakEdgeSegmentListener extends BreakEdgeListener{
      
  /** invoked after an edge segment has been broken
   * 
   * @param vertex connecting the two edges, point of breaking
   * @param brokenEdge edge that was broken before 
   * @param brokenEdgeSegment that has just been updated to reflect the fact it has a broken parent 
   */
  public abstract void onBreakEdgeSegment(DirectedVertex vertex, DirectedEdge brokenEdge, EdgeSegment brokenEdgeSegment);

}
