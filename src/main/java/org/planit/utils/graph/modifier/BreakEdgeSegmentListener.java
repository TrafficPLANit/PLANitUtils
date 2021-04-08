package org.planit.utils.graph.modifier;

import org.planit.utils.graph.Edge;
import org.planit.utils.graph.EdgeSegment;
import org.planit.utils.graph.Vertex;

/**
 * A listener for whenever link s are broken on a graph. allows for exogenous classes to perform actions whenever
 * links are broken in the network
 * 
 * @author markr
 *
 */
public interface BreakEdgeSegmentListener<V extends Vertex, E extends Edge, ES extends EdgeSegment> extends BreakEdgeListener<V,E>{

  /** invoked after an edge segment has been broken
   * 
   * @param vertex connecting the two edges, point of breaking
   * @param brokenEdge edge that was broken before 
   * @param brokenEdgeSegment that has just been updated to reflect the fact it has a broken parent 
   */
  void onBreakEdgeSegment(V vertex, E brokenEdge, ES brokenEdgeSegment);

}
