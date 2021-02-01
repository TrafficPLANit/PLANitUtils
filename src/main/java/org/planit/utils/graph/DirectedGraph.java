package org.planit.utils.graph;

import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 * Interface for an undirected graph
 * 
 * @author markr
 */
public interface DirectedGraph<V extends DirectedVertex, E extends DirectedEdge, ES extends EdgeSegment> extends Graph<V,E> {


  /** Collect edges segments of graph
   * @return edges segments
   */
  EdgeSegments<ES> getEdgeSegments();

  /**
   * {@inheritDoc}
   */
  @Override
  default boolean validate() {
    boolean isValid = Graph.super.validate();
    for(EdgeSegment edgeSegment : getEdgeSegments()) {
      isValid = isValid && edgeSegment.validate();
    }
    return isValid;
  }
  
  /** transform all geometries of the vertices, edges, and edge segments using the same transformer, can be used to transform from
   * one coordinate reference system to another, or perform translations, etc.
   * 
   * @param transformer to apply
   * @throws MismatchedDimensionException thrown if error
   * @throws TransformException thrown if error
   */
  @Override
  default void transformGeometries(MathTransform transformer) throws MismatchedDimensionException, TransformException {
    // currently edge segments have no geometry of their own, so just delegate
    Graph.super.transformGeometries(transformer);
  }   

}
