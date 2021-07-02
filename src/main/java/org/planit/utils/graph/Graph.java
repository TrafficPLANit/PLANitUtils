package org.planit.utils.graph;

/**
 * Interface for a graph with explicit container types rather than the base containers. This is
 * useful when the graph is exposed and the typed containers are needed to gain access to more specific
 * functionality without having to cast.
 * 
 * @author markr
 */
public interface Graph<V extends Vertices, E extends Edges> extends UntypedGraph<V,E> {
    
}
