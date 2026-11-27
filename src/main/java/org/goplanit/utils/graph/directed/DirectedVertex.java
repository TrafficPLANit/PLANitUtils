package org.goplanit.utils.graph.directed;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.goplanit.utils.graph.Vertex;
import org.goplanit.utils.misc.IterableUtils;

/**
 * Directed vertex representation connected to one or more edge segments that have direction. The vertex itself is of course not directional
 * 
 * @author markr
 *
 */
public interface DirectedVertex extends Vertex {
  
  /** Function collecting entry edge segments for vertex */
  public static final Function<DirectedVertex, Iterable<? extends EdgeSegment>> GET_ENTRY_EDGE_SEGMENTS =
          DirectedVertex::getEntryEdgeSegments;

  /** Function collecting exit edge segments for vertex */
  public static final Function<DirectedVertex, Iterable<? extends EdgeSegment>> GET_EXIT_EDGE_SEGMENTS =
          DirectedVertex::getExitEdgeSegments;
  
  /** Collect lambda function that collects either up or downstream edge segments
   * 
   * @param getEntrySegments flag indicating if entry segments lambda to collect for a given vertex
   * @return lambda function
   */
  public static Function<DirectedVertex, Iterable<? extends EdgeSegment>> getEdgeSegmentsForVertexLambda(
      boolean getEntrySegments) {
    return getEntrySegments ? GET_ENTRY_EDGE_SEGMENTS : GET_EXIT_EDGE_SEGMENTS;
  }

  /**
   * Collect lambda function that collects the vertices one step away from a vertex along the edge segments meeting a
   * condition: downstream, at the end of its exit segments, or upstream, at the start of its entry segments
   *
   * @param <V> type of vertex
   * @param eligibleSegment condition an edge segment must meet to be followed
   * @param upstream when true step upstream along entry segments, when false downstream along exit segments
   * @return lambda function, leaving out an edge segment without a vertex at the far end
   */
  @SuppressWarnings("unchecked")
  public static <V extends DirectedVertex> Function<V, Iterable<V>> getAdjacentVerticesForVertexLambda(
      Predicate<? super EdgeSegment> eligibleSegment, boolean upstream) {
    final var edgeSegments = getEdgeSegmentsForVertexLambda(upstream);
    final var adjacentVertex = EdgeSegment.getVertexForEdgeSegmentLambda(upstream);
    return vertex -> () -> IterableUtils.asStream(edgeSegments.apply(vertex))
        .filter(eligibleSegment::test).map(segment -> (V) adjacentVertex.apply(segment)).filter(Objects::nonNull)
        .iterator();
  }

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
  public Iterable<? extends EdgeSegment> getEntryEdgeSegments();

  /**
   * Collect the exit edge segments of this vertex (unmodifiable)
   * 
   * @return edgeSegments
   */
  public Iterable<? extends EdgeSegment> getExitEdgeSegments();

  /**
   * Provide a stream of all edge segments adjacent to this vertex
   * @return stream of edge segments
   */
  public default Stream<? extends EdgeSegment> streamEdgeSegments(){
    return Stream.concat(streamEntrySegments(),streamExitSegments());
  }

  /**
   * Provide a stream of entry edge segments adjacent to this vertex
   * @return stream of entry edge segments
   */
  public default Stream<? extends EdgeSegment> streamEntrySegments(){
    return IterableUtils.asStream(getEntryEdgeSegments());
  }

  /**
   * Provide a stream of entry edge segments adjacent to this vertex
   * @return stream of entry edge segments
   */
  public default Stream<? extends EdgeSegment> streamExitSegments(){
    return IterableUtils.asStream(getExitEdgeSegments());
  }

  /**
   * Obtain an edge segment, no guarantee on which one, any attached one may be returned
   *
   * @return any edge segment, null if none attached
   */
  public default EdgeSegment getFirstEdgeSegment(){
    if(hasEntryEdgeSegments()){
      return getEntryEdgeSegments().iterator().next();
    }
    if(hasExitEdgeSegments()){
      return getExitEdgeSegments().iterator().next();
    }
    return null;
  }
  
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
   * Find segment in entry segments
   *
   * @param segment to find
   * @return true when present, false otherwise
   */
  public default boolean hasEntrySegment(EdgeSegment segment){
    return EdgeSegment.hasSegment(segment,getEntryEdgeSegments());
  }

  /**
   * Find segment in exit segments
   *
   * @param segment to find
   * @return true when present, false otherwise
   */
  public default boolean hasExitSegment(EdgeSegment segment){
    return EdgeSegment.hasSegment(segment,getExitEdgeSegments());
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
   * slow method because it requires iterating over the underlying iterable since it is not a collection we are
   * obtain the count from
   * 
   * @return number of entry edge segments
   */
  public default int getNumberOfEntryEdgeSegments() {
    return (int) IterableUtils.sizeOfUsingLoop(getEntryEdgeSegments());
  }

  /**
   * Collect the number of exit edge segments of this vertex
   * <p>
   * slow method because it requires iterating over the underlying iterable since it is not a collection we are
   * obtain the count from
   * 
   * @return number of exit edge segments
   */
  public default int getNumberOfExitEdgeSegments() {
    return (int) IterableUtils.sizeOfUsingLoop(getExitEdgeSegments());
  }

  public default EdgeSegment[] createEdgeSegmentsAsArray(boolean getEntrySegments){
    var result = new EdgeSegment[getNumberOfExitEdgeSegments()];
    LongAdder index = new LongAdder();
    (getEntrySegments ? getEntryEdgeSegments() : getExitEdgeSegments()).forEach(
            e -> {
              result[index.intValue()] = e;
              index.increment();
            });
    return result;
  }

  public default EdgeSegment[] createExitEdgeSegmentsArray(){
    return createEdgeSegmentsAsArray(false);
  }

  public default EdgeSegment[] createEntryEdgeSegmentsArray(){
    return createEdgeSegmentsAsArray(true);
  }

}
