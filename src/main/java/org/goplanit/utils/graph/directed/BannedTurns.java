package org.goplanit.utils.graph.directed;

import org.goplanit.utils.graph.ManagedGraphEntities;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Primary managed container for banned turns explicitly and create them on the container via
 * its dedicated factory class
 * 
 * @author markr
  */
public interface BannedTurns extends ManagedGraphEntities<BannedTurn> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract BannedTurnFactory getFactory();

  /**
   * Collect the banned turns starting or ending at the segment, found by identity without going over every banned
   * turn
   *
   * @param segment to look up
   * @return banned turns starting or ending at the segment, empty when none
   */
  public abstract List<BannedTurn> getBySegment(EdgeSegment segment);

  /**
   * Apply a change to the segments of a registered banned turn, keeping the lookup by segment in step with what it
   * refers to before and after. The only way to change the segments of a registered banned turn
   *
   * @param bannedTurn to change
   * @param change to apply to it
   */
  public abstract void update(BannedTurn bannedTurn, Consumer<BannedTurn> change);

  /**
   * Replace the segments of every banned turn based on the mapping provided, e.g. to their copies after a deep copy
   * of the graph, keeping the lookup by segment in step
   *
   * @param segmentToSegmentMapping should contain the segment as currently used and then the value is the new segment
   *                                to replace it
   * @param removeMissingMappings when true remove banned turns with a segment without a mapping
   * @param <T> type of segment
   */
  public abstract <T extends EdgeSegment> void updateSegmentMapping(
      Function<T, T> segmentToSegmentMapping, boolean removeMissingMappings);

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract BannedTurns shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract BannedTurns deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract BannedTurns deepCloneWithMapping(BiConsumer<BannedTurn, BannedTurn> mapper);

}
