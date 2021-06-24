package org.planit.utils.path;

import java.util.Deque;

import org.planit.utils.graph.EdgeSegment;

/**
 * Support Paths as container and factory class 
 * 
 * @author markr
 *
 * @param <P> path type
 */
public interface DirectedPaths<P extends DirectedPath> extends Iterable<P>  {

  /**
   * remove a path.
   * 
   * @param path to remove
   */
  public abstract void remove(P path);
  
  /**
   * remove a path.
   * 
   * @param pathId of the edge to remove
   */
  public abstract void remove(final long pathId);  

  /**
   * Create new path
   *
   * @return the created path
   */
  public abstract P registerNew();
  
  /**
   * Create new path based on the provided edge segments
   *
   * @param edgeSegments      of the path
   * @return the created path
   */
  public abstract P registerNew(Deque<EdgeSegment> edgeSegments); 
  
  /**
   * Get path by id
   *
   * @param id the id of the path
   * @return the retrieved path, null if not present
   */
  public abstract P get(long id);
  
  /**
   * Get the number of paths registered
   *
   * @return the number of paths
   */
  public abstract int size();

  /**
   * Check if is empty
   * 
   * @return true when no paths, false otherwise
   */
  public default boolean isEmpty() {
    return size()==0;
  }

  /** Verify if path is present
   * 
   * @param id to verify
   * @return true when present false otherwise
   */
  public default boolean hasPath(long id) {
    return get(id) != null;
  }

}
