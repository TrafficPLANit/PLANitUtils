package org.goplanit.utils.graph.directed;

import java.util.Collection;

import org.goplanit.utils.graph.EdgeSegment;
import org.goplanit.utils.graph.Vertex;
import org.goplanit.utils.misc.IterableUtils;

/**
 * Directed vertex representation connected to one or more edge segments that have direction. The vertex itself is of course not directional
 * 
 * @author markr
 *
 */
public interface DirectedVertex extends Vertex {
   
  /**
   * Returns a collection of DirectedEdge objects (unmodifiable)
   * 
   * @return Set of DirectedEdge objects
   */
  @Override
  public abstract Collection<? extends DirectedEdge> getEdges();
  

  
  /**
   * Collect the entry edge segments of this vertex (unmodifiable)
   * 
   * @return edgeSegments
   */
  public Iterable<EdgeSegment> getEntryEdgeSegments();

  /**
   * Collect the exit edge segments of this vertex (unmodifiable)
   * 
   * @return edgeSegments
   */
  public Iterable<EdgeSegment> getExitEdgeSegments();  
  

  
  /** collect the first edge segment corresponding to the provided other vertex
   * 
   * @param otherVertex to use
   * @return first edge segment matching this signature
   */  
  default EdgeSegment getEdgeSegment(DirectedVertex otherVertex) {
    for(EdgeSegment edgeSegment : getExitEdgeSegments()) {
      if(edgeSegment.getDownstreamVertex().equals(otherVertex)) {
        return edgeSegment;
      }
    }
    for(EdgeSegment edgeSegment : getEntryEdgeSegments()) {
      if(edgeSegment.getUpstreamVertex().equals(otherVertex)) {
        return edgeSegment;
      }
    }
    return null;
  }
  
  /**
   * Test whether no exit edge segments have been registered
   * 
   * @return true if no exit edge segments have been registered, false otherwise
   */
  public default boolean hasExitEdgeSegments() {
    return getExitEdgeSegments().iterator().hasNext(); 
  }
  
  /**
   * Test whether no entry edge segments have been registered
   * 
   * @return true if no entry edge segments have been registered, false otherwise
   */
  public default boolean hasEntryEdgeSegments() {
    return getEntryEdgeSegments().iterator().hasNext();
  }
  
  /**
   * Collect the number of entry edge segments of this vertex
   * <p>
   * slow method because it requires iterating over the underlying iterable since it is not a collection we are obtain the count from
   * 
   * @return number of entry edge segments
   */
  public default int sizeOfEntryEdgeSegments() {
    return (int) IterableUtils.sizeOfUsingLoop(getEntryEdgeSegments());
  }

  /**
   * Collect the number of exit edge segments of this vertex
   * <p>
   * slow method because it requires iterating over the underlying iterable since it is not a collection we are obtain the count from
   * 
   * @return number of exit edge segments
   */
  public default int sizeOfExitEdgeSegments() {
    return (int) IterableUtils.sizeOfUsingLoop(getExitEdgeSegments());
  }
  
}
