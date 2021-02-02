package org.planit.utils.graph;

import org.planit.utils.exceptions.PlanItException;

/**
 * Container and factory class for edge segments in a graph, also to be used to create and register edge segments of any
 * (derived) type
 * 
 * @author markr
 */
public interface EdgeSegments<ES extends EdgeSegment> extends Iterable<ES> {
  
  /**
   * Remove an edges segment
   * 
   * @param edgeSegment to remove
   */
  public void remove(ES edgeSegment); 

  /**
   * Remove an edge segment by id
   * 
   * @param edgeSegmentId to remove by id
   */  
  public void remove(long edgeSegmentId);  

  /**
   * Create edge segment
   *
   * @param parentEdge  the parent edge of this edge segment
   * @param directionAB direction of travel
   * @return the created edge segment
   * @throws PlanItException thrown if there is an error
   */
  public ES create(final DirectedEdge parentEdge, final boolean directionAB) throws PlanItException;

  /**
   * Register a edge segment (not registered on nodes and edge)
   *
   * @param parentEdge  the parent edge which specified edge segment will be registered on
   * @param edgeSegment edge segment to be registered
   * @param directionAB direction of travel
   * @throws PlanItException thrown if there is an error
   */
  public void register(final DirectedEdge parentEdge, final ES edgeSegment, final boolean directionAB) throws PlanItException;
  
  /**
   * Create directional edge segment and register it
   *
   * @param parentEdge            the parent edge of this edge segment
   * @param directionAb           direction of travel
   * @param registerOnVertexAndEdge option to register the new edge segment on the underlying edge and its vertices
   * @return the created edge segment
   * @throws PlanItException thrown if there is an error
   */
  public ES registerNew(final DirectedEdge parentEdge, final boolean directionAb, final boolean registerOnVertexAndEdge) throws PlanItException;  

  /**
   * Get edge segment by id
   *
   * @param id id of the edge segment
   * @return retrieved edge Segment
   */
  public ES get(final long id);
  
  /**
   * Return an edge segment by its Xml id
   * 
   * @param xmlId the XML id of the edge segment
   * @return the specified edge segment instance
   */
  public ES getByXmlId(String xmlId);    

  /**
   * Return number of registered edge segments
   *
   * @return number of registered edge segments
   */
  public long size();

  /** copy the passed in edge segment and register it. 
   * 
   * @param edgeSegmentToCopy as is except for its ids which will be updated to make it uniquely identifiable
   * @param newParent update the parent edge to passed in edge
   * @return copy of edge segment now registered
   */  
  public ES registerUniqueCopyOf(ES edgeSegmentToCopy, DirectedEdge newParent);

  /** check if size is zero
   * @return true when empty, false otherwise
   */
  default public boolean isEmpty() {
    return size()==0;
  }



}
