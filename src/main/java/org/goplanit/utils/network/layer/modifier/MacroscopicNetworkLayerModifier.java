package org.goplanit.utils.network.layer.modifier;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.goplanit.utils.graph.directed.BannedTurn;
import org.goplanit.utils.modifier.LoggableModifier;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLink;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;
import org.goplanit.utils.network.layer.macroscopic.SpatialMovement;
import org.goplanit.utils.network.layer.macroscopic.intersection.Intersection;
import org.goplanit.utils.network.layer.macroscopic.intersection.SpatialIntersection;
import org.goplanit.utils.network.layer.macroscopic.intersection.modifier.event.IntersectionModifierEventProducer;
import org.goplanit.utils.network.layer.modifier.consolidator.IntersectionCollapseModifierResult;
import org.goplanit.utils.network.layer.modifier.consolidator.IntersectionConsolidationModifierResult;
import org.goplanit.utils.network.layer.modifier.consolidator.IntersectionConsolidatorSettings;
import org.goplanit.utils.network.layer.physical.Node;

/**
 * Modifier for macroscopic network layers. Besides the graph related changes of {@link UntypedDirectedGraphLayerModifier}
 * it keeps the layer's intersections and banned turns consistent with every change it makes, fires events about
 * intersections, and states what it changed unless the caller switches that off.
 *
 * @author markr
 */
