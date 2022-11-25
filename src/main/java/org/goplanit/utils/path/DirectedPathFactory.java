package org.goplanit.utils.path;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.id.ManagedIdEntityFactory;

import java.util.Deque;

/** Factory interface for simple directed paths
 * 
 * @author markr
 *
 */
public interface DirectedPathFactory<T extends SimpleDirectedPath> {

  /**
   * Create new path
   *
   * @return the created path
   */
  public abstract T createNew();
  
  /**
   * Create new path 
   *
   * @param edgeSegments      of the path
   * @return the created path
   */
  public abstract T createNew(Deque<? extends EdgeSegment> edgeSegments);

}
