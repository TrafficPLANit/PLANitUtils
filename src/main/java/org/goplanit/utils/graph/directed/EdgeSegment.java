package org.goplanit.utils.graph.directed;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Predicate;

import org.goplanit.utils.graph.GraphEntity;
import org.goplanit.utils.network.layer.physical.LinkSegment;

/**
 * EdgeSegment represents an edge in a particular (single) direction. Each edge
 * has either one or two edge segments where each edge segment may have a more
 * detailed geography than its parent edge
 * 
 * 
 * @author markr
 *
 */
public interface EdgeSegment extends Serializable, GraphEntity {
  
  /** id class for generating ids */
  public static final Class<EdgeSegment> EDGE_SEGMENT_ID_CLASS = EdgeSegment.class;
  
  /** Function collecting upstream vertex for edge segment */
  public static final Function<EdgeSegment, DirectedVertex> getUpstreamVertex = e -> e.getUpstreamVertex();

  /** Function collecting downstream vertex for edge segment */
  public static final Function<EdgeSegment, DirectedVertex> getDownstreamVertex = e -> e.getDownstreamVertex();
  
  /** Collect vertex of given edge segment lambda
   * 
   * @param upstreamVertex when true collect upstream vertex, downstream vertex otherwise
   * @return lambda function
   */
  public static Function<EdgeSegment, DirectedVertex> getVertexForEdgeSegmentLambda(boolean upstreamVertex) {
    return upstreamVertex ? getUpstreamVertex : getDownstreamVertex;
  }

  /**
   * Find segment in iterable of segments
   *
   * @param edgeSegment to find
   * @param edgeSegments to find them in
   * @return true when present, false otherwise
   */
  public static boolean hasSegment(EdgeSegment edgeSegment, Iterable<? extends EdgeSegment> edgeSegments){
    if( edgeSegments == null){
      return false;
    }

    for(var currSegment : edgeSegments){
      if(currSegment.equals(edgeSegment)){
        return true;
      }
    }

    return false;
  }
 
  /**
   * Get the segment's upstream vertex
   * 
   * @return upstream vertex
   */
  public default DirectedVertex getUpstreamVertex() {
    return isDirectionAb() ? getParent().getVertexA() : getParent().getVertexB();
  }

  /**
   * Get the segment's downstream vertex
   * 
   * @return downstream vertex
   */
  public default DirectedVertex getDownstreamVertex() {
    return isDirectionAb() ? getParent().getVertexB() : getParent().getVertexA();
  }

  /**
   * Test if predicate yields true on any of the two vertices of the segment
   *
   * @param matcher to apply
   * @return true when any vertex matches, false otherwise
   */
  public default boolean anyVertexMatches(Predicate<DirectedVertex> matcher) {
    var parentEdge = getParent();
    return (parentEdge.hasVertexA() && matcher.test(parentEdge.getVertexA())) ||
            (parentEdge.hasVertexB() && matcher.test(parentEdge.getVertexB()));
  }
  
  /**
   * Collect the parent edge of the segment
   * 
   * @return parentEdge
   */
  public abstract DirectedEdge getParent();

  /**
   * Verify if segment has parent set
   *
   * @return true if present, false otherwise
   */
  public default boolean hasParent(){
    return getParent() != null;
  }
  
  /**
   * remove the parent edge from this edge segment
   */
  public abstract void removeParentEdge();
  
  /**
   * check if edge segment runs from vertex a to b or b to a
   * @return true when running from a to b, otherwise false
   */
  public abstract boolean isDirectionAb();
  
  /** validate the contents of this edge segment
   * @return true when valid, false otherwise
   */
  public abstract boolean validate(); 

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract EdgeSegment shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract EdgeSegment deepClone();

  /**
   * Set the parent edge
   * 
   * @param parentEdge to set
   */
  public abstract void setParent(DirectedEdge parentEdge);
  
  /**
   * All edges use the EDGE_SEGMENT_ID_CLASS to generate the unique internal ids
   */
  @Override
  public default Class<? extends EdgeSegment> getIdClass() {
    return EDGE_SEGMENT_ID_CLASS;
  }   
   
  /** verify if parent (edge) has a name
   * 
   * @return true when present, false otherwise
   */
  public default boolean hasParentName() {
    if(getParent()!=null) {
      return getParent().hasName();
    }
    return false;
  }
  
  /** verify if parent (edge) has a name
   * 
   * @return true when present, false otherwise
   */
  public default String getParentName() {
    if(getParent()!=null) {
      return getParent().getName();
    }
    return null;
  }

  /** Collect the opposite direction segment of this edge segment (if any)
   * 
   * @return opposite direction segment, null if not present
   */
  public default EdgeSegment getOppositeDirectionSegment() {
    return this==getParent().getEdgeSegmentAb() ? getParent().getEdgeSegmentBa() : getParent().getEdgeSegmentAb();  
  }

  /**
   * Verify f opposite direction segment exists
   *
   * @return true when exists, false otherwise
   */
  public default boolean hasOppositeDirectionSegment(){
    return getOppositeDirectionSegment()!=null;
  }

  /**
   * Assuming geometry is present, if not false is returned, we verify if the geometry is provided in the direction of the
   * segment or not
   *
   * @param allowSingleVertexWithoutGeometry when true, we assume that geometry of edge is ok to be not matching vertex on one end
   * @return true when geometry direction coincides with segment direction, false otherwise
   */
  public default boolean isParentGeometryInSegmentDirection(boolean allowSingleVertexWithoutGeometry ){
    var geometry = getParent().getGeometry();
    if(geometry == null){
      return false;
    }
    return getParent().isGeometryInAbDirection(allowSingleVertexWithoutGeometry) == isDirectionAb();
  }

  /** Verify if provided edge segment is adjacent to this edge segment taking direction into account, i.e., either an upstream
   * segment is directly adjacent to this segment, or this segment connects to a directly adjcent downstream segment
   *
   * @param other edge segment to verify adjacency
   * @param allowUTurn when true the opposite direction segment is considered adjacent, otherwise not
   * @return true when adjacent, false otherwise
   */
  public default boolean isAdjacent(EdgeSegment other, boolean allowUTurn){
    if(other == null){
      return false;
    }

    if(other.equals(getOppositeDirectionSegment())){
      return allowUTurn ? true : false;
    }

    return getUpstreamVertex().hasEntrySegment(other) || getDownstreamVertex().hasExitSegment(other);
  }

  /**
   * Provides the length in km based on its parent's information
   *
   * @return length in km of the segment
   */
  public default double getLengthKm(){
    return getParent().getLengthKm();
  }

  /**
   * verify if geometry is present based on parent's geometry
   *
   * @return true when parent has geometry, false otherwise
   */
  public default boolean hasGeometry(){
    return getParent().hasGeometry();
  }

}
