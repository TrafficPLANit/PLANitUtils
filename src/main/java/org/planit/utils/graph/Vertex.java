package org.planit.utils.graph;

import java.io.Serializable;
import java.util.Set;

import org.opengis.geometry.DirectPosition;

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
  public interface EdgeSegments extends Iterable<EdgeSegment> {

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
     * Number of entries in edge segments
     * @return the number of edges connected to this vertex
     */
    public int getNumberOfEdges();
  }

  /**
   * Return id of this instance. 
   * 
   * @return the id
   */
  public long getId();
  
  /**
   * Collect the external id of the node
   * 
   * @return external id
   */
  Object getExternalId();

  /**
   * Set the external id of the node
   * 
   * @param externalId the external id to set
   */
  void setExternalId(Object externalId);

  /**
   * Returns whether the node has an external Id set
   * 
   * @return true if the node has an external Id, false otherwise
   */
  boolean hasExternalId();

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

  /**
   * Set the center point geometry for a vertex
   * 
   * @param centrePointGeometry the center point for a vertex
   */
  public void setCentrePointGeometry(final DirectPosition centrePointGeometry);

  /**
   * Collect the geometry of the point location of this vertex
   * 
   * @return direct position reflecting point location
   */
  public DirectPosition getCentrePointGeometry();

}
