package org.goplanit.utils.network.layer.modifier.consolidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.goplanit.utils.graph.directed.BannedTurn;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicBannedTurn;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLink;
import org.goplanit.utils.network.layer.macroscopic.SpatialMovement;
import org.goplanit.utils.network.layer.modifier.MacroscopicNetworkLayerModifier;
import org.goplanit.utils.network.layer.modifier.ModeAccessCleanupModifierResult;

/**
 * What collapsing spatial intersections onto one node for some modes did, see
 * {@link MacroscopicNetworkLayerModifier#collapseIntersectionsToNonSpatialForModes}, as a value so the caller decides
 * what to log. Collapse fills it as it makes each change; the caller only reads it. Each entity is listed once.
 *
 * @author markr
 */
public class IntersectionCollapseModifierResult {

  /** links used only by collapsed modes, moved from a member node to the centre node of their intersection */
  private final List<MacroscopicLink> linksMovedToCentreNode = new ArrayList<>();

  /** links added from a centre node to a member node, each for one collapsed mode */
  private final List<MacroscopicLink> linksAddedToCentreNode = new ArrayList<>();

  /** bans removed because their segments no longer meet once a link was moved, or left with no modes once the
   * collapsed modes were taken off */
  private final List<BannedTurn> removedBannedTurns = new ArrayList<>();

  /** banned turns added on the turns collapse made, or extended, for the collapsed modes they hold, each once */
  private final Set<MacroscopicBannedTurn> bannedTurnsAddedOrExtendedForCollapsedModes = new LinkedHashSet<>();

  /** banned turns on internal segments that no longer name the collapsed modes, each once */
  private final Set<MacroscopicBannedTurn> bannedTurnsThatLostCollapsedModes = new LinkedHashSet<>();

  /** each movement and collapsed mode whose ban no new turn could hold without banning a movement that was allowed */
  private final List<Pair<SpatialMovement, Mode>> collapsedModeBansNotHeld = new ArrayList<>();

  /** the ids, as given, of the nodes excluded from collapse that no node of the layer has, each once, in the order
   * they were recorded */
  private final Set<String> excludedNodeIdsNotFound = new LinkedHashSet<>();

  /** what was removed for lack of mode access after collapsing, empty until collapse removes something */
  private ModeAccessCleanupModifierResult modeAccessCleanup =
      new ModeAccessCleanupModifierResult(List.of(), List.of(), List.of(), List.of(), List.of());

  /**
   * Constructor of an empty result, which collapse fills as it makes each change
   */
  public IntersectionCollapseModifierResult() {
  }

  /**
   * Record a link used only by collapsed modes, moved from a member node to the centre node of its intersection
   *
   * @param link moved
   */
  public void addLinkMovedToCentreNode(MacroscopicLink link) {
    linksMovedToCentreNode.add(link);
  }

  /**
   * Record a link added from a centre node to a member node for one collapsed mode
   *
   * @param link added
   */
  public void addLinkAddedToCentreNode(MacroscopicLink link) {
    linksAddedToCentreNode.add(link);
  }

  /**
   * Record a ban removed because its segments no longer meet, or because it was left with no modes
   *
   * @param bannedTurn removed
   */
  public void addRemovedBannedTurn(BannedTurn bannedTurn) {
    removedBannedTurns.add(bannedTurn);
  }

  /**
   * Record a banned turn added on a turn collapse made, or extended, for the collapsed modes it holds
   *
   * @param bannedTurn added or extended
   */
  public void addBannedTurnAddedOrExtendedForCollapsedModes(MacroscopicBannedTurn bannedTurn) {
    bannedTurnsAddedOrExtendedForCollapsedModes.add(bannedTurn);
  }

  /**
   * Record a banned turn on an internal segment that no longer names the collapsed modes
   *
   * @param bannedTurn that lost the collapsed modes
   */
  public void addBannedTurnThatLostCollapsedModes(MacroscopicBannedTurn bannedTurn) {
    bannedTurnsThatLostCollapsedModes.add(bannedTurn);
  }

