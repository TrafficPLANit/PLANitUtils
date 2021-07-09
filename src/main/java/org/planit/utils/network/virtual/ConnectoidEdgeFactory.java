package org.planit.utils.network.virtual;

import java.util.Collection;

import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.graph.GraphEntityFactory;
import org.planit.utils.zoning.Connectoid;

/** Factory interface for connectoid edges
 * 
 * @author markr
 *
 */
public interface ConnectoidEdgeFactory extends GraphEntityFactory<ConnectoidEdge>{

  /**
   * Create new connectoid edges from a specified connectoid to all centroids of the zones this connectoid has registered as access zone.
   * 
   * @param connectoid extract information from connectoid to create virtual connectoid edge(s)
   * @return newly created connectoid edges (reference nodes not yet aware of connection these have to be added afterwards)
   * @throws PlanItException thrown if there is an error
   */
  public Collection<ConnectoidEdge> registerNew(Connectoid connectoid) throws PlanItException;
}
