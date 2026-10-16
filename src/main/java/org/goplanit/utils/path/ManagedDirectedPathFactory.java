package org.goplanit.utils.path;

import java.util.Deque;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.id.ManagedIdEntityFactory;

/** Factory interface for (managed) directed paths
 * 
 * @author markr
 *
 */
public interface ManagedDirectedPathFactory<T extends ManagedDirectedPath> extends ManagedIdEntityFactory<T>, DirectedPathFactory<T>{

  /**
   * Create new path
   *
   * @return the created path
   */
  @Override
  public abstract T createNew();
  
  /**
   * Create new path 
   *
   * @param edgeSegments      of the path
   * @return the created path
   */
  @Override
  public abstract T createNew(Deque<? extends EdgeSegment> edgeSegments);

}
