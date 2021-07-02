package org.planit.utils.graph;

import java.util.logging.Logger;

import org.planit.utils.exceptions.PlanItException;

/**
 * Container and factory class for edge segments in a graph, also to be used to create and register edge segments of any
 * (derived) type
 * 
 * @author markr
 */
public interface EdgeSegments extends GraphEntities<EdgeSegment> {
  
  /**
   * Register a edge segment (not registered on nodes and edge)
   *
   * @param parentEdge  the parent edge which specified edge segment will be registered on
   * @param edgeSegment edge segment to be registered
   * @param directionAB direction of travel
   * @throws PlanItException thrown if there is an error
   */
  public abstract void register(final DirectedEdge parentEdge, final EdgeSegment edgeSegment, final boolean directionAB) throws PlanItException;
      
  
  /**
   * Collect the edge segment factory to use for creating instances
   * 
   * @return edgeSegmentFactory to create edge segments for this container
   */
  @Override
  public default EdgeSegmentFactory getFactory(){
    /** override to change return type signature on interface, implementation must still
     * implement this method to provide access to an actual instance */
    Logger.getLogger(EdgeSegmentFactory.class.getCanonicalName()).warning("getFactory not implemented yet for edge segment implementation");
    return null;
  }    
  
  /**
   * clone edge segments
   */
  @Override
  public abstract EdgeSegments clone();   
}
