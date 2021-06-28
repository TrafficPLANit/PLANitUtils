package org.planit.utils.network.service;

import org.planit.utils.graph.Edges;

/** Container around Edges especially designed for ServiceLegs rather than raw Edge instances
 * 
 * @author markr
 *
 * @param <SL> type of service leg
 */
public interface ServiceLegs<SL extends ServiceLeg> extends Edges<SL> {

  /**
   * verify if service leg is present
   * 
   * @param id to check
   * @return true when present false otherwise
   */
  public default boolean hasServiceLeg(long id) {
    return hasEdge(id);
  }

}
