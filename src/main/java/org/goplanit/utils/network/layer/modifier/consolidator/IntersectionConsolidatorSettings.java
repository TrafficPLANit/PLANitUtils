package org.goplanit.utils.network.layer.modifier.consolidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.goplanit.utils.exceptions.ExceptionUtils;
import org.goplanit.utils.id.IdMapperType;
import org.goplanit.utils.id.IdMappingUtils;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.network.layer.modifier.MacroscopicNetworkLayerModifier;
import org.goplanit.utils.settings.UserOverride;
import org.goplanit.utils.settings.annotations.NoSettingsKey;

/**
 * Settings of intersection consolidation, see {@link MacroscopicNetworkLayerModifier}. Each value starts at its
 * default. The lists of nodes to join into one intersection, or to keep in separate intersections, override the
 * grouping consolidation chooses, and are empty by default
 * <p>
 * Consolidation chooses its grouping from the values of {@link IntersectionConsolidatorYamlKeyedSettings}; the lists
 * correct that grouping at a junction where it is wrong, by the ids the nodes have
 * </p>
 *
 * @author markr
 */
public class IntersectionConsolidatorSettings extends IntersectionConsolidatorYamlKeyedSettings {

  /**
   * Whether the node ids name at most one node, so there is nothing to join or keep apart. For example, "12", "12"
   * names one node
   *
   * @param nodeIds to check
   * @return true when they hold fewer than two different ids
   */
  private static boolean isSingleNodeOrNone(Collection<String> nodeIds) {
    return nodeIds.stream().distinct().count() < 2;
  }

  /** see {@link #addNodeIdsToJoinIntoOneIntersection(Collection, IdMapperType, String)}, empty by default */
  private final List<UserOverride<Pair<Set<String>, IdMapperType>>> nodeIdsToJoinIntoOneIntersection =
      new ArrayList<>();

  /** see {@link #addNodeIdsToKeepInSeparateIntersections(Collection, IdMapperType, String)}, empty by default */
  private final List<UserOverride<Pair<Set<String>, IdMapperType>>> nodeIdsToKeepInSeparateIntersections =
      new ArrayList<>();

  /**
   * Constructor, every value at its default
   */
  public IntersectionConsolidatorSettings() {
  }

  /**
   * Copy constructor
   *
   * @param other to copy
   */
  public IntersectionConsolidatorSettings(IntersectionConsolidatorSettings other) {
    super(other);
    other.nodeIdsToJoinIntoOneIntersection.forEach(
        list -> this.nodeIdsToJoinIntoOneIntersection.add(list.map(IdMappingUtils::copyIdList)));
    other.nodeIdsToKeepInSeparateIntersections.forEach(
        list -> this.nodeIdsToKeepInSeparateIntersections.add(list.map(IdMappingUtils::copyIdList)));
  }

  /**
   * The lists of nodes to join into one intersection, each with the kind of id its node ids are and its reason
   *
   * @return the lists in the order added, unmodifiable, empty when none
   */
  public List<UserOverride<Pair<Set<String>, IdMapperType>>> getNodeIdsToJoinIntoOneIntersection() {
    return Collections.unmodifiableList(nodeIdsToJoinIntoOneIntersection);
  }

  /**
   * Same as {@link #addNodeIdsToJoinIntoOneIntersection(Collection, IdMapperType, String)}, without a reason
   *
   * @param nodeIds the ids of the nodes to join
   * @param nodeIdType the kind of id the node ids are, for example the external id
   * @throws IllegalArgumentException naming the node ids, storing nothing, when there are fewer than two different ones
   */
  @NoSettingsKey(reason = "a converter adds the groups by the ids it gives its nodes, through its own settings")
  public void addNodeIdsToJoinIntoOneIntersection(Collection<String> nodeIds, IdMapperType nodeIdType) {
    addNodeIdsToJoinIntoOneIntersection(nodeIds, nodeIdType, null);
  }

