package org.goplanit.utils.graph.directed;

import java.util.logging.Logger;

import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.graph.GraphEntities;

/**
 * Container and factory class for conjugate edge segments in a conjugate graph.
 * 
 * @author markr
 */
public interface ConjugateEdgeSegments extends GraphEntities<ConjugateEdgeSegment> {   
  
  /**
   * Register a conjugate edge segment (not registered on nodes and edge)
   *
   * @param parentEdge  the conjugate parent edge which specified edge segment will be registered on
   * @param edgeSegment conjugate edge segment to be registered
   * @param directionAB direction of travel
   * @throws PlanItException thrown if there is an error
   */
  public abstract void register(final ConjugateDirectedEdge parentEdge, final ConjugateEdgeSegment edgeSegment, final boolean directionAB) throws PlanItException;
      
  
  /**
   * Collect the conjugate edge segment factory to use for creating instances
   * 
   * @return conjugate edgeSegmentFactory to create edge segments for this container
   */
  @Override
  public default ConjugateEdgeSegmentFactory getFactory(){
    /** override to change return type signature on interface, implementation must still
     * implement this method to provide access to an actual instance */
    Logger.getLogger(ConjugateEdgeSegmentFactory.class.getCanonicalName()).warning("getFactory not implemented yet for conjugate edge segment implementation");
    return null;
  }    
  
  /**
   * shallow clone conjugate edge segments
   */
  @Override
  public abstract ConjugateEdgeSegments shallowClone();

  /**
   * Deep clone conjugate edge segments
   */
  @Override
  public abstract ConjugateEdgeSegments deepClone();
}
