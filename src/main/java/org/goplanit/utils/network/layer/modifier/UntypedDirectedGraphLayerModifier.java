package org.goplanit.utils.network.layer.modifier;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.goplanit.utils.graph.directed.Connectivity;
import org.goplanit.utils.graph.directed.DirectedEdge;
import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.graph.modifier.event.GraphModifierEventProducer;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.network.layer.UntypedDirectedGraphLayer;
import org.goplanit.utils.graph.directed.BannedMovement;

import java.util.*;
import java.util.function.Predicate;

/**
 * Modifier with additional functionality related to modifications to layers derived from
 * {@link UntypedDirectedGraphLayer}. Since it wraps an untyped directed graph it exposes the event listener
 * functionality to the end user by implementing the GraphModifierEventProducer interface allowing users to register
 * listeners for the events fired when breaking links or removing subnetworks (subgraphs)
 *
 * @author markr
 */
public interface UntypedDirectedGraphLayerModifier<
        V extends DirectedVertex,
        E extends DirectedEdge,
        S extends EdgeSegment> extends TopologicalLayerModifier, GraphModifierEventProducer {

  /**
   * Remove any dangling subnetworks below a given size from the network if they exist and subsequently reorder
   * the internal ids if needed
   *
   * @param belowSize         remove subnetworks below the given size
   * @param aboveSize         remove subnetworks above the given size (typically set to maximum value)
   * @param alwaysKeepLargest when true the largest of the subnetworks is always kept, otherwise not
   * @param recreateManagedIds when true recreate managed id entities so they are contiguous again
   * @param testEdgeSegment only when both segments test positive on an edge, the edge tests positive for a vertex and
   *                        it is considered part of the subnetwork being considered. When always testing positive there
   *                        is effectively no restriction on the edge segments and all are considered
   * @see #removeDanglingSubnetworks(Integer, Integer, boolean, boolean, Predicate, Connectivity) applies
   *      {@link Connectivity#STRONG}, so infrastructure that cannot be both entered and left counts as a
   *      subnetwork of its own
   */
  public abstract void removeDanglingSubnetworks(
      final Integer belowSize,
      Integer aboveSize,
      boolean alwaysKeepLargest,
      boolean recreateManagedIds,
      Predicate<? super S> testEdgeSegment);

  /**
   * Remove any dangling subnetworks as per
   * {@link #removeDanglingSubnetworks(Integer, Integer, boolean, boolean, Predicate)}, with control over what
   * being part of the same subnetwork means.
   * <p>
   * Under {@link Connectivity#WEAK} a subnetwork is anything reachable while ignoring direction, which is the
   * established behaviour. Under {@link Connectivity#STRONG} it is what is mutually reachable while following
   * direction, so infrastructure that can be driven out of but never into is treated as a separate subnetwork and
   * becomes a candidate for removal in its own right.
   * </p>
   * <p>
   * Note that size thresholds lose much of their meaning under the strong notion, since it tends to yield a large
   * number of very small components, most of them a single vertex.
   * </p>
   *
   * @param belowSize         remove subnetworks below the given size
   * @param aboveSize         remove subnetworks above the given size (typically set to maximum value)
   * @param alwaysKeepLargest when true the largest of the subnetworks is always kept, otherwise not
   * @param recreateManagedIds when true recreate managed id entities so they are contiguous again
   * @param testEdgeSegment see {@link #removeDanglingSubnetworks(Integer, Integer, boolean, boolean, Predicate)}
   * @param connectivity what constitutes belonging to the same subnetwork
   */
  public abstract void removeDanglingSubnetworks(
      final Integer belowSize,
      Integer aboveSize,
      boolean alwaysKeepLargest,
      boolean recreateManagedIds,
      Predicate<? super S> testEdgeSegment,
      Connectivity connectivity);

  /**
   * Break the passed in links by inserting the passed in node in between. After completion the original links remain
   * as (NodeA,NodeToBreakAt), and new links as inserted for (NodeToBreakAt,NodeB).
   * Underlying link segments (if any) are also updated accordingly in the same manner.
   * 
   * @param linksToBreak       the links to break
   * @param nodeToBreakAt      the node to break at
   * @param crs                to use to recompute link lengths of broken links
   * @return the broken edges for each original edge's id
   */
  public abstract Map<Long, Pair<E,E>> breakAt(
      List<E> linksToBreak, V nodeToBreakAt, CoordinateReferenceSystem crs);

  /**
   * Break the passed in links by inserting the passed in node in between. After completion the original
   * links remain as (NodeA,NodeToBreakAt), and new links as inserted for (NodeToBreakAt,NodeB).
   * Underlying link segments (if any) are also updated accordingly in the same manner.
   * we pass in indexed movements to speed up the updating of the touched movements (if any). If any
   * banned movements exist on the broken edges layer, or many edges are to broken with successive calls,
   * this should be the go to, to optimize performance compared to equivalent method without this index. It is assumed
   * the passed on movements are the drop-in replacement for the layer's movements container
   *
   * @param linksToBreak  the links to break
   * @param nodeToBreakAt the node to break at
   * @param movementsByCentreVertex precompiled index for movements so they can be quickly updated
   * @param crs           to use to recompute link lengths of broken links
   * @return the broken links for each original link's internal id
   */
  public abstract Map<Long, Pair<E,E>> breakAt(
      List<E> linksToBreak,
      V nodeToBreakAt,
      Map<? extends V, List<BannedMovement>> movementsByCentreVertex,
      CoordinateReferenceSystem crs);

  /**
   * Recreate all managed id entities on the layer
   * <p>
   *   Should fire #RecreatedGraphEntitiesManagedIdsEvent and #RecreatedDirectedGraphEntitiesManagedIdsEvent after each
   *   relevant managedIds container (nodes, links) and (link segments) that has updated
   * </p>
   */
  public abstract void recreateManagedIdEntities();

  /**
   * Remove an edge from the layer and any edge segments in the process
   *
   * <p>
   *   Should fire #RemoveSubGraphEdgeEvent for the edge that is to be removed
   * </p>
   *
   * @param edge to remove
   */
  public void removeEdge(E edge);

  /**
   * Remove an edge segment by removing it from the graph and the edge it is connected to. Any registered events
   * for edge segment removal will be triggered.
   * <p>
   *   Should fire #RemoveSubGraphEdgeSegmentEvent for the edge segment that is to be removed
   * </p>
   *
   * @param edgeSegment to remove
   */
  public void removeEdgeSegment(S edgeSegment);

  /**
   * Remove a vertex from the layer. It is up to the caller to ensure nothing is still attached to it, since a
   * vertex outliving its edges is the expected case here rather than an error.
   *
   * @param vertex to remove
   */
  public void removeVertex(V vertex);
}
