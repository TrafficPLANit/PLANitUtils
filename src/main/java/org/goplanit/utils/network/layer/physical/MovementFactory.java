package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.graph.GraphEntityFactory;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.id.ManagedIdEntityFactory;

/** Factory to create movements and register them on its container
 * 
 * @author markr
 *
 */
public interface MovementFactory extends ManagedIdEntityFactory<Movement> {
  
  /**
   * Create movement
   *
   * @param from  the from segment
   * @param to the to segment
   * @param banned flag whether banned or not
   * @return the created movement
   */
  public abstract Movement create(final EdgeSegment from, final EdgeSegment to, boolean banned);

  /**
   * Create movement and register it
   *
   * @param from  the from segment
   * @param to the to segment
   * @param banned flag whether banned or not
   * @return the created and registered movement
   */
  public abstract Movement registerNew(final EdgeSegment from, final EdgeSegment to, boolean banned);

}
