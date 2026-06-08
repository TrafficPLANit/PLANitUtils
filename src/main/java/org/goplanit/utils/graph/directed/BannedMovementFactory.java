package org.goplanit.utils.graph.directed;

import org.goplanit.utils.graph.GraphEntityFactory;
import org.goplanit.utils.id.ManagedIdEntityFactory;

/** Factory to create movements and register them on its container
 * 
 * @author markr
 *
 */
public interface BannedMovementFactory extends
    ManagedIdEntityFactory<BannedMovement>, GraphEntityFactory<BannedMovement> {
  
  /**
   * Create movement
   *
   * @param from  the from segment
   * @param to the to segment
   * @return the created movement
   */
  public abstract BannedMovement create(final EdgeSegment from, final EdgeSegment to);

  /**
   * Create movement and register it
   *
   * @param from  the from segment
   * @param to the to segment
   * @return the created and registered movement
   */
  public abstract BannedMovement registerNew(final EdgeSegment from, final EdgeSegment to);

}
