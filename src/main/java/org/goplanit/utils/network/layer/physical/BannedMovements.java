package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.id.ManagedIdEntities;

import java.util.function.BiConsumer;

/**
 * Primary managed container for movements explicitly and create them on the container via
 * its dedicated factory class
 * 
 * @author markr
  */
public interface BannedMovements extends ManagedIdEntities<BannedMovement> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MovementFactory getFactory();

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
