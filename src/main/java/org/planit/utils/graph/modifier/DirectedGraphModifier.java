package org.planit.utils.graph.modifier;

import org.planit.utils.graph.directed.DirectedEdge;
import org.planit.utils.graph.directed.DirectedVertex;

/**
 * Modify directed graph elements .
 * 
 * @author markr
 *
 */
public interface DirectedGraphModifier extends GraphModifier<DirectedVertex, DirectedEdge>{

  /** register listener for removing directed sub graphs
   * @param subGraphRemovalListener to register
   */
  public abstract void registerRemoveSubGraphListener(RemoveDirectedSubGraphListener subGraphRemovalListener);
   


}
