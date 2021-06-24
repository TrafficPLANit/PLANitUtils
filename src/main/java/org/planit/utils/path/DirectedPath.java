package org.planit.utils.path;

import org.planit.utils.graph.EdgeSegment;
import org.planit.utils.id.ExternalIdable;

/**
 * Path interface representing a path through the network on edge segment level
 * 
 * @author markr
 *
 */
public interface DirectedPath extends ExternalIdable, Iterable<EdgeSegment> {

  /**
   * add an edge segment to the path by appending it
   * 
   * @param edgeSegment the edge segment to add
   * @return true as per Collection.add
   */
  //public abstract Boolean addEdgeSegment(final EdgeSegment edgeSegment);

}
