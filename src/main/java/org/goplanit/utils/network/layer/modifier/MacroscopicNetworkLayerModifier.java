package org.goplanit.utils.network.layer.modifier;

import java.util.Collection;

import org.goplanit.utils.modifier.LoggableModifier;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLink;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;
import org.goplanit.utils.network.layer.macroscopic.intersection.Intersection;
import org.goplanit.utils.network.layer.macroscopic.intersection.modifier.event.IntersectionModifierEventProducer;
import org.goplanit.utils.network.layer.physical.Node;

/**
 * Modifier for macroscopic network layers. Besides the graph related changes of {@link UntypedDirectedGraphLayerModifier}
 * it keeps the layer's intersections and banned movements consistent with every change it makes, fires events about
 * intersections, and states what it changed unless the caller switches that off.
 *
 * @author markr
 */
public interface MacroscopicNetworkLayerModifier extends
    UntypedDirectedGraphLayerModifier<Node, MacroscopicLink, MacroscopicLinkSegment>,
    IntersectionModifierEventProducer, LoggableModifier {

  /**
   * Remove an intersection from the layer, firing a remove intersection event
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
}
