package org.planit.utils.graph;

import java.io.Serializable;
import java.util.Collection;
import org.opengis.geometry.DirectPosition;

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
   * Add a property from the original input that is not part of the readily available members
   *
   * @param key   key (name) of the input property
   * @param value value of the input property
   */
  public void addInputProperty(final String key, final Object value);  

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
   * Remove edge
   * 
   * @param edgeId Edge to be removed
   * @return true when removed, false when not present (and not removed)
   */
  public boolean removeEdge(long edgeId);  

  /**
   * Returns a collection of Edge objects
   * 
   * @return Set of Edge objects
   */
  public Collection<? extends Edge> getEdges();
  
  /**
   * Number of entries in edge segments
   * @return the number of edges connected to this vertex
   */
  public int getNumberOfEdges();  
  
  /**
   * colect the edge based on the other vertex
   * 
   * @param otherVertex that defines the edge
   * @return edge the edge if available
   */
  Edge getEdge(Vertex otherVertex);   
  
  /**
   * Clone the vertex
   * @return
   */
  public Vertex clone();

  
  /** replace one edge with the other
   * 
   * @param edgeToReplace one to replace
   * @param edgeToReplaceWith one to replace it with
   */
  default public boolean replace(Edge edgeToReplace, Edge edgeToReplaceWith) {
    if(removeEdge(edgeToReplace)) {
      return addEdge(edgeToReplaceWith);      
    }
    return false;
  }

}
