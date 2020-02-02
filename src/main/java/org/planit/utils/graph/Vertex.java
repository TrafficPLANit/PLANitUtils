package org.planit.utils.graph;

import java.io.Serializable;
import java.util.Set;

/**
 * Vertex representation connected to one or more edges
 * 
 * @author markr
 *
 */
public interface Vertex extends Comparable<Vertex>, Serializable {

    /**
     * edges of this vertex
     * 
     * @author markr
     */
	public interface Edges {

        /**
         * Add edge, do not invoke when parsing networks, this connection is
         * auto-populated before the assignment starts based on the edge and its two
         * vertices that have been registered.
         * 
         * @param edge Edge to be added
         * @return true when added, false when already present (and not added)
         */
        public boolean addEdge(Edge edge);

        /**
         * Remove edge
         * 
         * @param edge Edge to be removed
         * @return true when removed, false when not present (and not removed)
         */
        public boolean removeEdge(Edge edge);
        
        /**
         * Returns a Set of Edge objects
         * 
         * @return Set of Edge objects
         */
        public Set<Edge> getEdges();
    }

    /**
     * EdgeSegment container
     * 
     * @author markr
     */
	public interface EdgeSegments {

        /**
         * Add edgeSegment, do not invoke when parsing networks, this connection is
         * auto-populated before the assignment starts based on the edge segment
         * vertices that have been registered.
         * 
         * @param edgeSegment EdgeSegment object to be added
         * @return true when added, false when already present (and not added)
         */
        public boolean addEdgeSegment(EdgeSegment edgeSegment);

        /**
         * Remove edgeSegment
         * 
         * @param edgeSegment EdgeSegment object to be removed
         * @return true when removed, false when not present (and not removed)
         */
        public boolean removeEdgeSegment(EdgeSegment edgeSegment);

        /**
         * Test whether no edge segments have been registered
         * 
         * @return true if no edge segments have been registered, false otherwise
         */
        public boolean isEmpty();
        
        /**
         * Return Set of EdgeSegment objects
         * 
         * @return Set of EdgeSegment objects
         */
        public Set<EdgeSegment> getEdgeSegments();
    }

    /**
     * Return id of this instance. This id is expected to be generated using the org.planit.utils.misc.IdGenerator
     * @return the id
     */
    public long getId();
    
    /**
     * Collect the entry edge segments of this vertex
     * 
     * @return edgeSegments
     */
    public EdgeSegments getEntryEdgeSegments();
    
    /**
     * Collect the exit edge segments of this vertex
     * 
     * @return edgeSegments
     */
    public EdgeSegments getExitEdgeSegments();
    
    /**
     * Collect the edges of this vertex
     * 
     * @return edges
     */
    public Edges getEdges();

}
