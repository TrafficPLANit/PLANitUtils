package org.planit.utils.graph;

import java.io.Serializable;
import java.util.Set;

import org.opengis.geometry.DirectPosition;
import org.planit.utils.exceptions.PlanItException;

/**
 * Vertex representation connected to one or more edges and/or edge segments
 * 
 * @author markr
 *
 */
public interface Vertex extends Comparable<Vertex>, Serializable {

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


  /**
   * Test whether no exit edge segments have been registered
   * 
   * @return true if no exit edge segments have been registered, false otherwise
   */
  public boolean hasExitEdgeSegments();
  
  /**
   * Test whether no entry edge segments have been registered
   * 
   * @return true if no entry edge segments have been registered, false otherwise
   */
  public boolean hasEntryEdgeSegments();  
 
  /**
   * Number of entries in edge segments
   * @return the number of edges connected to this vertex
   */
  public int getNumberOfEdges();  
  
  /**
   * Add edgeSegment, do not invoke when parsing networks, this connection is
   * auto-populated before the assignment starts based on the edge segment
   * vertices that have been registered.
   * 
   * @param edgeSegment EdgeSegment object to be added
   * @return true when added, false when already present (and not added)
   * @throws PlanItException thrown when error
   */
  public boolean addEdgeSegment(EdgeSegment edgeSegment) throws PlanItException;

  /**
   * Remove edgeSegment
   * 
   * @param edgeSegment EdgeSegment object to be removed
   * @return true when removed, false when not present (and not removed)
   * @throws PlanItException thrown when error
   */
  public boolean removeEdgeSegment(EdgeSegment edgeSegment) throws PlanItException;
  
  /**
   * Collect the entry edge segments of this vertex
   * 
   * @return edgeSegments
   */
  public Set<EdgeSegment> getEntryEdgeSegments();

  /**
   * Collect the exit edge segments of this vertex
   * 
   * @return edgeSegments
   */
  public Set<EdgeSegment> getExitEdgeSegments();  

}