  /**
   * Record a movement and collapsed mode whose ban no new turn could hold without banning a movement that was allowed
   *
   * @param movement whose ban was dropped
   * @param mode collapsed
   */
  public void addCollapsedModeBanNotHeld(SpatialMovement movement, Mode mode) {
    collapsedModeBansNotHeld.add(Pair.of(movement, mode));
  }

  /**
   * Record an id of a node excluded from collapse that no node of the layer has, each id once
   *
   * @param nodeId the id, as given
   */
  public void addExcludedNodeIdNotFound(String nodeId) {
    excludedNodeIdsNotFound.add(nodeId);
  }

  /**
   * Set what was removed for lack of mode access after collapsing
   *
   * @param modeAccessCleanup the mode access cleanup result
   */
  public void setModeAccessCleanup(ModeAccessCleanupModifierResult modeAccessCleanup) {
    this.modeAccessCleanup = modeAccessCleanup;
  }

  /**
   * Links used only by collapsed modes, moved from a member node to the centre node of their intersection
   *
   * @return moved links, unmodifiable
   */
  public List<MacroscopicLink> getLinksMovedToCentreNode() {
    return Collections.unmodifiableList(linksMovedToCentreNode);
  }

  /**
   * Links added from a centre node to a member node, each for one collapsed mode still allowed on a road kept at that
   * member node
   *
   * @return added links, unmodifiable
   */
  public List<MacroscopicLink> getLinksAddedToCentreNode() {
    return Collections.unmodifiableList(linksAddedToCentreNode);
  }

  /**
   * Bans removed because their segments no longer meet once a link was moved to a centre node, or because a banned
   * turn on an internal segment was left with no modes once the collapsed modes were taken off
   *
   * @return removed bans, unmodifiable
   */
  public List<BannedTurn> getRemovedBannedTurns() {
    return Collections.unmodifiableList(removedBannedTurns);
  }

  /**
   * Banned turns added on the turns collapse made, at the centre node or at a member node, or an existing banned turn
   * on the same segments that gained the collapsed modes it holds
   *
   * @return added or extended banned turns, unmodifiable
   */
  public List<MacroscopicBannedTurn> getBannedTurnsAddedOrExtendedForCollapsedModes() {
    return List.copyOf(bannedTurnsAddedOrExtendedForCollapsedModes);
  }

  /**
   * Banned turns on internal segments that no longer name the collapsed modes, since those modes no longer use the
   * internal segments. A banned turn left with no modes is removed instead, and listed with the removed bans
   *
   * @return banned turns that lost collapsed modes, unmodifiable
   */
  public List<MacroscopicBannedTurn> getBannedTurnsThatLostCollapsedModes() {
    return List.copyOf(bannedTurnsThatLostCollapsedModes);
  }

  /**
   * Each movement and collapsed mode whose ban no new turn could hold without also banning a movement that was
   * allowed, so the ban was dropped. This only happens for a movement with an arm that stays on its member node
   *
   * @return dropped bans as movement and mode, unmodifiable
   */
  public List<Pair<SpatialMovement, Mode>> getCollapsedModeBansNotHeld() {
    return Collections.unmodifiableList(collapsedModeBansNotHeld);
  }

  /**
   * The ids, as given, of the nodes excluded from collapse that no node of the layer has
   *
   * @return the ids, in the order they were recorded, unmodifiable
   */
  public List<String> getExcludedNodeIdsNotFound() {
    return List.copyOf(excludedNodeIdsNotFound);
  }

  /**
   * What was removed for lack of mode access after collapsing, such as the inner links no mode can use any more
   *
   * @return the mode access cleanup result
   */
  public ModeAccessCleanupModifierResult getModeAccessCleanup() {
    return modeAccessCleanup;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return String.format(
        "moved %d links to and added %d links to centre nodes, removed %d bans, added or extended %d and reduced %d " +
        "banned turns for collapsed modes, dropped %d bans of collapsed modes, did not find %d nodes excluded from " +
        "collapse",
        linksMovedToCentreNode.size(), linksAddedToCentreNode.size(), removedBannedTurns.size(),
        bannedTurnsAddedOrExtendedForCollapsedModes.size(), bannedTurnsThatLostCollapsedModes.size(),
        collapsedModeBansNotHeld.size(), excludedNodeIdsNotFound.size());
  }
}
