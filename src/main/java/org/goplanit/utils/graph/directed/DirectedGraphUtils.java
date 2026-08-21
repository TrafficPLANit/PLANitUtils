package org.goplanit.utils.graph.directed;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.graph.*;
import org.goplanit.utils.graph.directed.algorithms.BreadthFirstSearch;
import org.goplanit.utils.id.IdGenerator;
import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.misc.Pair;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Utilities for directed graphs
 *
 * @author markr
 */
public class DirectedGraphUtils {

  /**
   * Perform a breadth-first search from a starting vertex in a given direction with conditions
   * on:
   * <ul>
   *   <li>what initial edge segments from the start vertex to consider</li>
   *   <li>what edge segments from any other vertex to consider</li>
   *   <li>what constitutes a successful search by defining a termination condition</li>
   * </ul>
   *
   * @param <ES> edge segment type
   * @param <V> directed vertex type
   * @param startVertex to start search from
   * @param invertDirection direction for searching, when true invert direction from downstream to upstream
   * @param initialVertexSegmentInclusionCondition predicate for initial condition on edge segments to consider
   * @param vertexSegmentInclusionCondition predicate for general condition on edge segments to consider (prevEdgeSegment, currEdgeSegment)
   * @param vertexSegmentTerminationCondition predicate for termination condition for successful search completion
   * @return found vertex (if any) based on termination and the back links for all processed vertices
   */
  public static <V extends DirectedVertex,ES extends EdgeSegment> Pair<V, Map<V, ES>> breadthFirstSearch(
      V startVertex,
      boolean invertDirection,
      Predicate<ES> initialVertexSegmentInclusionCondition,
      BiPredicate<ES, ES> vertexSegmentInclusionCondition,
      BiPredicate<V,ES> vertexSegmentTerminationCondition){
    /* implementation lives with its peers in the algorithms package, this remains the established entry point */
    return BreadthFirstSearch.execute(
        startVertex,
        invertDirection,
        initialVertexSegmentInclusionCondition,
        vertexSegmentInclusionCondition,
        vertexSegmentTerminationCondition);
  }

  /**
   * Number of edge segments registered under id token used by reference graph
   *
   * @param referenceGraph to use
   * @return num edge segments
   * @param <V> type of vertex
   * @param <E> type of edge
   * @param <ES> type of edge segment
   */
  public static <V extends DirectedVertex, E extends DirectedEdge, ES extends EdgeSegment>
  long getNumberOfEdgeSegmentsRegisteredUnderIdToken(UntypedDirectedGraph<V,E,ES> referenceGraph){
    return IdGenerator.getLatestIdForToken(
        referenceGraph.getEdgeSegments().getFactory().getIdGroupingToken(), EdgeSegment.EDGE_SEGMENT_ID_CLASS);
  }

  /**
   * Identify the connected subgraph for a vertex without imposing any condition on edge segments, leniently. Equivalent
   * to {@link #identifySubGraphForVertex(UntypedDirectedGraph, DirectedVertex, Predicate, Predicate, boolean)} with an
   * always true edge segment test and strict set to false.
   *
   * @param referenceGraph the parent graph the subgraph is a subset of
   * @param referenceVertex to process
   * @param testEdge when an edge tests positive it is included in the subgraph
   * @return subgraph connected to passed in reference vertex
   * @param <V> type of vertex
   * @param <E> type of edge
   * @param <ES> type of edge segment
   */
  public static <V extends DirectedVertex, E extends DirectedEdge, ES extends EdgeSegment>
  UntypedDirectedSubGraph<V,E,ES> identifySubGraphForVertex(
      UntypedDirectedGraph<V,E,ES> referenceGraph,
      DirectedVertex referenceVertex,
      Predicate<? super E> testEdge) {
    return identifySubGraphForVertex(referenceGraph, referenceVertex, testEdge, es -> true, false);
  }

