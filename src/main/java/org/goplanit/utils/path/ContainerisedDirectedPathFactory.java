package org.goplanit.utils.path;

import java.util.Deque;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.id.ManagedIdEntityFactory;

/** Factory interface for directed paths
 * 
 * @author markr
 *
 */
public interface ContainerisedDirectedPathFactory extends ManagedIdEntityFactory<DirectedPath>, DirectedPathFactory{

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
