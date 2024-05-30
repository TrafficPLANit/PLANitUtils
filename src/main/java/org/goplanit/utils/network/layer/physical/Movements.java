package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.id.ManagedIdEntities;
import org.goplanit.utils.network.virtual.CentroidVertex;
import org.goplanit.utils.zoning.OdZone;
import org.goplanit.utils.zoning.TransferZone;
import org.goplanit.utils.zoning.Zone;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Primary managed container for movements explicitly and create them on the container via
 * its dedicated factory class
 * 
 * @author markr
  */
public interface Movements extends ManagedIdEntities<Movement> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MovementFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Movements shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Movements deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Movements deepCloneWithMapping(BiConsumer<Movement, Movement> mapper);

}