  /**
   * Helper function for subgraph identification (deliberately NOT recursive to avoid stack overflow on
   * large networks)
   * <p>
   * Traversal always follows connectivity across edges that qualify, so the full connected component is explored
   * regardless of strictness. Strictness only governs what ends up registered on the returned subgraph.
   * </p>
   * Inclusion rules:
   * <ul>
   *   <li>an edge segment is included when its parent edge is included and it tests positive</li>
   *   <li>an edge is included when it tests positive and, in strict mode, <b>all</b> of its edge segments test
   *   positive, or, in lenient mode, <b>any</b> of them does. An edge carrying no segments at all is judged on the
   *   edge test alone</li>
   *   <li>a vertex is included when, in strict mode, <b>all</b> of its edges are included, i.e. it lies wholly within
   *   the subgraph, or, in lenient mode, <b>any</b> of them is</li>
   * </ul>
   * Use strict when the result drives removal, so entities still referenced by the retained graph are not part of it;
   * use lenient when the result is used to identify connectivity.
   *
   * @param referenceGraph the parent graph the subgraph is a subset of
   * @param referenceVertex to process
   * @param testEdge when edge tests positive it should be included on individual basis,
   * @param testEdgeSegment when edgeSegment tests positive, it will be included
   * @param strict see inclusion rules above
   * @return subgraph of all vertices, edges and edge segments in the subnetwork connected to passed in reference vertex
   * @param <V> type of vertex
   * @param <E> type of edge
   * @param <ES> type of edge segment
   */
  public static <V extends DirectedVertex, E extends DirectedEdge, ES extends EdgeSegment>
  UntypedDirectedSubGraph<V,E,ES> identifySubGraphForVertex(
      UntypedDirectedGraph<V,E,ES> referenceGraph,
      DirectedVertex referenceVertex,
      Predicate<? super E> testEdge,
      Predicate<? super ES> testEdgeSegment,
      boolean strict) {

    PlanItRunTimeException.throwIfNull(referenceVertex, "Provided reference vertex is null " +
        "when identifying its subnetwork, this is not allowed");

    // rely on max id instead of container size, since entities can be shared between graphs and not owned by the
    // parent graph
    var numVertices = UndirectedGraphUtils.getNumberOfVerticesRegisteredUnderIdToken(referenceGraph);
    var numEdges = UndirectedGraphUtils.getNumberOfEdgesRegisteredUnderIdToken(referenceGraph);
    var numEdgeSegments = getNumberOfEdgeSegmentsRegisteredUnderIdToken(referenceGraph);

    var subGraph = new UntypedDirectedSubGraphImpl<V,E,ES>(
        IdGroupingToken.collectGlobalToken(), (int) numVertices, (int) numEdges, (int) numEdgeSegments);

    /* track visited separately from subgraph membership: under strict rules a vertex may be explored but not
     * registered, in which case subgraph membership would never mark it as seen and it would be revisited forever */
    final BitSet visitedVertices = new BitSet((int) numVertices);

    Deque<DirectedVertex> verticesToExplore = new ArrayDeque<>();
    verticesToExplore.add(referenceVertex);
    visitedVertices.set((int) referenceVertex.getId());

    while (!verticesToExplore.isEmpty()) {
      DirectedVertex currVertex = verticesToExplore.pop();

      Collection<E> edgesOfCurrVertex = (Collection<E>) currVertex.getEdges();
      boolean allEdgesIncluded = true;
      for (E currEdge : edgesOfCurrVertex) {

        if(!testEdge.test(currEdge) || !qualifiesOnEdgeSegments(currEdge, testEdgeSegment, strict)){
          /* vertex is attached to something outside the subgraph, so not wholly contained by it */
          allEdgesIncluded = false;
          continue;
        }
        subGraph.addEdge(currEdge);

        /* register the qualifying segments of this edge; under strict rules that is all of them by construction.
         * An edge only exposes its segments as the base type, while the graph guarantees they are ES */
        if(currEdge.hasEdgeSegmentAb() && testEdgeSegment.test((ES) currEdge.getEdgeSegmentAb())){
          subGraph.addEdgeSegment((ES) currEdge.getEdgeSegmentAb());
        }
        if(currEdge.hasEdgeSegmentBa() && testEdgeSegment.test((ES) currEdge.getEdgeSegmentBa())){
          subGraph.addEdgeSegment((ES) currEdge.getEdgeSegmentBa());
        }

        /* both extremities are candidates; they are tested independently since an already visited vertex A must
         * not stop vertex B from being explored */
        exploreIfUnvisited(currEdge.getVertexA(), currVertex, visitedVertices, verticesToExplore);
        exploreIfUnvisited(currEdge.getVertexB(), currVertex, visitedVertices, verticesToExplore);
      }

      /* lenient: reaching a vertex means an incident edge was included, so it qualifies. A vertex without any
       * included edges (an isolated reference vertex) still forms a subgraph of size one.
       * strict: every incident edge must be included, which holds vacuously for an isolated vertex */
      if(!strict || allEdgesIncluded){
        subGraph.addVertex((V) currVertex);
      }
    }
    return subGraph;
  }

  /**
   * Verify whether the edge segments of an edge allow the edge to be included, see the inclusion rules on
   * {@link #identifySubGraphForVertex(UntypedDirectedGraph, DirectedVertex, Predicate, Predicate, boolean)}
   *
   * @param edge to verify
   * @param testEdgeSegment to apply to each present segment
   * @param strict when true all present segments must test positive, when false at least one must
   * @return true when the edge qualifies, false otherwise
   * @param <ES> type of edge segment
   */
  @SuppressWarnings("unchecked")
  private static <ES extends EdgeSegment> boolean qualifiesOnEdgeSegments(
      DirectedEdge edge, Predicate<? super ES> testEdgeSegment, boolean strict){
    boolean hasAb = edge.hasEdgeSegmentAb();
    boolean hasBa = edge.hasEdgeSegmentBa();
    if(!hasAb && !hasBa){
      /* nothing to test against, defer to the edge level test which has already passed */
      return true;
    }

    boolean abQualifies = hasAb && testEdgeSegment.test((ES) edge.getEdgeSegmentAb());
    boolean baQualifies = hasBa && testEdgeSegment.test((ES) edge.getEdgeSegmentBa());

    if(strict){
      return (!hasAb || abQualifies) && (!hasBa || baQualifies);
    }
    return abQualifies || baQualifies;
  }

  /**
   * Queue an adjacent vertex for exploration when it has not been seen before and is not the vertex we came from
   *
   * @param candidate adjacent vertex to consider, may be null
   * @param currVertex vertex currently being processed
   * @param visitedVertices tracking of already queued/processed vertices by id
   * @param verticesToExplore queue to supplement
   */
  private static void exploreIfUnvisited(
      DirectedVertex candidate,
      DirectedVertex currVertex,
      BitSet visitedVertices,
      Deque<DirectedVertex> verticesToExplore){
    if(candidate == null || candidate.getId() == currVertex.getId()){
      return;
    }
    if(visitedVertices.get((int) candidate.getId())){
      return;
    }
    visitedVertices.set((int) candidate.getId());
    verticesToExplore.add(candidate);
  }
}
