package org.planit.utils.graph;

import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.planit.utils.graph.Edge;
import org.planit.utils.graph.Edges;
import org.planit.utils.graph.Vertex;

/**
 * 
 * A graph interface consisting of vertices and edges
 * 
 * @author markr
 *
 */
public interface Graph<V extends Vertex, E extends Edge> {

  /**
   * collect the id of this graph
   * 
   * @return graph id
   */
  long getId();

  /** collect vertices of graph
   * @return vertices
   */
  Vertices<V> getVertices();
  
  /** collect edges of graph
   * @return edges
   */
  Edges<E> getEdges(); 
  
  /**
   * validate the graph, issues will be logged
   * 
   * @return true when valid, false otherwise
   */
  default boolean validate() {
    boolean isValid = true;
    for(Edge edge : getEdges()) {
      isValid = isValid && edge.validate();
    }
    for(Vertex vertex : getVertices()) {
      isValid = isValid && vertex.validate();
    }
    return isValid;
  }
  
  /** transform all geometries of the nodes and edges using the same transformer, can be used to transform from
   * one coordinate reference system to another, or perform translations, etc.
   * 
   * @param transformer to apply
   * @throws MismatchedDimensionException thrown if error
   * @throws TransformException thrown if error
   */
  default void transformGeometries(MathTransform transformer) throws MismatchedDimensionException, TransformException {
    for(Edge edge : getEdges()) {
      edge.transformGeometry(transformer);
    }
    for(Vertex vertex : getVertices()) {
      vertex.transformPosition(transformer);
    }
  } 

}