  /**
   * Add a list of nodes to join into one intersection. This overrides the grouping consolidation chooses. Once the
   * crossings have joined, the intersections holding the listed nodes are joined into one, with the nodes on the
   * shortest roads between them within the joining reach. So any node of an intersection will do, and two nodes of
   * two larger intersections join those intersections as a whole. The steps from the pull-ins to the crossings then
   * run again for it, with its larger radius, before its approaches and movements are worked out. A crossing that
   * joined one of the intersections before stays in it. A crossing intersection named in a list joins even where the
   * crossing rules would not join it. A node in no intersection then has no effect. A list is not joined when it
   * would put nodes listed to keep in separate intersections into one, see
   * {@link #addNodeIdsToKeepInSeparateIntersections(Collection, IdMapperType)}. Lists that share an intersection join
   * into one, in the order added. A list needs at least two different node ids: a shorter one is refused. Example:
   * two parts of a large junction that consolidation left as two intersections. Each call adds one list
   *
   * @param nodeIds the ids of the nodes to join
   * @param nodeIdType the kind of id the node ids are, for example the external id
   * @param reason why the nodes are listed, null for none
   * @throws IllegalArgumentException naming the node ids, storing nothing, when there are fewer than two different ones
   */
  @NoSettingsKey(reason = "a converter adds the groups by the ids it gives its nodes, through its own settings")
  public void addNodeIdsToJoinIntoOneIntersection(Collection<String> nodeIds, IdMapperType nodeIdType, String reason) {
    ExceptionUtils.throwIllegalArgumentIf(isSingleNodeOrNone(nodeIds),
        "Refused a list of nodes to join into one intersection with fewer than two node ids: %s", nodeIds);
    nodeIdsToJoinIntoOneIntersection.add(UserOverride.of(IdMappingUtils.createIdList(nodeIds, nodeIdType), reason));
  }

  /**
   * The lists of nodes to keep in separate intersections, each with the kind of id its node ids are and its reason
   *
   * @return the lists in the order added, unmodifiable, empty when none
   */
  public List<UserOverride<Pair<Set<String>, IdMapperType>>> getNodeIdsToKeepInSeparateIntersections() {
    return Collections.unmodifiableList(nodeIdsToKeepInSeparateIntersections);
  }

  /**
   * Same as {@link #addNodeIdsToKeepInSeparateIntersections(Collection, IdMapperType, String)}, without a reason
   *
   * @param nodeIds the ids of the nodes to keep in separate intersections
   * @param nodeIdType the kind of id the node ids are, for example the external id
   * @throws IllegalArgumentException naming the node ids, storing nothing, when there are fewer than two different ones
   */
  @NoSettingsKey(reason = "a converter adds the groups by the ids it gives its nodes, through its own settings")
  public void addNodeIdsToKeepInSeparateIntersections(Collection<String> nodeIds, IdMapperType nodeIdType) {
    addNodeIdsToKeepInSeparateIntersections(nodeIds, nodeIdType, null);
  }

  /**
   * Add a list of nodes to keep in separate intersections. No grouping step puts two nodes of one list into one
   * intersection. So the subdivision of an intersection cluster that would do so is not allowed and the next best one
   * is chosen, a junction that would do so is not pulled in, a crossing joins the next nearest junction instead, a node
   * that would do so does not join, and a list to join that would do so is not joined. Nodes of one list that are in
   * one intersection before consolidation cannot be split, so they count as one. A list left with nothing to keep
   * apart has no effect. A list needs at least two different node ids: a shorter one is refused. Example: two
   * signalised junctions 30 m apart that run as separate controllers. Each call adds one list
   *
   * @param nodeIds the ids of the nodes to keep in separate intersections
   * @param nodeIdType the kind of id the node ids are, for example the external id
   * @param reason why the nodes are listed, null for none
   * @throws IllegalArgumentException naming the node ids, storing nothing, when there are fewer than two different ones
   */
  @NoSettingsKey(reason = "a converter adds the groups by the ids it gives its nodes, through its own settings")
  public void addNodeIdsToKeepInSeparateIntersections(Collection<String> nodeIds, IdMapperType nodeIdType,
      String reason) {
    ExceptionUtils.throwIllegalArgumentIf(isSingleNodeOrNone(nodeIds),
        "Refused a list of nodes to keep in separate intersections with fewer than two node ids: %s", nodeIds);
    nodeIdsToKeepInSeparateIntersections.add(UserOverride.of(IdMappingUtils.createIdList(nodeIds, nodeIdType), reason));
  }
}
