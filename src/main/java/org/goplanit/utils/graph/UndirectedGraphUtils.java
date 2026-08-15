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
   * Helper function for subgraph identification (deliberately NOT recursive to avoid stack overflow on
   * large networks)
   *
   * @param referenceVertex to process
   * @param testEdge when any edge tests positive, the vertex is considered part of the subnetwork
   * @return all vertices in the subnetwork connected to passed in reference vertex
   */
  public static <V extends Vertex, E extends Edge> UntypedSubGraph<V,E> identifySubGraphForVertex(
      UntypedGraph<V,E> referenceGraph,
      Vertex referenceVertex,
      Predicate<? super Edge> testEdge) {
    PlanItRunTimeException.throwIfNull(referenceVertex, "provided reference vertex is null " +
        "when identifying its subnetwork, this is not allowed");


    // rely on max id instead of container size, since vertices can be shared between graphs and not owned by the
    // parent graph
    var numVertices = IdGenerator.getLatestIdForToken(
        referenceGraph.getVertices().getFactory().getIdGroupingToken(), Vertex.VERTEX_ID_CLASS);
    var numEdges = IdGenerator.getLatestIdForToken(
        referenceGraph.getEdges().getFactory().getIdGroupingToken(), Edge.EDGE_ID_CLASS);

    var subGraph = new UntypedSubGraphImpl<V,E>(
        IdGroupingToken.collectGlobalToken(), (int) numVertices, (int) numEdges);

    Set<Vertex> verticesToExplore = new HashSet<>();
    verticesToExplore.add(referenceVertex);
    Iterator<Vertex> vertexIter = verticesToExplore.iterator();
    while (vertexIter.hasNext()) {
      /* collect and remove since it is processed */
      Vertex currVertex = vertexIter.next();
      vertexIter.remove();

      /* add newly found vertices to explore, and add then to final subnetwork list as well */
      Collection<E> edgesOfCurrVertex = (Collection<E>) currVertex.getEdges();
      for (E currEdge : edgesOfCurrVertex) {

        // if any edge is acceptable, then proceed
        if(!testEdge.test(currEdge)){
          continue;
        }
        subGraph.addEdge(currEdge);

        if (currEdge.getVertexA() != null && currEdge.getVertexA().getId() != currVertex.getId() &&
            !subGraph.containsVertex((V)currEdge.getVertexA())) {
          subGraph.addVertex((V)currEdge.getVertexA());
          verticesToExplore.add(currEdge.getVertexA());
        } else if (currEdge.getVertexB() != null && currEdge.getVertexB().getId() != currVertex.getId() &&
            !subGraph.containsVertex((V)currEdge.getVertexB())) {
          subGraph.addVertex((V)currEdge.getVertexB());
          verticesToExplore.add((V)currEdge.getVertexB());
        }
      }
      /* update iterator */
      vertexIter = verticesToExplore.iterator();
    }
    return subGraph;
  }


}
