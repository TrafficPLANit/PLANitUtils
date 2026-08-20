package org.goplanit.utils.graph;

import org.goplanit.utils.id.IdGenerator;
import org.goplanit.utils.id.IdGroupingToken;

import java.util.BitSet;
import java.util.logging.Logger;

/**
 *
 * A sub graph contains a subset of the full graph. The active subset of the graph is tracked by
 * explicitly registering edges and vertices.
 *
 * @author markr
 *
 * @param <V> type of vertex
 * @param <E> type of edge
 */
public class UntypedSubGraphImpl<V extends Vertex, E extends Edge> implements UntypedSubGraph<V, E> {

  /** logger to use */
  private static final Logger LOGGER = Logger.getLogger(UntypedSubGraphImpl.class.getCanonicalName());

  /**
   * The id of this sub graph
   */
  private final long id;


  /** track the edges used via a bit set, where 1 at index indicates the edge with id=index is included */
  private final BitSet registeredEdges;

  /** track the vertices used via a bit set, where 1 at index indicates the vertex with id=index is included */
  private final BitSet registeredVertices;

  /**
   * Constructor
   *
   * @param groupId                    generate id based on the group it resides in
   * @param numberOfParentVertices number of vertices of the parent this subgraph is a subset from
   * @param numberOfParentEdges number of edges of the parent this subgraph is a subset from
   */
  public UntypedSubGraphImpl(
      final IdGroupingToken groupId, int numberOfParentVertices, int numberOfParentEdges) {
    this.id = IdGenerator.generateId(groupId, UntypedSubGraph.class);
    this.registeredEdges = new BitSet(numberOfParentEdges);
    this.registeredVertices = new BitSet(numberOfParentVertices);
  }

  /**
   * Copy constructor
   *
   * @param other to copy
   * @param deepCopy when true, create a deep copy, shallow copy otherwise
   */
  public UntypedSubGraphImpl(UntypedSubGraphImpl<V, E> other, boolean deepCopy) {
    this.id = other.getId();
    this.registeredEdges = BitSet.valueOf(other.registeredEdges.toByteArray());
    this.registeredVertices = BitSet.valueOf(other.registeredVertices.toByteArray());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long getId() {
    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addEdge(E edge) {
    if (edge == null) {
      LOGGER.warning("Unable to add edge, null provided");
      return;
    }

    registeredEdges.set((int) edge.getId());
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public void removeEdge(E edge) {
    registeredEdges.set((int) edge.getId(), false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean containsEdge(E edge) {
    if (edge == null) {
      return false;
    }
    return containsEdge(edge.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean containsEdge(long edgeId) {
    return registeredEdges.get((int) edgeId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addVertex(V vertex) {
    if (vertex == null) {
      LOGGER.warning("Unable to add vertex, null provided");
      return;
    }

    registeredVertices.set((int) vertex.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removeVertex(V vertex) {
    registeredVertices.set((int) vertex.getId(), false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean containsVertex(V vertex) {
    if (vertex == null) {
      return false;
    }
    return containsVertex(vertex.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean containsVertex(long vertexId) {
    return registeredVertices.get((int) vertexId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getNumberOfEdges() {
    return registeredEdges.cardinality();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getNumberOfVertices() {
    return registeredVertices.cardinality();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public UntypedSubGraphImpl<V, E> shallowClone() {
    return new UntypedSubGraphImpl<>(this, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UntypedSubGraphImpl<V, E> deepClone() {
    return new UntypedSubGraphImpl<>(this, true);
  }

}

