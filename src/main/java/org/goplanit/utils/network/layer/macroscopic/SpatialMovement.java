package org.goplanit.utils.network.layer.macroscopic;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import org.goplanit.utils.graph.directed.Turn;
import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.id.ManagedId;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.macroscopic.intersection.SpatialIntersection;

/**
 * A movement from one link segment to another. It is either a movement through a spatial intersection, from one of
 * its approaches to one of its exits, or an independent movement that belongs to no intersection and is stored only
 * because a ban is placed on it.
 * <p>
 * A movement through a spatial intersection may be a single turn: an approach and an exit that meet at the same
 * member node, as at a corner of a divided-road crossing. It is still a movement, because it belongs to the
 * intersection, which lists every movement through it. Other movements pass internal segments on their way. Outside a
 * spatial intersection, a {@link Turn} is in effect a non-spatial movement.
 * </p>
 * <p>
 * A movement records its length and, when there is one, the ban placed on the movement itself: its direct ban. It
 * records neither the modes that can make it nor the internal segments it passes, so it does not depend on the detail
 * inside an intersection. A movement without a direct ban can still be banned in effect, by a banned turn on its way
 * through the intersection, or blocked by mode access. That ban is not stored on the movement: it follows from the
 * turns and segments on its way. It is not a direct ban, so {@link #hasDirectBan()} and
 * {@link #isModeDirectlyBanned(Mode)} do not report it.
 * </p>
 * <p>
 * The setters of its segments are raw edits. Once registered in its container, change its segments only through
 * {@link SpatialMovements#update(SpatialMovement, java.util.function.Consumer)}, so the container's lookup by segment
 * stays current.
 * </p>
 *
 * @author markr
 */
public interface SpatialMovement extends ExternalIdAble, ManagedId, Serializable {

  /** id class for generating ids */
  public static final Class<SpatialMovement> SPATIAL_MOVEMENT_ID_CLASS = SpatialMovement.class;

  /**
   * {@inheritDoc}
   */
  @Override
  public default Class<? extends SpatialMovement> getIdClass() {
    return SPATIAL_MOVEMENT_ID_CLASS;
  }

  /**
   * The link segment the movement starts on, for a movement of an intersection one of its approaches
   *
   * @return from link segment
   */
  public abstract MacroscopicLinkSegment getLinkSegmentFrom();

  /**
   * The link segment the movement ends on, for a movement of an intersection one of its exits
   *
   * @return to link segment
   */
  public abstract MacroscopicLinkSegment getLinkSegmentTo();

  /**
   * Set the link segment the movement starts on. A raw edit, see the class description
   *
   * @param linkSegment to set
   */
  public abstract void setLinkSegmentFrom(MacroscopicLinkSegment linkSegment);

  /**
   * Set the link segment the movement ends on. A raw edit, see the class description
   *
   * @param linkSegment to set
   */
  public abstract void setLinkSegmentTo(MacroscopicLinkSegment linkSegment);

  /**
   * The length of the movement, from where its from segment ends to where its to segment starts
   *
   * @return length in km, 0 when both meet at one node
   */
  public abstract double getLengthKm();

  /**
   * Verify if a ban is placed on the movement itself. A movement without one can still be blocked by a banned turn on
   * its way, or by mode access
   *
   * @return true when banned directly, false otherwise
   */
  public abstract boolean hasDirectBan();

  /**
   * Collect the modes the direct ban names. A direct ban that names no modes bans every mode
   *
   * @return the modes named, unmodifiable, empty when there is no direct ban or it bans every mode
   */
  public abstract Set<Mode> getExplicitlyDirectlyBannedModes();

  /**
   * Place a ban on the movement itself, replacing any it had
   *
   * @param explicitlyBannedModes the modes it bans; when empty it bans every mode
   */
  public abstract void banDirectly(Collection<Mode> explicitlyBannedModes);

  /**
   * Remove the ban placed on the movement itself, when it has one
   *
   * @return true when removed, false when it had none
   */
  public abstract boolean removeDirectBan();

  /**
   * Verify if the movement is banned directly for the mode
   *
   * @param mode to verify
   * @return true when it has a direct ban that names no modes or names this one, false otherwise
   */
  public default boolean isModeDirectlyBanned(Mode mode) {
    return hasDirectBan() &&
        (getExplicitlyDirectlyBannedModes().isEmpty() || getExplicitlyDirectlyBannedModes().contains(mode));
  }

  /**
   * The green ratio of this movement, used in place of that of its approach, from 0 to 1. A value of 1 is used in place
   * of the approach's too: a movement with 1 on an approach of 0.44 uses 1. See
   * {@link SpatialIntersection#getGreenRatioUsedBy(SpatialMovement)}
   *
   * @return the green ratio, null when the movement uses that of its approach
   */
  public abstract Double getGreenRatio();

  /**
   * Set the green ratio of this movement, used in place of that of its approach. It is always set directly, no
   * algorithm computes it
   *
   * @param greenRatio from 0 to 1
   * @return true when set, false when the value is outside 0 to 1, leaving the movement unchanged
   */
  public abstract boolean setGreenRatio(double greenRatio);

  /**
   * Remove the green ratio of this movement, so it uses that of its approach again
   *
   * @return true when removed, false when none was set
   */
  public abstract boolean removeGreenRatio();

  /**
   * The saturation flow factor of this movement, used in place of that of its approach, from 0 to 1. A value of 1 is
   * used in place of the approach's too
   *
   * @return the saturation flow factor, null when the movement uses that of its approach
   */
  public abstract Double getSaturationFlowFactor();

  /**
   * Set the saturation flow factor of this movement, used in place of that of its approach. It is always set directly,
   * no algorithm computes it
   *
   * @param saturationFlowFactor from 0 to 1
   * @return true when set, false when the value is outside 0 to 1, leaving the movement unchanged
   */
  public abstract boolean setSaturationFlowFactor(double saturationFlowFactor);

  /**
   * Remove the saturation flow factor of this movement, so it uses that of its approach again
   *
   * @return true when removed, false when none was set
   */
  public abstract boolean removeSaturationFlowFactor();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract SpatialMovement shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract SpatialMovement deepClone();
}
