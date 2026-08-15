package org.goplanit.utils.graph;

import org.goplanit.utils.id.IdAble;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A subgraph interface for a given parent graph by registering edges on it
 * (and therefore vertices)
 * 
 * @author markr
 *
 */
public interface UntypedSubGraph<V extends Vertex, E extends Edge> extends IdAble {
    
  /** Register an edge on the subgraph
   * 
   * @param edge to add
   */
  public abstract void addEdge(E edge);
  
  /** Remove an edge from the subgraph
   * 
   * @param edge to remove
   */
  public abstract void removeEdge(E edge);
  
  /** Verify if given edge is registered on this subgraph
   * 
   * @param edge to verify
   * @return true when registered, false otherwise
   */
  public abstract boolean containsEdge(E edge);

  /**
   * Verify if the graph contains the given edge
   *
   * @param edgeId to verify
   * @return true when present, false otherwise
   */
  public abstract boolean containsEdge(long edgeId);

  /** Register a vertex on the subgraph
   *
   * @param vertex to add
   */
  public abstract void addVertex(V vertex);

  /** Remove a vertex from the subgraph
   *
   * @param vertex to remove
   */
  public abstract void removeVertex(V vertex);

  /** Verify if given vertex is registered on this subgraph
   *
   * @param vertex to verify
   * @return true when registered, false otherwise
   */
  public abstract boolean containsVertex(V vertex);

  /**
   * Verify if the graph contains the given edge
   *
   * @param vertexId to verify
   * @return true when present, false otherwise
   */
  public abstract boolean containsVertex(long vertexId);

  /** Collect the number of edges that are present in the subgraph for the given
   * vertex on the parent graph
   *  
   * @param vertex to verify
   * @return number of subgraph edges
   */
  @SuppressWarnings("unchecked")
  public default int getNumberOfEdges(V vertex) {
    int numSubGraphVertexSegments = 0;
    for(var edge : vertex.getEdges()) {
      if(containsEdge((E)edge)) {
        ++numSubGraphVertexSegments;
      }
    }
    return numSubGraphVertexSegments;
  }

  /**
   * Construct a new list from all vertices on this subgraph. (costly)
   * @param parentVertices to check against
   * @return list of vertices
   */
  public default List<V> getVertices(GraphEntities<? extends V> parentVertices) {
    return parentVertices.stream().filter(this::containsVertex).collect(Collectors.toList());
  }

  /**
   * Construct a new list from all edges on this subgraph. (costly)
   * @param parentEdges to check against
   * @return list of edges
   */
  public default List<E> getEdges(GraphEntities<? extends E> parentEdges) {
    return parentEdges.stream().filter(this::containsEdge).collect(Collectors.toList());
  }

  /**
   * Number of edges on subgraph
   * @return number
   */
  public abstract int getNumberOfEdges();

  /**
   * Number of vertices on subgraph
   * @return number
   */
  public abstract int getNumberOfVertices();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UntypedSubGraph<V,E> shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UntypedSubGraph<V,E> deepClone();
  
}
