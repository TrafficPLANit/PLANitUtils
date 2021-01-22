package org.planit.utils.network.virtual;

import org.planit.utils.id.ExternalIdable;
import org.planit.utils.network.physical.Node;
import org.planit.utils.zoning.Zone;

/**
 * the connecting component between centroid and a first physical node in the network.
 * Note that all connectoids are directed edges but not all edges are connectoids
 * 
 * @author markr
 *
 */
public interface Connectoid extends ExternalIdable{

  /**
   * Default connectoid length in km
   */
  double DEFAULT_LENGTH_KM = 0.0;
  
  /** Collect the zone this connectoid provides access to
   * 
   * @return zone 
   */
  Zone getParentZone();
  
  /** Collect the physical node this connectoid proposes to create an acess point for its zone
   * 
   * @return access node
   */
  Node getAccessNode();  
  
  /** length can be used to virtually assign a length to the connectoid
   * @return length
   */
  double getLength();

}
