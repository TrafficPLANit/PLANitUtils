package org.goplanit.utils.graph.directed;

import org.goplanit.utils.graph.ManagedGraphEntities;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Primary managed container for movements explicitly and create them on the container via
 * its dedicated factory class
 * 
 * @author markr
  */
public interface BannedMovements extends ManagedGraphEntities<BannedMovement> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract BannedMovementFactory getFactory();

  /**
   * Collect the banned movements starting or ending at the segment, found by identity without going over every banned
   * movement
   *
   * @param segment to look up
   * @return banned movements starting or ending at the segment, empty when none
   */
  public abstract List<BannedMovement> getBySegment(EdgeSegment segment);

  /**
   * Apply a change to the segments of a registered banned movement, keeping the lookup by segment in step with what it
   * refers to before and after. The only way to change the segments of a registered banned movement
   *
   * @param bannedMovement to change
   * @param change to apply to it
   */
  public abstract void update(BannedMovement bannedMovement, Consumer<BannedMovement> change);

  /**
   * Replace the segments of every banned movement based on the mapping provided, e.g. to their copies after a deep copy
   * of the graph, keeping the lookup by segment in step
   *
   * @param segmentToSegmentMapping should contain the segment as currently used and then the value is the new segment
   *                                to replace it
   * @param removeMissingMappings when true remove banned movements with a segment without a mapping
   * @param <T> type of segment
   */
  public abstract <T extends EdgeSegment> void updateSegmentMapping(
      Function<T, T> segmentToSegmentMapping, boolean removeMissingMappings);

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract BannedMovements shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract BannedMovements deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract BannedMovements deepCloneWithMapping(BiConsumer<BannedMovement, BannedMovement> mapper);

}
