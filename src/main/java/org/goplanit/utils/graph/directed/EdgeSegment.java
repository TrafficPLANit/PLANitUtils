package org.goplanit.utils.graph.directed;

import java.io.Serializable;
import java.util.function.Function;

import org.goplanit.utils.graph.GraphEntity;

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
   * Collect the parent edge of the segment
   * 
   * @return parentEdge
   */
  public abstract DirectedEdge getParent();
  
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
   * Clone the edge segment
   * 
   * @return copy of this instance
   */
  public abstract EdgeSegment clone();

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
   * Assuming geometry is present, if not false is returned, we verify if the geometry is provided in the direction of the
   * segment or not
   * @return true when geometry direction coincides with segment direction, false otherwise
   */
  public default boolean isParentGeometryInSegmentDirection(){
    var geometry = getParent().getGeometry();
    if(geometry == null){
      return false;
    }
    return getParent().isGeometryInAbDirection() == isDirectionAb();
  }
}
