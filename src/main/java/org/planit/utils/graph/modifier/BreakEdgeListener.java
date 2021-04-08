package org.planit.utils.graph.modifier;

import org.planit.utils.graph.Edge;
import org.planit.utils.graph.Vertex;

/**
 * A listener for whenever link s are broken on a graph. allows for exogenous classes to perform actions whenever
 * links are broken in the network
 * 
 * @author markr
 *
 */
public interface BreakEdgeListener<V extends Vertex, E extends Edge>{

  /** invoked after an edge has been broken and now consists of to edges with a vertex connecting them
   * @param vertex connecting the two edges, point of breaking
   * @param aToBreak edge 
   * @param breakToB edge
   */
  void onBreakEdge(V vertex, E aToBreak, E breakToB);

}
