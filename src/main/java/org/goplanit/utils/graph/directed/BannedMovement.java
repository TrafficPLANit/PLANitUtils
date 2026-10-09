package org.goplanit.utils.graph.directed;

import org.goplanit.utils.graph.GraphEntity;

import java.io.Serializable;

/**
 * A Banned movement comprises a combined and ordered traversal of two adjacent edge segments (from, to).
 * <p>
 * The setters are raw edits. Once a banned movement is registered in its container, change its segments only through
 * {@link BannedMovements#update(BannedMovement, java.util.function.Consumer)}: a raw edit is not seen by the
 * container's lookup by segment, which then misses it under its new segment and still returns it under its old one.
 * </p>
 *
 * @author markr
 *
 */
public interface BannedMovement extends Movement, Serializable, GraphEntity {
  
  /** id class for generating ids */
  public static final Class<BannedMovement> BANNED_MOVEMENT_ID_CLASS = BannedMovement.class;

  /**
   * Set from segment. A raw edit: on a registered banned movement, call it only within
   * {@link BannedMovements#update(BannedMovement, java.util.function.Consumer)}, otherwise the container's lookup by
   * segment goes stale
   *
   * @param segment to set
   */
  public void setSegmentFrom(EdgeSegment segment);

  /**
   * Set to segment. A raw edit: on a registered banned movement, call it only within
   * {@link BannedMovements#update(BannedMovement, java.util.function.Consumer)}, otherwise the container's lookup by
   * segment goes stale
   *
   * @param segment to set
   */
  public void setSegmentTo(EdgeSegment segment);

  /**
   * {@inheritDoc}
   */
  public abstract BannedMovement shallowClone();

  /**
   * {@inheritDoc}
   */
  public abstract BannedMovement deepClone();
  }
