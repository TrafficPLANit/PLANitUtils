package org.goplanit.utils.network.layer.macroscopic.intersection;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.goplanit.utils.id.ManagedIdEntities;
import org.goplanit.utils.network.layer.macroscopic.SpatialMovement;
import org.goplanit.utils.network.layer.physical.LinkSegment;
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
   * Registered intersection a link segment ends in: the one its downstream node is a member of. For example the
   * intersection an approach enters, or the one an internal segment lies in
   *
   * @param linkSegment to look up
   * @return intersection, null when its downstream node is a member of none
   */
  public default Intersection getByDownstreamNodeOf(LinkSegment linkSegment){
    return getByMemberNode(linkSegment.getDownstreamNode());
  }

  /**
   * Registered spatial intersection a movement passes through: the one its from segment ends in
   *
   * @param movement to look up
   * @return spatial intersection, null when its from segment ends in none or in one that is not spatial
   */
  public default SpatialIntersection getSpatialIntersectionOf(SpatialMovement movement){
    var intersection = getByDownstreamNodeOf(movement.getLinkSegmentFrom());
    return intersection instanceof SpatialIntersection ? (SpatialIntersection) intersection : null;
  }

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
   * Verify if this container still holds the very intersection given, for example one held on to while others were
   * merged away. Only the same instance counts, not another one with its id
   *
   * @param intersection to verify
   * @return true when it is still registered here, false otherwise
   */
  public default boolean contains(Intersection intersection) {
    return get(intersection.getId()) == intersection;
  }

  /**
   * Stream the registered intersections that meet a condition, in the order of this container, which is by id
   *
   * @param condition to meet
   * @return the intersections meeting the condition
   */
  public default Stream<Intersection> stream(Predicate<? super Intersection> condition) {
    return stream().filter(condition);
  }

  /**
   * Stream the registered spatial intersections, by id
   *
   * @return the spatial intersections
   */
  public default Stream<SpatialIntersection> streamSpatial() {
    return stream(SpatialIntersection.class::isInstance).map(SpatialIntersection.class::cast);
  }

  /**
   * Stream the registered non-spatial intersections, by id
   *
   * @return the non-spatial intersections
   */
  public default Stream<NonSpatialIntersection> streamNonSpatial() {
    return stream(NonSpatialIntersection.class::isInstance).map(NonSpatialIntersection.class::cast);
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
