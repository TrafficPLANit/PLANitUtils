package org.goplanit.utils.graph.directed;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.graph.*;
import org.goplanit.utils.graph.directed.algorithms.BreadthFirstSearch;
import org.goplanit.utils.graph.directed.algorithms.StronglyConnectedComponents;
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

      /* every qualifying edge belongs to this subgraph, since a weakly connected subgraph is closed under edges:
       * anything reachable is by definition part of it. Each included edge extends the frontier */
      registerVertexAndItsEdges(
          subGraph, currVertex, testEdge, testEdgeSegment, strict,
          (edge, vertex) -> true,
          edge -> {
            /* both extremities are candidates; they are tested independently since an already visited vertex A
             * must not stop vertex B from being explored */
            exploreIfUnvisited(edge.getVertexA(), currVertex, visitedVertices, verticesToExplore);
            exploreIfUnvisited(edge.getVertexB(), currVertex, visitedVertices, verticesToExplore);
          });
    }
    return subGraph;
  }

  /**
   * Apply the inclusion rules of
   * {@link #identifySubGraphForVertex(UntypedDirectedGraph, DirectedVertex, Predicate, Predicate, boolean)} to a
   * single vertex, registering the vertex and its qualifying edges and edge segments on the given subgraph.
   * <p>
   * Shared by both notions of connectivity, which differ only in how they arrive at the vertices to apply this to
   * and in whether an edge can lead out of the subgraph at all.
   * </p>
   *
   * @param subGraph to register on
   * @param currVertex the vertex to judge
   * @param testEdge when an edge tests positive it is eligible for inclusion
   * @param testEdgeSegment when an edge segment tests positive it is eligible for inclusion
   * @param strict when true the vertex is only registered if all of its edges are included
   * @param withinSubGraph whether an otherwise eligible edge belongs to this subgraph given the vertex it is
   *          considered from. An edge ruled out here is not registered, but unlike an edge that fails the tests it
   *          does not stop its vertex from being registered, see
   *          {@link #identifyStronglyConnectedSubGraphs(UntypedDirectedGraph, Predicate, Predicate, boolean)}
   * @param onIncludedEdge invoked for each edge registered, allowing a caller to extend its traversal
   * @param <V> type of vertex
   * @param <E> type of edge
   * @param <ES> type of edge segment
   */
  @SuppressWarnings("unchecked")
  private static <V extends DirectedVertex, E extends DirectedEdge, ES extends EdgeSegment>
  void registerVertexAndItsEdges(
      UntypedDirectedSubGraphImpl<V,E,ES> subGraph,
      DirectedVertex currVertex,
      Predicate<? super E> testEdge,
      Predicate<? super ES> testEdgeSegment,
      boolean strict,
      BiPredicate<E, DirectedVertex> withinSubGraph,
      Consumer<E> onIncludedEdge) {

    boolean allEdgesIncluded = true;
    for (E currEdge : (Collection<E>) currVertex.getEdges()) {

      if(!testEdge.test(currEdge) || !qualifiesOnEdgeSegments(currEdge, testEdgeSegment, strict)){
        /* vertex is attached to something outside the subgraph, so not wholly contained by it */
        allEdgesIncluded = false;
        continue;
      }

      if(!withinSubGraph.test(currEdge, currVertex)){
        /* eligible, yet not part of this subgraph. Deliberately without disqualifying the vertex */
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

      onIncludedEdge.accept(currEdge);
    }

    /* lenient: reaching a vertex means an incident edge was included, so it qualifies. A vertex without any
     * included edges (an isolated reference vertex) still forms a subgraph of size one.
     * strict: every incident edge must be included, which holds vacuously for an isolated vertex */
    if(!strict || allEdgesIncluded){
      subGraph.addVertex((V) currVertex);
    }
  }

  /**
   * Partition a directed graph into its strongly connected subgraphs, i.e. groups of vertices that can each reach
   * every other one while following direction.
   * <p>
   * The counterpart of {@link #identifySubGraphForVertex(UntypedDirectedGraph, DirectedVertex, Predicate,
   * Predicate, boolean)} for {@link Connectivity#STRONG}. Where that method discovers one subgraph per call by
   * traversing outwards from a vertex, this partitions the graph in a single pass and returns the result keyed by
   * vertex, because a caller pruning subgraphs asks the question once per vertex and repeating the partition for
   * each of them would be quadratic in the size of the graph.
   * </p>
   * <b>Inclusion rules, and how they differ from the weakly connected case.</b> A weakly connected subgraph is
   * closed under edges: no edge leaves it. A strongly connected one is not, and the edge that leaves it is the
   * very thing that makes it a separate component. That forces a distinction the weakly connected case never has
   * to make, since there an excluded edge can only ever mean the first of these:
   * <ul>
   *   <li>an edge that <b>fails the tests</b> belongs to infrastructure this partition is not concerned with, e.g.
   *   a rail segment while road is being partitioned. Its vertex is shared with a network judged separately and
   *   must not be considered wholly contained here, exactly as in the weakly connected case</li>
   *   <li>an edge that <b>passes the tests but leads to another component</b> is part of this same network and is
   *   merely the boundary between two of its components. It is registered on neither subgraph and does not stop
   *   its vertices from being wholly contained, since it cannot outlive whichever of the two is removed</li>
   * </ul>
   * Without that distinction every component would retain the vertex its outgoing boundary edge attaches to, and
   * pruning would leave a stub vertex behind for each one.
   * <p>
   * Traversal follows individual edge segments rather than edges, which is what direction means here: a segment is
   * traversable when it passes the segment test and its parent edge passes the edge test. Strictness governs what
   * is registered, not what is reachable.
   * </p>
   * <p>
   * Every vertex of the reference graph appears as a key, so a caller may look up any vertex without a null check.
   * A vertex whose incident edges are all excluded maps to a subgraph holding nothing, which is the correct answer:
   * it belongs to no part of the network being partitioned.
   * </p>
   *
   * @param referenceGraph the parent graph the subgraphs are a subset of
   * @param testEdge when an edge tests positive it is eligible for inclusion
   * @param testEdgeSegment when an edge segment tests positive it is eligible for inclusion
   * @param strict see the inclusion rules on the weakly connected counterpart
   * @return the strongly connected subgraph each vertex belongs to
   * @param <V> type of vertex
   * @param <E> type of edge
   * @param <ES> type of edge segment
   */
  @SuppressWarnings("unchecked")
  public static <V extends DirectedVertex, E extends DirectedEdge, ES extends EdgeSegment>
  Map<V, UntypedDirectedSubGraph<V,E,ES>> identifyStronglyConnectedSubGraphs(
      UntypedDirectedGraph<V,E,ES> referenceGraph,
      Predicate<? super E> testEdge,
      Predicate<? super ES> testEdgeSegment,
      boolean strict) {

    /* a segment may be followed when it is eligible in its own right and its parent edge is too, so that an edge
     * ruled out at edge level cannot be traversed through one of its segments */
    Predicate<EdgeSegment> traversable = es ->
        testEdgeSegment.test((ES) es) && (es.getParent() == null || testEdge.test((E) es.getParent()));

    var components =
        StronglyConnectedComponents.execute(referenceGraph.getVertices(), traversable).getComponents();

    /* resolved up front rather than through the result's own lookup, so that the boundary test below is a plain
     * map read for both endpoints of every edge considered */
    Map<DirectedVertex, Integer> componentIndexByVertex = new HashMap<>();
    for (int index = 0; index < components.size(); ++index) {
      for (var vertex : components.get(index)) {
        componentIndexByVertex.put(vertex, index);
      }
    }

    var numVertices = UndirectedGraphUtils.getNumberOfVerticesRegisteredUnderIdToken(referenceGraph);
    var numEdges = UndirectedGraphUtils.getNumberOfEdgesRegisteredUnderIdToken(referenceGraph);
    var numEdgeSegments = getNumberOfEdgeSegmentsRegisteredUnderIdToken(referenceGraph);

    /* an edge belongs to the subgraph only when it stays inside the component it is considered from, which is the
     * one respect in which strongly connected identification differs from weakly connected identification */
    BiPredicate<E, DirectedVertex> withinSameComponent = (edge, vertex) -> {
      var adjacentVertex = edge.getVertexA().idEquals(vertex) ? edge.getVertexB() : edge.getVertexA();
      return adjacentVertex != null &&
          Objects.equals(componentIndexByVertex.get(vertex), componentIndexByVertex.get(adjacentVertex));
    };

    Map<V, UntypedDirectedSubGraph<V,E,ES>> subGraphByVertex = new HashMap<>();
    for (var component : components) {

      var subGraph = new UntypedDirectedSubGraphImpl<V,E,ES>(
          IdGroupingToken.collectGlobalToken(), (int) numVertices, (int) numEdges, (int) numEdgeSegments);

      for (var currVertex : component) {
        registerVertexAndItsEdges(
            subGraph, currVertex, testEdge, testEdgeSegment, strict, withinSameComponent, edge -> {});
        subGraphByVertex.put(currVertex, subGraph);
      }
    }
    return subGraphByVertex;
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
