package org.goplanit.utils.network.layer.macroscopic.intersection;

import java.util.List;
import java.util.stream.Collectors;

import org.goplanit.utils.id.IdAbleUtils;
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
 * <p>
 * Next to its approaches, its internal segments with a signal and its movements can have a green ratio and saturation
 * flow factor. A movement's own value is used in place of its approach's, not multiplied with it.
 * </p>
 * <p>
 * A listed movement is not always one a mode can make. A movement can be banned for a mode explicitly, by a direct ban
 * stored on the movement, see {@link SpatialMovement#hasDirectBan()}. It can also be banned implicitly, by a turn
 * banned for the mode at a member node, or by an internal segment the mode has no access to, leaving the mode with no
 * connection from the approach to the exit. An implicit ban is not stored, on the movement or on the intersection. It
 * follows from the network inside the intersection as it is now.
 * </p>
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
   * The internal segment objects of this intersection, one for each link segment between two member nodes listed as
   * internal: an {@link IntersectionSignalSegment} when a signal stands on it, an
   * {@link IntersectionUnsignalisedSegment} when it is plain, each with its values. Being in this list is what makes it
   * an internal segment; a link segment between two member nodes is internal only when listed here
   *
   * @return unmodifiable list of internal segment objects, in the order they were added
   */
  public abstract List<IntersectionSegment> getIntersectionInternalSegments();

  /**
   * The internal segment object of this intersection for a link segment, the very segment rather than one equal to it
   *
   * @param segment the link segment
   * @return the internal segment object, an {@link IntersectionSignalSegment} or
   * {@link IntersectionUnsignalisedSegment}, null when the link segment is not internal
   */
  public abstract IntersectionSegment getIntersectionInternalSegment(MacroscopicLinkSegment segment);

  /**
   * Internal link segments of this intersection, see {@link #getIntersectionInternalSegments()}
   *
   * @return unmodifiable list of internal segments
   */
  public default List<MacroscopicLinkSegment> getInternalSegments() {
    return getIntersectionInternalSegments().stream().map(IntersectionSegment::getLinkSegment)
        .collect(Collectors.toUnmodifiableList());
  }

  /**
   * Internal segment with the given id
   *
   * @param segmentId to look for
   * @return internal segment, null when not internal
   */
  public default MacroscopicLinkSegment getInternalSegment(long segmentId) {
    return IdAbleUtils.findById(getInternalSegments(), segmentId);
  }

  /**
   * Create a new plain internal segment object, an {@link IntersectionUnsignalisedSegment} without values, and add it
   * to this intersection's internal segments. Its link segment must start and end at member nodes
   *
   * @param segment to add
   * @return true when added, false when refused or already internal
   */
  public abstract boolean addNewInternalSegment(MacroscopicLinkSegment segment);

  /**
   * Remove an internal segment, with its signal and values
   *
   * @param segment to remove
   * @return true when removed, false when not internal
   */
  public abstract boolean removeInternalSegment(MacroscopicLinkSegment segment);

  /**
   * Internal link segments with a signal standing on them; the other internal segments are plain
   *
   * @return unmodifiable list of internal segments with a signal
   */
  public default List<MacroscopicLinkSegment> getInternalSegmentsWithSignal() {
    return getIntersectionInternalSegments().stream().filter(IntersectionSegment::hasSignal)
        .map(IntersectionSegment::getLinkSegment).collect(Collectors.toUnmodifiableList());
  }

  /**
   * Create a new internal segment object with a signal standing on it, an {@link IntersectionSignalSegment}, and add
   * it to this intersection's internal segments, or give an internal segment already listed a signal: a new
   * {@link IntersectionSignalSegment} then takes its place in the list, with its saturation flow factor copied, and
   * the old object is no longer one of the internal segments. Like any internal segment its link segment must start
   * and end at member nodes
   *
   * @param segment to add
   * @return true when added or given a signal, false when refused or when it already has a signal
   */
  public abstract boolean addNewInternalSegmentWithSignal(MacroscopicLinkSegment segment);

  /**
   * The movements through this intersection, each from an approach to an exit
   *
   * @return unmodifiable list of movements
   */
  public abstract List<SpatialMovement> getMovements();

  /**
   * The movements through this intersection starting on an approach
   *
   * @param approach object of this intersection
   * @return the movements starting on its link segment, in the order of {@link #getMovements()}
   */
  public default List<SpatialMovement> getMovementsFrom(IntersectionSegment approach) {
    return getMovements().stream().filter(movement -> movement.getLinkSegmentFrom() == approach.getLinkSegment())
        .collect(Collectors.toUnmodifiableList());
  }

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
   * The green ratio a movement uses: its own when it has one, otherwise that of its approach. For example, on an
   * approach of 0.44 a right turn on a short arrow with its own 0.15 uses 0.15, and the other movements use 0.44
   *
   * @param movement to get it for, starting on one of the approaches
   * @return the green ratio it uses, 1 when it has none and its approach has no signal
   */
  public default double getGreenRatioUsedBy(SpatialMovement movement) {
    var own = movement.getGreenRatio();
    var approach = getApproach(movement.getLinkSegmentFrom());
    return own != null ? own :
        approach instanceof IntersectionSignalSegment ? ((IntersectionSignalSegment) approach).getGreenRatio() : 1;
  }

  /**
   * The saturation flow factor a movement uses: its own when it has one, otherwise that of its approach
   *
   * @param movement to get it for, starting on one of the approaches
   * @return the saturation flow factor it uses, 1 when it has none and does not start on an approach
   */
  public default double getSaturationFlowFactorUsedBy(SpatialMovement movement) {
    var own = movement.getSaturationFlowFactor();
    var approach = getApproach(movement.getLinkSegmentFrom());
    return own != null ? own : approach != null ? approach.getSaturationFlowFactor() : 1;
  }

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
