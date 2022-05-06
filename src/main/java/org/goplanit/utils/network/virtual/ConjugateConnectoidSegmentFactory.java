package org.goplanit.utils.network.virtual;

import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.graph.GraphEntityFactory;

/** Factory interface for onjugate connectoid segments
 * 
 * @author markr
 *
 */
public interface ConjugateConnectoidSegmentFactory extends GraphEntityFactory<ConjugateConnectoidSegment>{

  /**
   * Create but not  register conjugate connectoid segment in AB direction on container
   *
   * @param parent      the conjugate connectoid edge that contains this conjugate connectoid segment
   * @param directionAb direction of travel
   * @return created conjugate connectoid segment
   */
  public abstract ConjugateConnectoidSegment create(ConjugateConnectoidEdge parent, boolean directionAb);

  /**
   * Create and register connectoid segment in AB direction on container
   * 
   * @param parent      the conjugate connectoid edge that contains this conjugate connectoid segment
   * @param directionAb direction of travel
   * @param registerOnNodeAndLink when true register segment on node and link, otherwise not
   * @return created connectoid segment
   */
  public abstract ConjugateConnectoidSegment registerNew(ConjugateConnectoidEdge parent, boolean directionAb, boolean registerOnNodeAndLink);


}
