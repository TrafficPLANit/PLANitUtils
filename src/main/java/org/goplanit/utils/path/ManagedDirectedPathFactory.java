package org.goplanit.utils.path;

import java.util.Deque;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.id.ManagedIdEntityFactory;

/** Factory interface for (managed) directed paths
 * 
 * @author markr
 *
 */
public interface ManagedDirectedPathFactory extends ManagedIdEntityFactory<ManagedDirectedPath>, DirectedPathFactory<ManagedDirectedPath>{

  /**
   * Create new path
   *
   * @return the created path
   */
  @Override
  public abstract ManagedDirectedPath createNew();
  
  /**
   * Create new path 
   *
   * @param edgeSegments      of the path
   * @return the created path
   */
  @Override
  public abstract ManagedDirectedPath createNew(Deque<? extends EdgeSegment> edgeSegments);

}
