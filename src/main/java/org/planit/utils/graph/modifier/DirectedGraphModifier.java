package org.planit.utils.graph.modifier;

import org.planit.utils.graph.directed.DirectedEdge;
import org.planit.utils.graph.directed.DirectedVertex;
import org.planit.utils.graph.modifier.event.DirectedGraphModifierEventProducer;

/**
 * Modify directed graph elements .
 * 
 * @author markr
 *
 */
public interface DirectedGraphModifier extends GraphModifier<DirectedVertex, DirectedEdge>, DirectedGraphModifierEventProducer{

}
