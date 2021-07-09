package org.planit.utils.network.virtual;

import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.graph.GraphEntityFactory;

/** Factory interface for connectoid segments
 * 
 * @author markr
 *
 */
public interface ConnectoidSegmentFactory extends GraphEntityFactory<ConnectoidSegment>{

  /**
   * Create and register connectoid segment in AB direction on container
   * 
   * @param parent      the connectoid which will contain this connectoid segment
   * @param directionAB direction of travel
   * @return created connectoid segment
   * @throws PlanItException thrown if there is an error
   */
  public abstract ConnectoidSegment registerNew(ConnectoidEdge parent, boolean directionAB) throws PlanItException;
 
}
