package org.goplanit.utils.graph.modifier;

import org.goplanit.utils.graph.directed.*;
import org.goplanit.utils.graph.modifier.event.DirectedGraphModifierEventProducer;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Modify directed graph elements .
 * 
 * @author markr
 *
 */
public interface DirectedGraphModifier<V extends DirectedVertex, E extends DirectedEdge, ES extends EdgeSegment>
    extends GraphModifier<V, E>, DirectedGraphModifierEventProducer{

  /**
   * remove any directed subgraphs below a given size from the graph if they exist and subsequently reorder the
   * internal ids if needed.
   *
   * @param belowSize         remove subgraphs below the given size
   * @param aboveSize         remove subgraphs above the given size (typically set to maximum value)
   * @param alwaysKeepLargest indicate if the largest of the subgraphs is always to be kept even if it does
   *                          not match the criteria
   * @param identifySubGraphForVertex function that given a starting vertex identifies the connected directed subgraph
   */
  public abstract void removeDanglingDirectedSubGraphs(
      Integer belowSize,
      Integer aboveSize,
      boolean alwaysKeepLargest,
      Function<DirectedVertex, ? extends UntypedDirectedSubGraph<DirectedVertex, DirectedEdge, EdgeSegment>>
          identifySubGraphForVertex);

  /**
   * Remove an edge segment by removing it from the graph and the edge it is connected to. Any registered events
   * for edge segment removal will be triggered. No attached vertices, edges, or banned turns will be removed
   *
   * @param edgeSegment to remove
   */
  public abstract void removeEdgeSegment(ES edgeSegment);

  /**
   * Remove an edge from the graph. When its edge segments are removed as well, this is done through
   * {@link #removeEdgeSegment(EdgeSegment)} so each fires its own removal event. Otherwise the edge segments are left
   * untouched, still registered and attached to the edge, for the caller to remove
   *
   * @param edge to remove
   * @param removeEdgeSegments when true remove its edge segments as well, when false leave them untouched
   */
  public abstract void removeEdge(E edge, boolean removeEdgeSegments);

  /**
   * Move the end at a vertex of each given edge to another vertex, in one call. Each edge keeps its segments, which then
   * start or end at the new vertex. The move fires one event once the edges have moved, naming them and both vertices,
   * so what refers to the vertex over several of them can follow them as a whole
   *
   * @param edges to move the end of, each with the current end vertex as one of its vertices
   * @param currentEndVertex the end vertex to move away from
   * @param newEndVertex the vertex to move the ends to
   */
  public abstract void moveEdgeEndsAtVertex(Collection<? extends E> edges, V currentEndVertex, V newEndVertex);

  /**
   * Remove a banned turn by removing it from the graph. Any registered events
   * for banned turn removal will be triggered.No attached vertices, edges, or segments will be removed
   *
   * @param bannedTurn to remove
   */
  public abstract void removeBannedTurn(BannedTurn bannedTurn);

  /**
   * Remove a directed subgraph
   *
   * @param subGraphToRemove to remove
   */
  public abstract void removeDirectedSubGraph(UntypedDirectedSubGraph<V, E, ES> subGraphToRemove);

}
