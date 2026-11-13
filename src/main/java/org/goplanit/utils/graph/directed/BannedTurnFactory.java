package org.goplanit.utils.graph.directed;

import org.goplanit.utils.graph.GraphEntityFactory;

/** Factory to create banned turns and register them on its container
 * 
 * @author markr
 *
 */
public interface BannedTurnFactory extends GraphEntityFactory<BannedTurn> {
  
  /**
   * Create banned turn
   *
   * @param from  the from segment
   * @param to the to segment
   * @return the created banned turn
   */
  public abstract BannedTurn create(final EdgeSegment from, final EdgeSegment to);

  /**
   * Create banned turn and register it
   *
   * @param from  the from segment
   * @param to the to segment
   * @return the created and registered banned turn
   */
  public abstract BannedTurn registerNew(final EdgeSegment from, final EdgeSegment to);

}
