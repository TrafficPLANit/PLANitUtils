package org.goplanit.utils.network.layer.macroscopic.intersection;

import org.goplanit.utils.id.IdGenerator;
import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.network.layer.physical.Node;

/**
 * An intersection that is a point: its one node, with its approaches and how it is controlled. It has no internal
 * segments and no movements. Every turn at its node is allowed unless banned, and its banned turns belong to the node,
 * found through the layer's banned turns rather than stored on the intersection.
 *
 * @author markr
 */
public interface NonSpatialIntersection extends Intersection {

  /** id class for generating the ids of this kind */
  public static final Class<NonSpatialIntersection> NON_SPATIAL_INTERSECTION_ID_CLASS = NonSpatialIntersection.class;

  /**
   * Class used to generate the ids of this kind via the id generator
   *
   * @return class type
   */
  public default Class<? extends NonSpatialIntersection> getNonSpatialIntersectionIdClass() {
    return NON_SPATIAL_INTERSECTION_ID_CLASS;
  }

  /**
   * Generate an id of this kind
   *
   * @param tokenId contiguous id generation within this group for instances of this class
   * @return the generated id
   */
  public default long generateNonSpatialIntersectionId(final IdGroupingToken tokenId) {
    return IdGenerator.generateId(tokenId, getNonSpatialIntersectionIdClass());
  }

  /**
   * The id of this intersection among the non-spatial intersections of its layer, next to its intersection id, which
   * is unique over both kinds
   *
   * @return non-spatial intersection id
   */
  public abstract long getNonSpatialIntersectionId();

  /**
   * The node of this intersection
   *
   * @return its node, null when it was removed
   */
  public default Node getNode() {
    return getMemberNodes().isEmpty() ? null : getMemberNodes().get(0);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract NonSpatialIntersection shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract NonSpatialIntersection deepClone();
}
