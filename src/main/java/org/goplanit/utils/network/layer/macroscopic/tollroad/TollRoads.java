package org.goplanit.utils.network.layer.macroscopic.tollroad;

import org.goplanit.utils.id.ManagedIdEntities;
import org.goplanit.utils.network.layer.physical.LinkSegment;

/**
 * The toll roads of a layer, found by (referenced non-owned) link segment
 * <p>
 * A layer holds its toll roads as it holds its intersections, and the container finds the toll road of a link segment
 * without going over them all, as a check of whether a link segment is already tolled, or a scheme following a
 * removed link segment, needs.
 * </p>
 *
 * @author markr
 */
public interface TollRoads extends ManagedIdEntities<TollRoad> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract TollRoadFactory getFactory();

  /**
   * The registered toll road a link segment is on
   *
   * @param linkSegment to look up
   * @return the toll road, null when the link segment is not tolled
   */
  public abstract TollRoad getByLinkSegment(LinkSegment linkSegment);
}
