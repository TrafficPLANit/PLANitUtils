package org.planit.utils.graph.modifier;

import org.planit.utils.graph.Edge;
import org.planit.utils.graph.Vertex;

/**
 * A listener interface to be triggered whenever planit entities are removed from the network due to a sub graph being
 * removed. Can be used by any exogenous party to receive a call baack in case it relies on these removed entities 
 * 
 * @author markr
 *
 */
public interface RemoveSubGraphListener<V extends Vertex, E extends Edge> {

  /** callback whenever an edge is removed from a subgraph it is part of
   * 
   * @param edge that is removed
   */
  public void onRemoveSubGraphEdge(E edge);
  
  /** callback whenever a vertex is removed from a subgraph it is part of
   * 
   * @param vertex that is removed
   */
  public void onRemoveSubGraphVertex(V vertex);
  
  /**
   * callback after completing all subgraph removals
   */
  public void onCompletion();  
}
