package org.goplanit.utils.path;

import java.util.Deque;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.id.ManagedIdEntityFactory;

/** Factory interface for directed paths
 * 
 * @author markr
 *
 */
public interface DirectedPathFactory extends ManagedIdEntityFactory<DirectedPath>{

  /**
   * Create new path
   *
   * @return the created path
   */
  public abstract DirectedPath createNew();
  
  /**
   * Create new path 
   *
   * @param edgeSegments      of the path
   * @return the created path
   */
  public abstract DirectedPath createNew(Deque<? extends EdgeSegment> edgeSegments); 
}
