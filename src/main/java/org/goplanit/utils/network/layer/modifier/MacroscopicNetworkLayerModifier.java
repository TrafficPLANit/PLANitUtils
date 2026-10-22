package org.goplanit.utils.network.layer.modifier;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.goplanit.utils.graph.directed.BannedTurn;
import org.goplanit.utils.modifier.LoggableModifier;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLink;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;
import org.goplanit.utils.network.layer.macroscopic.SpatialMovement;
import org.goplanit.utils.network.layer.macroscopic.intersection.Intersection;
import org.goplanit.utils.network.layer.macroscopic.intersection.SpatialIntersection;
import org.goplanit.utils.network.layer.macroscopic.intersection.modifier.event.IntersectionModifierEventProducer;
import org.goplanit.utils.network.layer.modifier.consolidator.IntersectionCollapseModifierResult;
import org.goplanit.utils.network.layer.modifier.consolidator.IntersectionCollapserSettings;
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
   * Consolidate the intersections of the layer. In order, it:
   * <ul>
   * <li>joins the parsed intersections of one junction into one intersection, and makes internal the links reached
   * only through it;</li>
   * <li>pulls in the junctions within its radius;</li>
   * <li>lets crossings join their junction;</li>
   * <li>joins the nodes it encloses;</li>
   * <li>pulls in the junction that the road of a would-be approach without a signal leads back to;</li>
   * <li>makes internal the links now reached only through each intersection that grew by pulling in a junction, and
   * joins the nodes it now encloses;</li>
   * <li>makes each road entering a signalised junction that is still not an approach an approach without a signal of
   * its own;</li>
   * <li>stores the movements of each spatial intersection.</li>
   * </ul>
   * Every change is made through this modifier, and every entity it removes is removed with a removal event.
   * <p>
   * The lists of nodes in the settings override the grouping. No step puts nodes of one list to keep apart into one
   * intersection. Once the crossings have joined, the intersections holding the nodes of one list to join are joined
   * into one, and the steps from the pull-ins to the crossings run again for it, before the roads entering become
   * approaches and the movements are stored.
   * </p>
   * <p>
   * A parsed intersection is a registered intersection with the kind junction. Parsed intersections within the joining
   * reach of each other, along the road in either direction, form an intersection cluster. Ways to
   * divide a cluster into cluster subsets are scored on how well the traffic entering each cluster subset can leave it
   * in every direction the cluster offers: every way for a small cluster, a step by step search for a larger one. Each
   * cluster subset of the best subdivision becomes one intersection.
   * </p>
   * <p>
   * A segment that becomes internal becomes an internal segment with a signal when it has a signal of its own, or
   * when it enters a signalised crossing on a link no motorised road mode uses, such as a footway or cycleway, which
   * has a light of its own there. A road into a crossing does not, nor does a segment into a junction whose signal
   * stands on its node: a junction often has such a signal on every one of its nodes, so it would count one real
   * signal several times.
   * </p>
   *
   * @param crs of the layer's geometry, to measure directions and distances in
   * @param settings the reach, scoring values and warning lengths to use
   * @param hasSignalOfItsOwn true for a link segment with a signal of its own, which the layer does not record: a
   *                          vehicle signal standing on it, or a light for the footway or cycleway it is on where no
   *                          road or rail passes, so no intersection holds it. It is asked for segments that become
   *                          internal, to give them a signal, and for road segments into a junction, to tell whether a
   *                          node lies in front of the junction. Pass {@code segment -> false} when the positions of
   *                          signals are not known
   * @return what was done
   */
  public abstract IntersectionConsolidationModifierResult consolidateIntersections(
      CoordinateReferenceSystem crs,
      IntersectionConsolidatorSettings settings,
      Predicate<MacroscopicLinkSegment> hasSignalOfItsOwn);

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
   * Collapse spatial intersections onto one node each for the modes of the settings, so for those modes each becomes
   * in effect a non-spatial intersection on its centre node:
   * <ul>
   * <li>a centre node is added at the centroid of its member nodes, as a member node;</li>
   * <li>its internal segments lose access for the modes;</li>
   * <li>links leaving it that only those modes use move their member node end to the centre node;</li>
   * <li>for each of the modes, a link joins the centre node to each member node where a kept road still allows the
   * mode, taking that road's link segment type, or a modified copy of it allowing only the mode;</li>
   * <li>the modes are banned on the new turns so each can make only what it could make while the intersection was
   * spatial, worked out from the stored movements and, for a segment entering it that is not an approach, from what
   * that segment could reach;</li>
   * <li>what no mode can use any more is removed, and a spatial intersection left with one member node becomes a
   * non-spatial intersection on it;</li>
   * <li>the banned turns on the internal segments left no longer name the modes.</li>
   * </ul>
   * The movements stay as they are. Every change is made through this modifier, and every entity it removes is removed
   * with a removal event. Nothing is done when no intersection or no mode is given.
   * <p>
   * A spatial intersection with a member node the settings exclude from collapse is left as it is, whatever its other
   * member nodes. Each node excluded is listed in the result with what that did.
   * </p>
   *
   * @param intersections the spatial intersections to collapse, each registered on the layer, else an exception is
   *                      thrown before anything changes
   * @param settings the modes to collapse onto the centre nodes, and the nodes excluded from collapse
   * @param crs of the layer's geometry, to measure distances in
   * @return what was done
   */
  public abstract IntersectionCollapseModifierResult collapseIntersectionsToNonSpatialForModes(
      Collection<? extends SpatialIntersection> intersections, IntersectionCollapserSettings settings,
      CoordinateReferenceSystem crs);

  /**
   * Collapse one spatial intersection onto one node for the modes of the settings, see
   * {@link #collapseIntersectionsToNonSpatialForModes(Collection, IntersectionCollapserSettings,
   * CoordinateReferenceSystem)}. Collapsing several at once is preferred, since what no mode can use any more is
   * removed once for all of them
   *
   * @param intersection the spatial intersection to collapse
   * @param settings the modes to collapse onto its centre node, and the nodes excluded from collapse
   * @param crs of the layer's geometry, to measure distances in
   * @return what was done
   */
  public default IntersectionCollapseModifierResult collapseIntersectionToNonSpatialForModes(
      SpatialIntersection intersection, IntersectionCollapserSettings settings, CoordinateReferenceSystem crs) {
    return collapseIntersectionsToNonSpatialForModes(List.of(intersection), settings, crs);
  }
}
