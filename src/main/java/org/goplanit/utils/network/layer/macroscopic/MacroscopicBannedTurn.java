package org.goplanit.utils.network.layer.macroscopic;

import org.goplanit.utils.graph.directed.BannedTurn;
import org.goplanit.utils.mode.Mode;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A banned turn on a macroscopic layer: from one link segment onto the next at a node, banned for the modes it names.
 * A banned turn that names no modes is banned for every mode. The graph-level {@link BannedTurn} has no modes, since
 * modes belong to the layer; every banned turn a macroscopic layer registers is of this kind.
 *
 * @author markr
 */
public interface MacroscopicBannedTurn extends BannedTurn {

  /**
   * The segment the turn starts from, as a link segment
   *
   * @return from link segment, null when not set
   */
  public default MacroscopicLinkSegment getLinkSegmentFrom() {
    return (MacroscopicLinkSegment) getSegmentFrom();
  }

  /**
   * The segment the turn goes onto, as a link segment
   *
   * @return to link segment, null when not set
   */
  public default MacroscopicLinkSegment getLinkSegmentTo() {
    return (MacroscopicLinkSegment) getSegmentTo();
  }

  /**
   * Collect the modes this turn is banned for, when it names them. A turn that names no modes is banned for every
   * mode
   *
   * @return the modes named, unmodifiable, empty when the turn is banned for every mode
   */
  public abstract Set<Mode> getExplicitlyBannedModes();

  /**
   * Verify if the turn names the modes it is banned for, rather than being banned for every mode
   *
   * @return true when it names modes, false when it is banned for every mode
   */
  public default boolean hasExplicitlyBannedModes() {
    return !getExplicitlyBannedModes().isEmpty();
  }

  /**
   * Name a mode the turn is banned for. A turn that names modes is banned for those modes only
   *
   * @param mode to name
   * @return true when added, false when already named
   */
  public abstract boolean addExplicitlyBannedMode(Mode mode);

  /**
   * Stop naming a mode the turn is banned for. The last named mode is not removed, because a turn that names no
   * modes is banned for every mode: remove the banned turn itself instead
   *
   * @param mode to stop naming
   * @return true when removed, false when not named or when it is the last named mode
   */
  public abstract boolean removeExplicitlyBannedMode(Mode mode);

  /**
   * Verify if the turn is banned for the mode
   *
   * @param mode to verify
   * @return true when the turn names no modes or names this one, false otherwise
   */
  public default boolean isModeBanned(Mode mode) {
    return !hasExplicitlyBannedModes() || getExplicitlyBannedModes().contains(mode);
  }

  /**
   * Collect which of the given modes the turn is banned for
   *
   * @param modes to check
   * @return the modes among them the turn is banned for, empty when none
   */
  public default Set<Mode> getBannedModesFrom(Collection<Mode> modes) {
    return modes.stream().filter(this::isModeBanned).collect(Collectors.toSet());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicBannedTurn shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicBannedTurn deepClone();
}
