package org.goplanit.utils.graph.directed;

import org.goplanit.utils.graph.GraphEntity;

import java.io.Serializable;

/**
 * A banned turn comprises a combined and ordered traversal of two adjacent edge segments (from, to).
 * <p>
 * The setters are raw edits. Once a banned turn is registered in its container, change its segments only through
 * {@link BannedTurns#update(BannedTurn, java.util.function.Consumer)}: a raw edit is not seen by the
 * container's lookup by segment, which then misses it under its new segment and still returns it under its old one.
 * </p>
 *
 * @author markr
 *
 */
public interface BannedTurn extends Turn, Serializable, GraphEntity {
  
  /** id class for generating ids */
  public static final Class<BannedTurn> BANNED_TURN_ID_CLASS = BannedTurn.class;

  /**
   * Set from segment. A raw edit: on a registered banned turn, call it only within
   * {@link BannedTurns#update(BannedTurn, java.util.function.Consumer)}, otherwise the container's lookup by
   * segment goes stale
   *
   * @param segment to set
   */
  public void setSegmentFrom(EdgeSegment segment);

  /**
   * Set to segment. A raw edit: on a registered banned turn, call it only within
   * {@link BannedTurns#update(BannedTurn, java.util.function.Consumer)}, otherwise the container's lookup by
   * segment goes stale
   *
   * @param segment to set
   */
  public void setSegmentTo(EdgeSegment segment);

  /**
   * {@inheritDoc}
   */
  public abstract BannedTurn shallowClone();

  /**
   * {@inheritDoc}
   */
  public abstract BannedTurn deepClone();
  }
