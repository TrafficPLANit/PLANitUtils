package org.goplanit.utils.graph.modifier;

import org.goplanit.utils.graph.directed.DirectedEdge;
import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.graph.modifier.event.DirectedGraphModifierEventProducer;

/**
 * Modify directed graph elements .
 * 
 * @author markr
 *
 */
public interface DirectedGraphModifier extends GraphModifier<DirectedVertex, DirectedEdge>, DirectedGraphModifierEventProducer{

  /**
   * Remove an edge segment by removing it from the graph and the edge it is connected to. Any registered events for edge segment removal
   * will be triggered.
   *
   * @param edgeSegment to remove
   */
  public abstract void removeEdgeSegment(EdgeSegment edgeSegment);

}
