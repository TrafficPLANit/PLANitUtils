package org.planit.utils.graph;

import java.io.Serializable;

import org.planit.utils.exceptions.PlanItException;

/**
 * Edge interface connecting two vertices in a non-directional fashion. Each edge has one or
 * two underlying edge segments in a particular direction which may carry
 * additional information for each particular direction of the edge.
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
   * Return length of this edge in km
   * 
   * @return length of this edge in km
   */
  public double getLength();
  
  /**
   * Register EdgeSegment.
   *
   * If there already exists an edgeSegment for that direction it is replaced and returned
   *
   * @param edgeSegment the edgeSegment to be registered
   * @param directionAB direction of travel
   * @return replaced egeSegment (if any)
   * @throws PlanItException thrown if there is an error
   */
  public EdgeSegment registerEdgeSegment(final EdgeSegment edgeSegment, final boolean directionAB) throws PlanItException;  

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
   * collect the name of the edge
   * 
   * @return the name
   */
  public String getName();

  /**
   * Add a property from the original input that is not part of the readily
   * available link members
   * 
   * @param key
   *          key (name) of input property
   * @param value
   *          value of input property
   */
  public void addInputProperty(String key, Object value);

  /**
   * Get input property by its key
   * 
   * @param key
   *          key of input property
   * @return value retrieved value of input property
   */
  public Object getInputProperty(String key);

  /**
   * Edge segment in the direction from A to B
   * 
   * @return edge segment AB
   */
  public EdgeSegment getEdgeSegmentAB();

  /**
   * Edge segment in the direction from B to A
   * 
   * @return edge segment BA
   */
  public EdgeSegment getEdgeSegmentBA();

}
