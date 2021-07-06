package org.planit.utils.path;

import java.util.Deque;

import org.planit.utils.graph.EdgeSegment;
import org.planit.utils.id.ContainerisedManagedIdEntityFactory;

/** Factory interface for directed paths
 * 
 * @author markr
 *
 */
public interface DirectedPathFactory extends ContainerisedManagedIdEntityFactory<DirectedPath>{

  /**
   * Create new path
   *
   * @return the created path
   */
  public abstract DirectedPath registerNew();
  
  /**
   * Create new path based on the provided edge segments
   *
   * @param edgeSegments      of the path
   * @return the created path
   */
  public abstract DirectedPath registerNew(Deque<? extends EdgeSegment> edgeSegments); 
}
