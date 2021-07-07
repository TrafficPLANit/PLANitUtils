package org.planit.utils.network.layer.service;

import org.planit.utils.graph.GraphEntityFactory;

/** Factory interface for creating service leg segment instances
 * 
 * @author markr
 *
 */
public interface ServiceLegSegmentFactory extends GraphEntityFactory<ServiceLegSegment> {

  /** Create a new service leg segment (without registering)
   * 
   * @return created service leg segment
   */
  public abstract ServiceLegSegment createNew();
  
  /**
   * Create and register new service leg segment
   *
   * @return new service leg segment created
   */
  public abstract ServiceLegSegment registerNew(); 
  
}
