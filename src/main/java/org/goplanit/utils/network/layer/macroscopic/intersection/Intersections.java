package org.goplanit.utils.network.layer.macroscopic.intersection;

import java.util.function.BiConsumer;

import org.goplanit.utils.id.ManagedIdEntities;
import org.goplanit.utils.network.layer.physical.Node;

/**
 * Container of the intersections of a layer. Registering an intersection makes its member nodes known to the lookup by
 * member node; the lookup is kept current by registering and removing intersections and by the layer modifier, while
 * changes made directly on a registered intersection are not reflected in it.
 *
 * @author markr
 */
public interface Intersections extends ManagedIdEntities<Intersection> {

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract IntersectionFactory getFactory();

  /**
   * Registered intersection the node is a member of
   *
   * @param node to look up
   * @return intersection, null when the node is a member of none
   */
  public abstract Intersection getByMemberNode(Node node);

  /**
   * Verify if the node is a member of a registered signalised intersection
   *
   * @param node to verify
   * @return true when signalised, false otherwise
   */
  public default boolean isSignalised(Node node){
    var intersection = getByMemberNode(node);
    return intersection != null && intersection.isSignalised();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Intersections shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Intersections deepClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Intersections deepCloneWithMapping(BiConsumer<Intersection, Intersection> mapper);
}
