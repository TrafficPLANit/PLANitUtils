package org.planit.utils.graph;

import java.io.Serializable;

import org.planit.utils.exceptions.PlanItException;

/**
 * Edge interface connecting two vertices in a non-directional fashion.
 * 
 * @author markr
 *
 */
public interface Edge extends Comparable<Edge>, Serializable {

  /**
   * Return id of this instance. This id is expected to be generated using the
   * org.planit.utils.misc.IdGenerator
   * 
   * @return id of this Edge object
   */
  public long getId();
  
  /**
   * Set the external id
   * 
   * @param externalId the external id to set
   */
  void setExternalId(final Object externalId);   

  /**
   * Collect the external id
   * 
   * @return externalID
   */
  Object getExternalId();

  /**
   * Returns whether the external Id has been set
   * 
   * @return true if the external Id has been set, false otherwise
   */
  boolean hasExternalId();  
 
  /**
   * Remove vertex from edge 
   * 
   * @param vertex to remove
   * @return true when successful false otherwise
   */
  public boolean removeVertex(Vertex vertex);    

  // Getters-Setters  

  /**
   * Vertex A of the edge
   * 
   * @return vertex A
   */
  public Vertex getVertexA();

  /**
   * Vertex B of the edge
   * 
   * @return vertex B
   */
  public Vertex getVertexB();

  /**
   * set the name of the edge
   * @param name
   */
  void setName(final String name);  
  
  /**
   * get the name of the edge
   * return name
   */
  public String getName();
  
  /**
   * set length of this edge in km
   * 
   * @param length of this edge in km
   */
  public void setLengthKm(double lengthInKm);  
  
  /**
   * Return length of this edge in km
   * 
   * @return length of this edge in km
   */
  public double getLengthKm();  

  /**
   * Add a property from the original input that is not part of the readily
   * available link members
   * 
   * @param key
   *          key (name) of input property
   * @param value
   *          value of input property
   */
  public void addInputProperty(final String key, final Object value);

  /**
   * Get input property by its key
   * 
   * @param key
   *          key of input property
   * @return value retrieved value of input property
   */
  public Object getInputProperty(String key);

  /**
   * Replace one of the vertices of the link
   * 
   * @param vertextoReplace the vertex to replace
   * @param vertexToReplaceWith the vertex to replace with
   * @return true when replaced, false otherwise
   * @throws PlanItException thrown if error
   */
  public boolean replace(final Vertex vertextoReplace, final Vertex vertexToReplaceWith) throws PlanItException;
  
  /**
   * Clone the edge as is, all shared members are shallow copied, fully owned members are deep copied
   * 
   * @return copy of this edge
   */
  public Edge clone();
  
  /** validate the contents of this edge 
   * @return true when valid, false otherwise
   */
  public boolean validate();

  /** check if vertex is present on the edge
   * @param vertex to check
   * @return true when present false otherwise
   */
  default public boolean hasVertex(Vertex vertex) {
    return getVertexA().equals(vertex) || getVertexB().equals(vertex); 
  }
    
}
