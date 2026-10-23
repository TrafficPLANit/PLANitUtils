package org.goplanit.utils.network.layer.macroscopic;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.goplanit.utils.id.ManagedIdEntities;
import org.goplanit.utils.network.layer.physical.LinkSegment;

/**
 * Container of the spatial movements of a layer, both those of its spatial intersections and its independent ones,
 * with a lookup of the movements starting or ending on a link segment. The lookup is kept current by registering and
 * removing movements and by {@link #update(SpatialMovement, Consumer)}; raw edits of a registered movement are not
 * reflected in it.
 *
 * @author markr
 */
public interface SpatialMovements extends ManagedIdEntities<SpatialMovement> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract SpatialMovementFactory getFactory();

  /**
   * Collect the registered movements starting or ending on the link segment, found by identity without going over
   * every movement
   *
   * @param linkSegment to look up
   * @return movements with the link segment as from or to segment, empty when none
   */
  public abstract List<SpatialMovement> getBySegment(LinkSegment linkSegment);

  /**
   * Apply a change to the segments of a registered movement, keeping the lookup by segment in step with what it refers
   * to before and after. The only way to change the segments of a registered movement
   *
   * @param movement to change
   * @param change to apply to it
   */
  public abstract void update(SpatialMovement movement, Consumer<SpatialMovement> change);

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract SpatialMovements shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract SpatialMovements deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract SpatialMovements deepCloneWithMapping(BiConsumer<SpatialMovement, SpatialMovement> mapper);
}
