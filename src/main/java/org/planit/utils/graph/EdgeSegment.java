package org.planit.utils.graph;

import java.io.Serializable;

/**
 * EdgeSegment represents an edge in a particular (single) direction. Each edge
 * has either one or two edge segments where each edge segment may have a more
 * detailed geography than its parent edge
 * 
 * 
 * @author markr
 *
 */
public interface EdgeSegment extends Comparable<EdgeSegment>, Serializable {

    /**
     * Return id of this instance. This id is expected to be generated using the org.planit.utils.misc.IdGenerator
     * @return id the id
     */
    public long getId();
    
    /**
     * set external id of the instance. Note that this id need not be unique (unlike regular id)
     * 
     * @param externalId
     */
	public void setExternalId(Object externalId);
	
    /**
     * Does the instance have an external id
     * 
     * @return true when available, false otherwise
     */
	public boolean hasExternalId();
	
    /**
     * Get external id of the instance. Note that this id need not be unique (unlike regular id)
     * 
     * @return externalId
     */
	public Object getExternalId();
    
    /**
     * Get the segment's upstream vertex
     * 
     * @return upstream vertex
     */
    public Vertex getUpstreamVertex();

    /**
     * Get the segment's downstream vertex
     * 
     * @return downstream vertex
     */
    public Vertex getDownstreamVertex();

    /**
     * Collect the parent edge of the segment
     * 
     * @return parentEdge
     */
    public Edge getParentEdge();
    
}