public interface MacroscopicNetworkLayerModifier extends
    UntypedDirectedGraphLayerModifier<Node, MacroscopicLink, MacroscopicLinkSegment>,
    IntersectionModifierEventProducer, LoggableModifier {

  /**
   * Remove an intersection from the layer, firing a remove intersection event. The movements of a spatial
   * intersection are removed first, each through {@link #removeSpatialMovement(SpatialMovement)}
   *
   * @param intersection to remove
   * @return true when removed, false when not registered on the layer
   */
  public abstract boolean removeIntersection(Intersection intersection);

  /**
   * Remove the given intersections from the layer, each through {@link #removeIntersection(Intersection)} so its
   * removal event fires
   *
   * @param intersections to remove
   * @return number of intersections removed, those not registered on the layer excepted
   */
  public abstract int removeIntersections(Collection<? extends Intersection> intersections);

  /**
   * Remove every registered intersection that is incomplete in one of the selected ways, each through
   * {@link #removeIntersection(Intersection)} so its removal event fires. The lookups of the intersections are rebuilt
   * afterwards, since raw edits that left an intersection incomplete are not seen by them
   *
   * @param withoutMemberNodes when true, remove intersections without member nodes, e.g. after raw edits emptied them.
   *                           Intersections emptied by removing nodes through this modifier are already removed as part
   *                           of that removal
   * @param withoutApproaches when true, remove intersections without approaches, which control no traffic, e.g. once
   *                          the segments that were their approaches are removed because no mode can use them any more
   * @return number of intersections removed
   */
  public abstract int removeIncompleteIntersections(boolean withoutMemberNodes, boolean withoutApproaches);

  /**
   * Remove a spatial movement from the layer, and from its intersection when it has one, firing a remove spatial
   * movement event. Removing an intersection, or the from or to segment of a movement, removes the movement this way
   *
   * @param spatialMovement to remove
   * @return true when removed, false when not registered on the layer
   */
  public abstract boolean removeSpatialMovement(SpatialMovement spatialMovement);

  /**
   * Remove what no mode can use any more: link segments granting no mode access, links left without segments, nodes
   * left without edges, intersections left without approaches, and link segment types granting no mode access. The
   * removed entities are stated by the result rather than logged one by one.
   * <p>
   * Meant to run once after every mode has had its access restricted to the subnetworks worth keeping, since only then
   * is it settled what nothing can use
   * </p>
   *
   * @return what was removed
   */
  public abstract ModeAccessCleanupModifierResult removeInfrastructureWithoutModeAccess();

  /**
   * Move one end node of a link to another node. The link keeps its id, external id, segments and mode access. Its
   * geometry keeps every point and runs on in a straight line to the new node, and its length follows; a link without
   * geometry is taken to run straight between its nodes. Approaches and
   * exits on the link now end or start at the new node. Bans whose segments no longer meet are removed, each with its
   * removal event
   *
   * @param link to move an end node of
   * @param currentEndNode the end node to move, node A or node B of the link
   * @param newEndNode the node to move it to
   * @param crs of the layer's geometry, to measure the new length in
   * @return the bans removed because their segments no longer meet
   */
  public abstract List<BannedTurn> moveLinkEndNode(
      MacroscopicLink link, Node currentEndNode, Node newEndNode, CoordinateReferenceSystem crs);

  /**
   * Consolidate the intersections of the layer: join the parsed intersections of one junction into one intersection,
   * make internal the links reached only through it, let crossings join their junction, count missing approaches, and
   * store the movements of each spatial intersection. Every change is made through this modifier, and every entity it
   * removes is removed with a removal event.
   * <p>
   * A parsed intersection is a registered intersection with the kind junction. Parsed intersections within the maximum
   * internal link length of each other, along the road in either direction, form an intersection cluster. Every way to
   * divide a cluster into cluster subsets is scored on how well the traffic entering each cluster subset can leave it
   * in every direction the cluster offers, and each cluster subset of the best subdivision becomes one intersection
   * </p>
   *
   * @param crs of the layer's geometry, to measure directions and distances in
   * @param settings the reach, scoring values and warning lengths to use
   * @param hasVehicleSignal true for a link segment with a vehicle signal standing on it, which the layer does not
   *                         record. It is asked only for segments that become internal: one for which it is true
   *                         becomes an internal segment with a signal. Pass {@code segment -> false} when the positions
   *                         of signals are not known
   * @return what was done
   */
  public abstract IntersectionConsolidationModifierResult consolidateIntersections(
      CoordinateReferenceSystem crs,
      IntersectionConsolidatorSettings settings,
      Predicate<MacroscopicLinkSegment> hasVehicleSignal);

  /**
   * Consolidate the intersections of the layer with the default settings, and no vehicle signals known to stand on
   * link segments, see
   * {@link #consolidateIntersections(CoordinateReferenceSystem, IntersectionConsolidatorSettings, Predicate)}
   *
   * @param crs of the layer's geometry, to measure directions and distances in
   * @return what was done
   */
  public default IntersectionConsolidationModifierResult consolidateIntersections(CoordinateReferenceSystem crs) {
    return consolidateIntersections(crs, new IntersectionConsolidatorSettings(), segment -> false);
  }

  /**
   * Collapse spatial intersections onto one node each for the given modes, so for those modes each becomes in effect a
   * non-spatial intersection on its centre node:
   * <ul>
   * <li>a centre node is added at the centroid of its member nodes, as a member node;</li>
   * <li>its internal segments lose access for the modes;</li>
   * <li>links leaving it that only those modes use move their member node end to the centre node;</li>
   * <li>for each of the modes, a link joins the centre node to each member node where a kept road still allows the
   * mode, taking that road's link segment type, or a modified copy of it allowing only the mode;</li>
   * <li>the bans of the modes, worked out from the stored movements, move onto the new turns where they hold exactly;
   * </li>
   * <li>what no mode can use any more is removed, and a spatial intersection left with one member node becomes a
   * non-spatial intersection on it;</li>
   * <li>the banned turns on the internal segments left no longer name the modes.</li>
   * </ul>
   * The movements stay as they are. Every change is made through this modifier, and every entity it removes is removed
   * with a removal event. Nothing is done when no intersection or no mode is given.
   *
   * @param intersections the spatial intersections to collapse, each registered on the layer, else an exception is
   *                      thrown before anything changes
   * @param modes to collapse onto the centre nodes
   * @param crs of the layer's geometry, to measure distances in
   * @return what was done
   */
  public abstract IntersectionCollapseModifierResult collapseIntersectionsToNonSpatialForModes(
      Collection<? extends SpatialIntersection> intersections, Collection<Mode> modes, CoordinateReferenceSystem crs);

  /**
   * Collapse one spatial intersection onto one node for the given modes, see
   * {@link #collapseIntersectionsToNonSpatialForModes(Collection, Collection, CoordinateReferenceSystem)}. Collapsing
   * several at once is preferred, since what no mode can use any more is removed once for all of them
   *
   * @param intersection the spatial intersection to collapse
   * @param modes to collapse onto its centre node
   * @param crs of the layer's geometry, to measure distances in
   * @return what was done
   */
  public default IntersectionCollapseModifierResult collapseIntersectionToNonSpatialForModes(
      SpatialIntersection intersection, Collection<Mode> modes, CoordinateReferenceSystem crs) {
    return collapseIntersectionsToNonSpatialForModes(List.of(intersection), modes, crs);
  }
}
