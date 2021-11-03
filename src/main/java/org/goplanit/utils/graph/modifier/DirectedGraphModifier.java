package org.goplanit.utils.graph.modifier;

import org.goplanit.utils.graph.directed.DirectedEdge;
import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.graph.modifier.event.DirectedGraphModifierEventProducer;

/**
 * Modify directed graph elements .
 * 
 * @author markr
 *
 */
public interface DirectedGraphModifier extends GraphModifier<DirectedVertex, DirectedEdge>, DirectedGraphModifierEventProducer{

}
