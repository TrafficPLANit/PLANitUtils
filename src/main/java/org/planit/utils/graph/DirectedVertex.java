package org.planit.utils.graph;

import java.util.Set;

import org.opengis.geometry.DirectPosition;
import org.planit.utils.exceptions.PlanItException;

/**
 * Directed vertex representation connected to one or more edge segments that have direction
 * 
 * @author markr
 *
 */
public interface DirectedVertex extends Vertex {

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
  
  /**
   * Collect the number of entry edge segments of this vertex
   * 
   * @return number of entry edge segments
   */
  public int getNumberOfEntryEdgeSegments();

  /**
   * Collect the number of exit edge segments of this vertex
   * 
   * @return number of exit edge segments
   */
  public int getNumberOfExitEdgeSegments(); 

}
