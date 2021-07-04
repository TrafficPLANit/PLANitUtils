package org.planit.utils.graph.directed;

import org.planit.utils.graph.UntypedDirectedGraph;

/**
 * Interface for a directed graph with explicit container types rather than the base containers. This is
 * useful when the graph is exposed and the typed containers are needed to gain access to more specific
 * functionality without having to cast.
 * 
 * @author markr
 */
public interface DirectedGraph<V extends DirectedVertices, E extends DirectedEdges, ES extends EdgeSegments> extends UntypedDirectedGraph<V,E,ES> {

    
}
