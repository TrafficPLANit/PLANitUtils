package org.goplanit.utils.graph;

import org.goplanit.utils.id.IdGenerator;
import org.goplanit.utils.id.IdGroupingToken;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
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
   * <p>
   * Membership is tracked by id in a bit set, so the members are collected by walking the set bits and looking each
   * one up in the parent container, which is a keyed lookup. The interface default instead scans the whole parent
   * container and tests each entity for membership, making a single call proportional to the size of the entire
   * graph rather than to the size of this subgraph. That difference dominates any caller that identifies or removes
   * many subgraphs, e.g. dangling subnetwork removal, which calls this once per subgraph and would otherwise be
   * quadratic in the graph size.
   * </p>
   */
  @Override
  public List<V> getVertices(GraphEntities<? extends V> parentVertices) {
    return collectRegistered(registeredVertices, parentVertices);
  }

  /**
   * {@inheritDoc}
   * <p>
   * See {@link #getVertices(GraphEntities)} for why this does not use the scanning default.
   * </p>
   */
  @Override
  public List<E> getEdges(GraphEntities<? extends E> parentEdges) {
    return collectRegistered(registeredEdges, parentEdges);
  }

  /** Collect the parent entities whose id is registered in the given bit set.
   *
   * @param <T> type of entity
   * @param registered bit set holding the ids that are part of this subgraph
   * @param parentEntities container to look the ids up in
   * @return the registered entities present on the parent container
   */
  protected static <T> List<T> collectRegistered(
      final BitSet registered, final GraphEntities<? extends T> parentEntities) {
    var result = new ArrayList<T>(registered.cardinality());
    for (int id = registered.nextSetBit(0); id >= 0; id = registered.nextSetBit(id + 1)) {
      var entity = parentEntities.get(id);
      if (entity != null) {
        /* an id can be registered while the entity is no longer on the parent container, e.g. when a caller
         * removes entities in between identifying a subgraph and materialising it */
        result.add(entity);
      }
    }
    return result;
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

