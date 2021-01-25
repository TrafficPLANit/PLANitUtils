package org.planit.utils.zoning;

import org.planit.utils.graph.EdgeSegment;

/**
 * A directed connectoid is referring to an edge segment in a network (layer) which is directed for access
 * hence, the connectoid also being directed
 * 
 * @author markr
 *
 */
public interface DirectedConnectoid extends Connectoid{

  /** the edge segment that provides access
   * 
   * @return access edge segment
   */
  EdgeSegment getAccessEdgeSegment();
}
