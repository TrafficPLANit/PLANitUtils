package org.planit.utils.graph.modifier;

import org.planit.utils.graph.Edge;
import org.planit.utils.graph.Vertex;
import org.planit.utils.id.IdGenerator;
import org.planit.utils.id.IdGroupingToken;
import org.planit.utils.id.IdAble;

/**
 * A listener for whenever link s are broken on a graph. allows for exogenous classes to perform actions whenever
 * links are broken in the network
 * 
 * @author markr
 *
 */
public interface BreakEdgeListener extends IdAble {
  
  /**
   * Every instance implementing this interface should generate its id using this method to ensure that we have a unique id across all break edge listeners
   * in case they are stored in a comparable based container
   * 
   * @return id for a break edge listener class instance
   */
  public static long generateId() {
    return IdGenerator.generateId(IdGroupingToken.collectGlobalToken(),BreakEdgeSegmentListener.class);
  }

  /** invoked after an edge has been broken and now consists of to edges with a vertex connecting them
   * @param vertex connecting the two edges, point of breaking
   * @param aToBreak edge 
   * @param breakToB edge
   */
  void onBreakEdge(Vertex vertex, Edge aToBreak, Edge breakToB);

}
