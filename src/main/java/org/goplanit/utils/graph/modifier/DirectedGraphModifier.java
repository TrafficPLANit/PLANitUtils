package org.goplanit.utils.graph.modifier;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.goplanit.utils.geo.PlanitJtsCrsUtils;
import org.goplanit.utils.graph.directed.*;
import org.goplanit.utils.graph.modifier.event.DirectedGraphModifierEventProducer;
import org.goplanit.utils.misc.Pair;

import java.util.List;
import java.util.Map;
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
   * for edge segment removal will be triggered. No attached vertices, edges, or movements will be removed
   *
   * @param edgeSegment to remove
   */
  public abstract void removeEdgeSegment(ES edgeSegment);

  /**
   * Remove a movement by removing it from the graph. Any registered events
   * for movement removal will be triggered.No attached vertices, edges, or segments will be removed
   *
   * @param movement to remove
   */
  public abstract void removeMovement(BannedMovement  movement);

  /**
   * Remove a directed subgraph
   *
   * @param subGraphToRemove to remove
   */
  public abstract void removeDirectedSubGraph(UntypedDirectedSubGraph<V, E, ES> subGraphToRemove);

  /**
   * Identical to the {@code breakEdgeAt(DirectedVertex, Ex, PlanitJtsCrsUtils)} implementation except that we
   * pass in indexed movements to speed up the updating of the touched movements (if any). If any banned movements
   * exist on
   * the broken edge's layer this should be the go to optimise performance. It is assumed the passed on movements are
   * the drop-in replacement for the layer's movements container
   *
   * @param <Ex> edge type
   * @param edgeToBreak     edge to break
   * @param vertexToBreakAt the vertex to break at
   * @param movementsByCentreVertex precompiled index for movements so they can be quickly updated
   * @param geoUtils        required to update edge lengths
   * @return newly created edge due to breaking, null if not feasible
   */
  public abstract <Ex extends DirectedEdge> Ex breakEdgeAt(
      final V vertexToBreakAt,
      final Ex edgeToBreak,
      Map<? extends DirectedVertex, List<BannedMovement>> movementsByCentreVertex,
      final PlanitJtsCrsUtils geoUtils);

  /**
   * Identical to the {@code breakEdgesAt(List<Ex>, DirectedVertex, CoordinateReferenceSystem)} implementation except
   * that we pass in indexed movements to speed up the updating of the touched movements (if any). If any
   * banned movements exist on the broken edges layer this should be the go to optimise performance. It is assumed
   * the passed on movements are the drop-in replacement for the layer's movements container
   *
   * @param <Ex> edge type
   * @param edgesToBreak    the links to break
   * @param vertexToBreakAt the node to break at
   * @param movementsByCentreVertex precompiled index for movements so they can be quickly updated
   * @param crs required to update edge lengths
   * @return affectedEdges the list of all result edges of the breaking of links by their original link id
   */
  public abstract <Ex extends DirectedEdge> Map<Long, Pair<Ex, Ex>> breakEdgesAt(
      final List<Ex> edgesToBreak,
      final V vertexToBreakAt,
      Map<? extends DirectedVertex, List<BannedMovement>> movementsByCentreVertex,
      final CoordinateReferenceSystem crs);

}
