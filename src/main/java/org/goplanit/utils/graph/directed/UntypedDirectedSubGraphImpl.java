package org.goplanit.utils.graph.directed;

import org.goplanit.utils.graph.UntypedSubGraphImpl;
import org.goplanit.utils.id.IdGroupingToken;

import java.util.BitSet;
import java.util.logging.Logger;

/**
 *
 * A directed sub graph contains a subset of the full directed graph. The active subset of the graph is tracked by
 * explicitly registering vertices, edges, and edge segments.
 *
 * @author markr
 *
 * @param <V> type of vertex
 * @param <E> type of edge
 */
public class UntypedDirectedSubGraphImpl<V extends DirectedVertex, E extends DirectedEdge, ES extends EdgeSegment>
    extends UntypedSubGraphImpl<V,E> implements UntypedDirectedSubGraph<V, E, ES> {

  /** logger to use */
  private static final Logger LOGGER = Logger.getLogger(
      org.goplanit.graph.directed.acyclic.UntypedACyclicSubGraphImpl.class.getCanonicalName());

  /** track the edge segments used via a bit set, where 1 at index indicates the edge with id=index is included */
  private BitSet registeredEdgeSegments;


  /**
   * Constructor
   *
   * @param groupId                    generate id based on the group it resides in
   * @param numberOfParentVertices number of vertices of the parent this subgraph is a subset from
   * @param numberOfParentEdges number of edges of the parent this subgraph is a subset from
   * @param numberOfParentEdgeSegments number of edge segments of the parent this subgraph is a subset from
   */
  public UntypedDirectedSubGraphImpl(
      final IdGroupingToken groupId,
      int numberOfParentVertices,
      int numberOfParentEdges,
      int numberOfParentEdgeSegments) {
    super(groupId, numberOfParentEdges, numberOfParentVertices);
    this.registeredEdgeSegments = new BitSet(numberOfParentEdgeSegments);
  }

  /**
   * Copy constructor
   *
   * @param other to copy
   * @param deepCopy when true, create a deep copy, shallow copy otherwise
   */
  public UntypedDirectedSubGraphImpl(UntypedDirectedSubGraphImpl<V, E, ES> other, boolean deepCopy) {
    super(other, deepCopy);
    this.registeredEdgeSegments = BitSet.valueOf(other.registeredEdgeSegments.toByteArray());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addEdgeSegment(ES edgeSegment) {
    if (edgeSegment == null) {
      LOGGER.warning("Unable to add edgeSegment, null provided");
      return;
    }

    registeredEdgeSegments.set((int) edgeSegment.getId());
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public void removeEdgeSegment(ES edgeSegment) {
    registeredEdgeSegments.set((int) edgeSegment.getId(), false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean containsEdgeSegment(EdgeSegment edgeSegment) {
    if (edgeSegment == null) {
      return false;
    }
    return containsEdgeSegment(edgeSegment.getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean containsEdgeSegment(long edgeSegmentId) {
    return registeredEdgeSegments.get((int) edgeSegmentId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getNumberOfEdgeSegments() {
    return registeredEdgeSegments.cardinality();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UntypedDirectedSubGraphImpl<V, E, ES> shallowClone() {
    return new UntypedDirectedSubGraphImpl<>(this, false);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UntypedDirectedSubGraphImpl<V, E, ES> deepClone() {
    return new UntypedDirectedSubGraphImpl<>(this, true);
  }

}

