package org.planit.utils.path;

import org.planit.utils.graph.EdgeSegment;
import org.planit.utils.id.ExternalIdAble;
import org.planit.utils.id.ManagedId;

/**
 * Path interface representing a path through the network on edge segment level
 * 
 * @author markr
 *
 */
public interface DirectedPath extends ExternalIdAble, ManagedId, Iterable<EdgeSegment> {

  /** class to use for id generation */
  public static final Class<DirectedPath> PATH_ID_CLASS = DirectedPath.class;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public default Class<DirectedPath> getIdClass() {
    return PATH_ID_CLASS;
  }  
  
  /**
   * add an edge segment to the path by appending it
   * 
   * @param edgeSegment the edge segment to add
   * @return true as per Collection.add
   */
  //public abstract Boolean addEdgeSegment(final EdgeSegment edgeSegment);  
  
  /** The size of the path is given by the number of edge segments it holds
   * 
   * @return size
   */
  public abstract long size();
  
  /**
   * verify if the size of the path is zero (empty) or not
   * 
   * @return true when empty, false otherwise
   */
  public default boolean isEmpty() {
    return size()<=0;
  } 

}
