package org.goplanit.utils.graph;

import org.goplanit.utils.graph.Edge;
import org.goplanit.utils.graph.Vertex;
import org.goplanit.utils.id.ManagedId;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import java.util.function.BiConsumer;

/**
 * 
 * A graph interface consisting of vertices and edges without specifying a concrete container type
 * to access them. Only generic method signatures in this base interface.
 * 
 * @author markr
 *
 */
public interface UntypedGraph<V extends Vertex, E extends Edge> extends ManagedId {
  
  /** id class for generating ids */
  @SuppressWarnings("rawtypes")
  public static final Class<UntypedGraph> GRAPH_ID_CLASS = UntypedGraph.class;    

  /** collect vertices of graph
   * @return vertices
   */
  public abstract GraphEntities<V> getVertices();
  
  /** collect edges of graph
   * @return edges
   */
  public abstract GraphEntities<E> getEdges(); 
  
  /** Verify if empty, empty when no nodes and edges exist
   * 
   * @return true when no nodes and edges, false otherwise
   */
  public default boolean isEmpty() {
    return (getVertices()==null || getVertices().isEmpty()) && (getEdges()==null || getEdges().isEmpty());
  }
  
  /**
   * validate the graph, issues will be logged
   * 
   * @return true when valid, false otherwise
   */
  public default boolean validate() {
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
  public default void transformGeometries(MathTransform transformer) throws MismatchedDimensionException, TransformException {
    for(Edge edge : getEdges()) {
      edge.transformGeometry(transformer);
    }
    for(Vertex vertex : getVertices()) {
      vertex.transformPosition(transformer);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UntypedGraph<V, E> shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UntypedGraph<V, E> deepClone();

}
