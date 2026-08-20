package org.goplanit.utils.graph;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.id.IdGenerator;
import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.misc.Pair;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Utilities for (undirected) graphs
 *
 * @author markr
 */
public class UndirectedGraphUtils {


  /**
   * Number of vertices registered under id token used by reference graph
   * @param referenceGraph to use
   * @return num vertices
   * @param <V> type of vertex
   * @param <E> type of edge
   */
  public static <V extends Vertex, E extends Edge> long getNumberOfVerticesRegisteredUnderIdToken(
      UntypedGraph<V,E> referenceGraph){
    return IdGenerator.getLatestIdForToken(
        referenceGraph.getVertices().getFactory().getIdGroupingToken(), Vertex.VERTEX_ID_CLASS);
  }

  /**
   * Number of edges registered under id token used by reference graph
   * @param referenceGraph to use
   * @return num edges
   * @param <V> type of vertex
   * @param <E> type of edge
   */
  public static <V extends Vertex, E extends Edge> long getNumberOfEdgesRegisteredUnderIdToken(
      UntypedGraph<V,E> referenceGraph){
    return IdGenerator.getLatestIdForToken(
        referenceGraph.getEdges().getFactory().getIdGroupingToken(), Edge.EDGE_ID_CLASS);
  }

  /**
   * Helper function for subgraph identification (deliberately NOT recursive to avoid stack overflow on
   * large networks)
   *
   * Traversal always follows connectivity across edges that test positive, so the full connected component is
   * explored regardless of strictness. Strictness only governs what ends up registered on the returned subgraph.
   *
   * @param referenceGraph the parent graph the subgraph is a subset of
   * @param referenceVertex to process
   * @param testEdge when an edge tests positive it is included in the subgraph
   * @param strict when true a vertex is only included when every one of its edges is included, i.e. the vertex lies
   *               wholly within the subgraph. When false a vertex is included when any of its edges is included.
   *               Use strict when the result drives removal, so entities still referenced by the retained graph
   *               are not part of it; use lenient when the result is used to identify connectivity.
   * @return subgraph of all vertices and edges in the subnetwork connected to passed in reference vertex
   */
  public static <V extends Vertex, E extends Edge> UntypedSubGraph<V,E> identifySubGraphForVertex(
      UntypedGraph<V,E> referenceGraph,
      V referenceVertex,
      Predicate<? super E> testEdge,
      boolean strict) {
    PlanItRunTimeException.throwIfNull(referenceVertex, "provided reference vertex is null " +
        "when identifying its subnetwork, this is not allowed");


    // rely on max id instead of container size, since vertices can be shared between graphs and not owned by the
    // parent graph
    var numVertices = getNumberOfVerticesRegisteredUnderIdToken(referenceGraph);
    var numEdges = getNumberOfEdgesRegisteredUnderIdToken(referenceGraph);

    var subGraph = new UntypedSubGraphImpl<V,E>(
        IdGroupingToken.collectGlobalToken(), (int) numVertices, (int) numEdges);

    /* track visited separately from subgraph membership: under strict rules a vertex may be explored but not
     * registered, in which case subgraph membership would never mark it as seen and it would be revisited forever */
    final BitSet visitedVertices = new BitSet((int) numVertices);

    Deque<Vertex> verticesToExplore = new ArrayDeque<>();
    verticesToExplore.add(referenceVertex);
    visitedVertices.set((int) referenceVertex.getId());

    while (!verticesToExplore.isEmpty()) {
      Vertex currVertex = verticesToExplore.pop();

      Collection<E> edgesOfCurrVertex = (Collection<E>) currVertex.getEdges();
      boolean allEdgesIncluded = true;
      for (E currEdge : edgesOfCurrVertex) {

        if(!testEdge.test(currEdge)){
          /* vertex is attached to something outside the subgraph, so not wholly contained by it */
          allEdgesIncluded = false;
          continue;
        }
        subGraph.addEdge(currEdge);

        /* both extremities are candidates; they are tested independently since an already visited vertex A must
         * not stop vertex B from being explored */
        exploreIfUnvisited(currEdge.getVertexA(), currVertex, visitedVertices, verticesToExplore);
        exploreIfUnvisited(currEdge.getVertexB(), currVertex, visitedVertices, verticesToExplore);
      }

      /* lenient: reaching a vertex means an incident edge was included, so it qualifies. A vertex without any
       * included edges (an isolated reference vertex) still forms a subgraph of size one.
       * strict: every incident edge must be included, which holds vacuously for an isolated vertex */
      if(!strict || allEdgesIncluded){
        subGraph.addVertex((V)currVertex);
      }
    }
    return subGraph;
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
      Vertex candidate, Vertex currVertex, BitSet visitedVertices, Deque<Vertex> verticesToExplore){
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
