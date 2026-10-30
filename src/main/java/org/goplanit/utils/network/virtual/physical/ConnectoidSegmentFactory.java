package org.goplanit.utils.network.virtual.physical;

import org.goplanit.utils.graph.GraphEntityFactory;

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
   * @param directionAb direction of travel
   * @return created connectoid segment
   */
  public abstract ConnectoidSegment registerNew(ConnectoidLink parent, boolean directionAb);
 
}
