package org.goplanit.utils.network.layer.macroscopic.intersection;

import java.util.List;

import org.goplanit.utils.id.IdGenerator;
import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;
import org.goplanit.utils.network.layer.macroscopic.SpatialMovement;
import org.goplanit.utils.network.layer.physical.Node;

/**
 * An intersection that covers an area: several member nodes, the link segments between them (internal segments, plain
 * or with a signal standing on them), its approaches and every movement through it. There is one movement for each
 * approach and exit its internal segments connect, banned or not, except the exit back along the approach's own link,
 * so a pair of approach and exit without a movement is one they do not connect.
 *
 * @author markr
 */
public interface SpatialIntersection extends Intersection {

  /** id class for generating the ids of this kind */
  public static final Class<SpatialIntersection> SPATIAL_INTERSECTION_ID_CLASS = SpatialIntersection.class;

  /**
   * Class used to generate the ids of this kind via the id generator
   *
   * @return class type
   */
  public default Class<? extends SpatialIntersection> getSpatialIntersectionIdClass() {
    return SPATIAL_INTERSECTION_ID_CLASS;
  }

  /**
   * Generate an id of this kind
   *
   * @param tokenId contiguous id generation within this group for instances of this class
   * @return the generated id
   */
  public default long generateSpatialIntersectionId(final IdGroupingToken tokenId) {
    return IdGenerator.generateId(tokenId, getSpatialIntersectionIdClass());
  }

  /**
   * The id of this intersection among the spatial intersections of its layer, next to its intersection id, which is
   * unique over both kinds
   *
   * @return spatial intersection id
   */
  public abstract long getSpatialIntersectionId();

  /**
   * Add a member node. Refused when the node is where an approach segment starts, since that segment would no longer
   * enter the intersection.
   *
   * @param node to add
   * @return true when added, false when already a member or refused
   */
  public abstract boolean addMemberNode(Node node);

  /**
   * Internal link segments of this intersection. A link segment between two member nodes is internal only when listed
   * here.
   *
   * @return unmodifiable list of internal segments
   */
  public abstract List<MacroscopicLinkSegment> getInternalSegments();

  /**
   * Internal segment with the given id
   *
   * @param segmentId to look for
   * @return internal segment, null when not internal
   */
  public abstract MacroscopicLinkSegment getInternalSegment(long segmentId);

  /**
   * Add an internal segment, which must start and end at member nodes
   *
   * @param segment to add
   * @return true when added, false when refused or already internal
   */
  public abstract boolean addInternalSegment(MacroscopicLinkSegment segment);

  /**
   * Remove an internal segment
   *
   * @param segment to remove
   * @return true when removed, false when not internal
   */
  public abstract boolean removeInternalSegment(MacroscopicLinkSegment segment);

  /**
   * Internal segments with a signal standing on them, such as a stop line in a median. Each is also one of the internal
   * segments; the other internal segments are plain
   *
   * @return unmodifiable list of internal segments with a signal
   */
  public abstract List<MacroscopicLinkSegment> getInternalSegmentsWithSignal();

  /**
   * Add an internal segment with a signal standing on it, or give an internal segment already listed a signal. Like any
   * internal segment it must start and end at member nodes. Removing the internal segment removes its signal
   *
   * @param segment to add
   * @return true when added or given a signal, false when refused or when it already has a signal
   */
  public abstract boolean addInternalSegmentWithSignal(MacroscopicLinkSegment segment);

  /**
   * The movements through this intersection, each from an approach to an exit
   *
   * @return unmodifiable list of movements
   */
  public abstract List<SpatialMovement> getMovements();

  /**
   * Add a movement through this intersection, which must start on one of its approaches
   *
   * @param movement to add
   * @return true when added, false when refused or already present
   */
  public abstract boolean addMovement(SpatialMovement movement);

  /**
   * Remove a movement through this intersection
   *
   * @param movement to remove
   * @return true when removed, false when not present
   */
  public abstract boolean removeMovement(SpatialMovement movement);

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract SpatialIntersection shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract SpatialIntersection deepClone();
}
