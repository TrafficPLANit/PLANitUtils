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
   * Return id of this instance. This id is expected to be generated using the
   * org.planit.utils.misc.IdGenerator
   * 
   * @return id the id
   */
  public long getId();

  /**
   * set external id of the instance. Note that this id need not be unique (unlike regular id)
   * 
   * @param externalId the external id to set
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
   * Remove the vertex from the edge segment if it is either the up or downstrema vertex
   * 
   * @param vertex to remove
   * @return true when successful, false otherwise
   */
  public boolean removeVertex(Vertex vertex);  

  /**
   * Get the segment's upstream vertex
   * 
   * @return upstream vertex
   */
  public DirectedVertex getUpstreamVertex();

  /**
   * Get the segment's downstream vertex
   * 
   * @return downstream vertex
   */
  public DirectedVertex getDownstreamVertex();

  /**
   * Collect the parent edge of the segment
   * 
   * @return parentEdge
   */
  public Edge getParentEdge();
  
  /**
   * check if edge segment runs from vertex a to b or b to a
   * @return true when running from a to b, otherwise false
   */
  boolean isDirectionAb();



}
