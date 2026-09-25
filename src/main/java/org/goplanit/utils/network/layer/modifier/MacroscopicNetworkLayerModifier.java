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
   * Remove every registered intersection without member nodes, each through {@link #removeIntersection(Intersection)}
   * so its removal event fires. Intended after raw edits emptied a registered intersection; the lookups of the
   * intersections are rebuilt afterwards, since raw edits are not seen by them. Intersections emptied by removing nodes
   * through this modifier are already removed as part of that removal
   *
   * @return number of intersections removed
   */
  public abstract int removeIntersectionsWithoutMemberNodes();
}
