package org.goplanit.utils.graph;

import org.goplanit.utils.geo.PlanitJtsUtils;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.Point;

import java.util.Collection;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Conjugate vertex representation connected to one or more conjugate edges
 * 
 * @author markr
 *
 */
public interface ConjugateVertex extends Vertex {
  
  /** vertex logger */
  public static final Logger LOGGER = Logger.getLogger(ConjugateVertex.class.getCanonicalName());
  
  /** id class for generating ids */
  public static final Class<ConjugateVertex> CONJUGATE_VERTEX_ID_CLASS = ConjugateVertex.class;

  @Override
  public default boolean hasPosition() {
    if (!hasOriginalEdgeSegment()) {
      return false;
    }
    var originalVertexUp = getOriginalEdgeSegment().getUpstreamVertex();
    var originalVertexDown = getOriginalEdgeSegment().getDownstreamVertex();
    if(!originalVertexUp.hasPosition() || !originalVertexDown.hasPosition()){
      return false;
    }

    return true;
  }

  /**
   * Conjugate vertex's position is derived on-the-fly from its parent edge. Currently, we simply
   * take the mid-point of the original edge its two vertices ignoring any projection information
   * todo: improve by considering projection and possibly the shape of the line string of the original
   *  edge
   * @return derived location
   */
  @Override
  public default Point getPosition() {
    if(!hasPosition()){
      return null;
    }
    var originalVertexUp = getOriginalEdgeSegment().getUpstreamVertex();
    var originalVertexDown = getOriginalEdgeSegment().getDownstreamVertex();
    return PlanitJtsUtils.createPoint(LineSegment.midPoint(
            originalVertexUp.getPosition().getCoordinate(),
            originalVertexDown.getPosition().getCoordinate()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Collection<? extends ConjugateEdge> getEdges();  
    
  /**
   * Shallow Clone the conjugate vertex
   * @return the cloned vertex
   */
  @Override
  public abstract ConjugateVertex shallowClone();

  /**
   * Deep Clone the conjugate vertex
   * @return the cloned vertex
   */
  @Override
  public abstract ConjugateVertex deepClone();
  
  /**
   * All vertices use the CONJUGATE_VERTEX_ID_CLASS to generate the unique internal ids
   */
  @Override
  public default Class<? extends ConjugateVertex> getIdClass() {
    return CONJUGATE_VERTEX_ID_CLASS;
  }  
 
  
  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public default Set<? extends ConjugateEdge> getEdges(Vertex otherVertex) {
    return (Set<? extends ConjugateEdge>) Vertex.super.getEdges(otherVertex);
  }  
  
  /**
   * Collect the original edge this conjugate vertex represents in the conjugate graph
   * @return original edge
   */
  public abstract EdgeSegment getOriginalEdgeSegment();

  /**
   * Verify if original edge is present or not
   *
   * @return true when present, false otherwise
   */
  public default boolean hasOriginalEdgeSegment(){
    return getOriginalEdgeSegment() != null;
  }

  /**
   * populate the XMLId by using the underlying original edge's XMLId. Optionally post-fix the result when provided.
   * If not original is not available, indicate via 'N/A' instead.
   *
   * @param deriveFromOriginalEdge when true use original edg XML id, otherwise use internal id of conjugates
   * @param postFix to apply
   */
  public default void populateXmlId(boolean deriveFromOriginalEdge, String postFix){
    boolean xmlIdAvailable = hasOriginalEdgeSegment() && getOriginalEdgeSegment().hasXmlId();
    String createdXmlId =
            deriveFromOriginalEdge && xmlIdAvailable ? getOriginalEdgeSegment().getXmlId(): "N/A";
    setXmlId(createdXmlId + postFix);
  }
}
