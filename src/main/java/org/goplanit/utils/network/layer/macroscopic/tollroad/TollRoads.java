package org.goplanit.utils.network.layer.macroscopic.tollroad;

import org.goplanit.utils.id.ManagedIdEntities;
import org.goplanit.utils.network.layer.physical.LinkSegment;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.goplanit.utils.containers.ListUtils;

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
   * The link segments among those given that a toll road holds, or that none holds, each once, in the order given
   *
   * @param <LS> type of the link segments
   * @param linkSegments to look for
   * @param onATollRoad true for those a toll road holds, false for those none holds
   * @return the link segments found
   */
  private <LS extends LinkSegment> List<LS> findLinkSegments(Collection<LS> linkSegments, boolean onATollRoad) {
    var found = new ArrayList<LS>();
    for (var linkSegment : linkSegments) {
      if ((getByLinkSegment(linkSegment) != null) == onATollRoad && !ListUtils.containsByIdentity(found, linkSegment)) {
        found.add(linkSegment);
      }
    }
    return found;
  }

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

  /**
   * The link segments among those given that a toll road holds, each once, in the order given: those a new toll road
   * cannot take, as a link segment is on one toll road of its layer at most
   *
   * @param <LS> type of the link segments
   * @param linkSegments to look for
   * @return the link segments a toll road holds
   */
  public default <LS extends LinkSegment> List<LS> findLinkSegmentsOnATollRoad(Collection<LS> linkSegments) {
    return findLinkSegments(linkSegments, true);
  }

  /**
   * The link segments among those given that no toll road holds, each once, in the order given: those a new toll road
   * can take
   *
   * @param <LS> type of the link segments
   * @param linkSegments to look for
   * @return the link segments no toll road holds
   */
  public default <LS extends LinkSegment> List<LS> findLinkSegmentsNotOnATollRoad(Collection<LS> linkSegments) {
    return findLinkSegments(linkSegments, false);
  }
}
