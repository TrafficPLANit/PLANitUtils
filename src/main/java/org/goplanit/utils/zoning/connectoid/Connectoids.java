package org.goplanit.utils.zoning.connectoid;

import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.id.ManagedIdEntities;
import org.goplanit.utils.zoning.Zone;
import org.goplanit.utils.zoning.modifier.event.ZoningModifierListener;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Container to register and manager connectoids that define the points of access for
 * zones regarding infrastructure network (layer)
 * 
 * @author markr
 *
 */
public interface Connectoids<T extends Connectoid> extends ManagedIdEntities<T>, ZoningModifierListener{

  /**
   * Lay an index across all connectoids based on their access zones, e.g., a reversed index essentially
   *
   * @return newly created map of each access zone's its connectoids
   */
  public abstract Map<Zone, Set<T>> createIndexByAccessZone();

  /**
   * Collect the connectoids at an access node, compared by identity, so a node of another layer with the same id gives
   * none
   *
   * @param accessNode to look up
   * @return connectoids at the node in the order indexed, empty when none
   */
  public abstract List<T> getByAccessNode(DirectedVertex accessNode);

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Connectoids shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Connectoids deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Connectoids<T> deepCloneWithMapping(BiConsumer<T, T> mapper);
}
