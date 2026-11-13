package org.goplanit.utils.network.layer.modifier.consolidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.goplanit.utils.id.IdMapperType;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.modifier.MacroscopicNetworkLayerModifier;

/**
 * Settings of collapsing spatial intersections onto one node for some modes, see
 * {@link MacroscopicNetworkLayerModifier#collapseIntersectionsToNonSpatialForModes}: the modes to collapse and the
 * nodes excluded from collapse, both empty by default. The intersections to collapse are given to the collapse itself
 *
 * @author markr
 */
public class IntersectionCollapserSettings {

  /** see {@link #setCollapsedModes(Collection)}, empty by default */
  private final Set<Mode> collapsedModes = new LinkedHashSet<>();

  /** see {@link #addNodeIdsExcludedFromCollapse(Collection, IdMapperType)}, empty by default */
  private final List<Pair<Set<String>, IdMapperType>> nodeIdsExcludedFromCollapse = new ArrayList<>();

  /**
   * Constructor, every value at its default
   */
  public IntersectionCollapserSettings() {
  }

  /**
   * Copy constructor
   *
   * @param other to copy
   */
  public IntersectionCollapserSettings(IntersectionCollapserSettings other) {
    this.collapsedModes.addAll(other.collapsedModes);
    other.nodeIdsExcludedFromCollapse.forEach(nodeIds -> this.nodeIdsExcludedFromCollapse.add(
        Pair.of(new LinkedHashSet<>(nodeIds.first()), nodeIds.second())));
  }

  /**
   * The modes to collapse onto one node per spatial intersection
   *
   * @return the modes, unmodifiable, empty for none
   */
  public Set<Mode> getCollapsedModes() {
    return Collections.unmodifiableSet(collapsedModes);
  }

  /**
   * Set the modes to collapse onto one node per spatial intersection, replacing those set before. They are modes of
   * the network the layer belongs to. With no modes, collapse changes nothing
   *
   * @param modes to collapse
   */
  public void setCollapsedModes(Collection<? extends Mode> modes) {
    collapsedModes.clear();
    collapsedModes.addAll(modes);
  }

  /**
   * The nodes excluded from collapse, as added, each group of node ids with the kind of id they are
   *
   * @return the node ids in the order added, unmodifiable, empty when none
   */
  public List<Pair<Set<String>, IdMapperType>> getNodeIdsExcludedFromCollapse() {
    return Collections.unmodifiableList(nodeIdsExcludedFromCollapse);
  }

  /**
   * Exclude nodes from collapse. A spatial intersection with one of these nodes as a member node is not collapsed at
   * all, whatever its other member nodes. A node that is not in the layer, or in no intersection to collapse, changes
   * nothing. Example: a junction whose footways are worth keeping in full while the others collapse pedestrians. Each
   * call adds to the nodes excluded before
   *
   * @param nodeIds the ids of the nodes to exclude from collapse
   * @param nodeIdType the kind of id the node ids are, for example the external id
   */
  public void addNodeIdsExcludedFromCollapse(Collection<String> nodeIds, IdMapperType nodeIdType) {
    if (!nodeIds.isEmpty()) {
      nodeIdsExcludedFromCollapse.add(Pair.of(new LinkedHashSet<>(nodeIds), nodeIdType));
    }
  }
}
