package org.goplanit.utils.path;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.id.ManagedIdEntityFactory;

import java.util.Deque;

/** Factory interface for directed paths that are not only managed but also containerised
 * 
 * @author markr
 *
 */
public interface ContainerisedDirectedPathFactory extends ManagedDirectedPathFactory<ManagedDirectedPath>{

  /**
   * Create new path
   *
   * @return the created path
   */
  public abstract ManagedDirectedPath registerNew();

  /**
   * Create new path based on the provided edge segments
   *
   * @param edgeSegments      of the path
   * @return the created path
   */
  public abstract ManagedDirectedPath registerNew(Deque<? extends EdgeSegment> edgeSegments);
}
