package org.planit.utils.graph.modifier;

import org.planit.utils.graph.Edge;
import org.planit.utils.graph.Vertex;
import org.planit.utils.id.IdAble;

/**
 * A listener interface to be triggered whenever PLANit entities are removed from the network due to a sub graph being
 * removed. Can be used by any exogenous party to receive a call back in case it relies on these removed entities 
 * 
 * @author markr
 *
 */
public interface RemoveSubGraphListener extends IdAble {
  
  /** callback whenever an edge is removed from a subgraph it is part of
   * 
   * @param edge that is removed
   */
  public void onRemoveSubGraphEdge(Edge edge);
  
  /** callback whenever a vertex is removed from a subgraph it is part of
   * 
   * @param vertex that is removed
   */
  public void onRemoveSubGraphVertex(Vertex vertex);
  
  /**
   * callback after completing all subgraph removals
   */
  public void onCompletion();  
}
