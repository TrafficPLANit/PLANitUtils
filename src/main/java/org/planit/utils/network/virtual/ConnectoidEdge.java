package org.planit.utils.network.virtual;

import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.graph.directed.DirectedEdge;

/**
 * the connecting component between centroid and a first physical node in the network.
 * Note that all connectoids are directed edges but not all edges are connectoids
 * 
 * @author markr
 *
 */
public interface ConnectoidEdge extends DirectedEdge{

  /**
   * Register connectoidSegment.
   * 
   * If there already exists a connectoidSegment for that direction it is replaced
   * and returned
   * 
   * @param connectoidSegment connectoid segment to be registered
   * @param directionAB direction of travel
   * @return replaced ConnectoidSegment
   * @throws PlanItException thrown if there is an error
   */
  ConnectoidSegment registerConnectoidSegment(ConnectoidSegment connectoidSegment, boolean directionAB)
      throws PlanItException;

  /**
   * 
   * Return the unique id of this connectoid edge
   * 
   * @return id of this connectoid edge
   */
  long getConnectoidEdgeId();

}
