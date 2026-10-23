package org.goplanit.utils.network.layer.macroscopic.intersection;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.goplanit.utils.containers.ListUtils;
import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.id.IdAbleUtils;
import org.goplanit.utils.id.ManagedId;
import org.goplanit.utils.network.layer.physical.LinkSegment;
import org.goplanit.utils.network.layer.physical.Node;

/**
 * An intersection is a junction or crossing of a layer. It comes in two kinds: a {@link NonSpatialIntersection} is a
 * point, its one node, while a {@link SpatialIntersection} covers an area, several nodes and the link segments between
 * them. Both record how they are controlled, which kinds of intersection they are, and which link segments enter them
 * (approaches), each with how much of its capacity can pass: its green ratio and saturation flow factor. They share one
 * id across both kinds, and each also has an id of its own kind.
 * <p>
 * The methods on an intersection are raw edits: they change only this intersection and fire no events. Once an
 * intersection is registered in its layer, changes that must keep the rest of the layer consistent are made through
 * the layer modifier. References are held in lists, so they remain valid when ids are recreated.
 * </p>
 *
 * @author markr
 */
public interface Intersection extends ExternalIdAble, ManagedId, Serializable {

  /** id class for generating ids */
  public static final Class<Intersection> INTERSECTION_ID_CLASS = Intersection.class;

  /**
   * {@inheritDoc}
   */
  @Override
  public default Class<? extends Intersection> getIdClass() {
    return INTERSECTION_ID_CLASS;
  }

  /**
   * Member nodes of this intersection
   *
   * @return unmodifiable list of member nodes
   */
  public abstract List<Node> getMemberNodes();

  /**
   * Member node with the given id
   *
   * @param nodeId to look for
   * @return member node, null when not a member
   */
  public abstract Node getMemberNode(long nodeId);

  /**
   * Verify the vertex is one of the member nodes, compared by identity since ids can be recreated
   *
   * @param vertex to check, may be null
   * @return true when a member node, false otherwise
   */
  public default boolean hasMemberNode(DirectedVertex vertex) {
    return ListUtils.containsByIdentity(getMemberNodes(), vertex);
  }

  /**
   * Remove a member node
   *
   * @param node to remove
   * @param removeDependentReferences when true, the approach segments ending at the node, and on a spatial
   *                                  intersection the internal segments touching it, are removed as well; when false
   *                                  only the node is removed, leaving those references to be removed by other changes
   * @return true when removed, false when not a member
   */
  public abstract boolean removeMemberNode(Node node, boolean removeDependentReferences);

  /**
   * How traffic at this intersection is controlled
   *
   * @return control type
   */
  public abstract IntersectionControlType getControlType();

  /**
   * Set how traffic at this intersection is controlled
   *
   * @param controlType to set
   */
  public abstract void setControlType(IntersectionControlType controlType);

  /**
   * Kinds of this intersection, at least one
   *
   * @return unmodifiable set of kinds
   */
  public abstract Set<IntersectionType> getTypes();

  /**
   * Add a kind
   *
   * @param type to add
   * @return true when added, false when already present
   */
  public abstract boolean addType(IntersectionType type);

  /**
   * The approach objects of this intersection, one for each link segment entering it: an
   * {@link IntersectionSignalSegment} for an approach with a signal of its own, an
   * {@link IntersectionUnsignalisedSegment} for one without, each with its values. Being in this list is what makes it
   * an approach. Every way out of the intersection is allowed from an approach unless a banned turn of the layer,
   * starting from its link segment, forbids it
   *
   * @return unmodifiable list of approach objects, in the order they were added
   */
  public abstract List<IntersectionSegment> getApproaches();

  /**
   * The approach object entering this intersection on a link segment, the very segment rather than one equal to it
   *
   * @param entrySegment the link segment the approach enters on
   * @return the approach object, an {@link IntersectionSignalSegment} or {@link IntersectionUnsignalisedSegment}, null
   * when the segment is not an approach segment
   */
  public abstract IntersectionSegment getApproach(LinkSegment entrySegment);

  /**
   * The approach or internal segment object of this intersection for a link segment, with its signal and values. An
   * intersection that is not spatial has only approaches
   *
   * @param linkSegment the link segment
   * @return its approach object, else its internal segment object, null when it is neither
   */
  public default IntersectionSegment getApproachOrInternalSegment(LinkSegment linkSegment) {
    return getApproach(linkSegment);
  }

  /**
   * Create a new approach object, without values, for a link segment that ends at a member node and starts at a node
   * that is not a member, and add it to this intersection's approaches: an {@link IntersectionSignalSegment} or an
   * {@link IntersectionUnsignalisedSegment} as chosen, whatever the control type of the intersection. For example, at
   * a signalised intersection a driveway joining inside the junction is added without a signal of its own
   *
   * @param entrySegment the link segment it enters on
   * @param hasOwnSignal true to create an {@link IntersectionSignalSegment}, false to create an
   *                     {@link IntersectionUnsignalisedSegment}
   * @return the new approach object, null when the segment is an approach segment already or does not enter the
   * intersection
   */
  public abstract IntersectionSegment addNewApproach(LinkSegment entrySegment, boolean hasOwnSignal);

  /**
   * Create a new approach object, without values, for a link segment entering the intersection, and add it to this
   * intersection's approaches, following the control type of the intersection: an {@link IntersectionSignalSegment}
   * when the intersection is signalised, an {@link IntersectionUnsignalisedSegment} when it is not. To choose, see
   * {@link #addNewApproach(LinkSegment, boolean)}
   *
   * @param entrySegment the link segment it enters on
   * @return the new approach object, null when the segment is an approach segment already or does not enter the
   * intersection
   */
  public default IntersectionSegment addNewApproach(LinkSegment entrySegment) {
    return addNewApproach(entrySegment, isSignalised());
  }

  /**
   * Approach segments of this intersection: the link segments its approaches enter on, see {@link #getApproaches()}
   *
   * @return unmodifiable list of approach segments, in the order their approaches were added
   */
  public default List<LinkSegment> getApproachSegments() {
    return getApproaches().stream().map(IntersectionSegment::getLinkSegment).collect(Collectors.toUnmodifiableList());
  }

  /**
   * Approach segment with the given id
   *
   * @param segmentId to look for
   * @return approach segment, null when no approach segment has this id
   */
  public default LinkSegment getApproachSegment(long segmentId) {
    return IdAbleUtils.findById(getApproachSegments(), segmentId);
  }

  /**
   * Create a new approach object, without values, for a link segment entering the intersection, and add it to this
   * intersection's approaches, following the control type of the intersection: an {@link IntersectionSignalSegment}
   * when the intersection is signalised, an {@link IntersectionUnsignalisedSegment} when it is not. The same as
   * {@link #addNewApproach(LinkSegment)}, telling only whether it was added
   *
   * @param segment to add
   * @return true when added, false when already an approach segment or refused because it does not enter the
   * intersection
   */
  public default boolean addNewApproachSegment(LinkSegment segment) {
    return addNewApproach(segment) != null;
  }

  /**
   * Remove the approach object entering on a link segment from this intersection's approaches, with its signal and
   * values
   *
   * @param segment to remove
   * @return true when removed, false when not an approach segment
   */
  public abstract boolean removeApproachSegment(LinkSegment segment);

  /**
   * Let the approach entering on a link segment enter on another link segment instead, for example on the new link
   * segment ending at the intersection once the approach's link is broken at a node upstream. It stays the same
   * approach object: only its link segment changes, so it keeps its signal and its values without copying them
   *
   * @param approachSegment the link segment the approach enters on now
   * @param entrySegment the link segment it is to enter on instead
   * @return true when set, false when the first is not an approach segment or the second does not enter the
   * intersection or is an approach segment already, leaving the intersection unchanged
   */
  public abstract boolean setEntrySegmentOnApproach(
      LinkSegment approachSegment, LinkSegment entrySegment);

  /**
   * Verify if a link segment is an exit of this intersection: it starts at a member node and ends at a vertex that is
   * not a member. For example a link segment of the layer leaving the intersection, or a connectoid segment leaving a
   * member node towards the centroid of its zone. Exits are not stored, they follow from the member nodes
   *
   * @param segment to verify
   * @return true when an exit, false otherwise
   */
  public default boolean isExitSegment(LinkSegment segment) {
    return hasMemberNode(segment.getUpstreamVertex()) && !hasMemberNode(segment.getDownstreamVertex());
  }

  /**
   * Verify if an approach segment has a signal of its own: its approach is a signal segment. When added, an approach
   * follows the control type of the intersection, and it keeps its signal when the control type changes. For example,
   * at a signalised intersection a driveway joining inside the junction has no signal of its own
   *
   * @param approachSegment to check
   * @return true when the approach segment has a signal of its own, false otherwise or when it is not an approach
   * segment
   */
  public default boolean hasOwnSignal(LinkSegment approachSegment) {
    var approach = getApproach(approachSegment);
    return approach != null && approach.hasSignal();
  }

  /**
   * Swap the approach object entering on a link segment for an {@link IntersectionSignalSegment}, when it is to have a
   * signal of its own, or for an {@link IntersectionUnsignalisedSegment}, when not. When it is of that kind already, it
   * stays the same object. Otherwise a new approach object of the other kind takes its place in this intersection's
   * approaches, for the same link segment and with its saturation flow factor copied; without a signal it has no green
   * ratio. The old object is no longer one of the approaches
   *
   * @param approachSegment the link segment the approach enters on
   * @param hasOwnSignal true for a signal segment, false for an unsignalised segment
   * @return the approach after the swap, null when the segment is not an approach segment
   */
  public abstract IntersectionSegment swapApproachToNewSignalOrUnsignalised(
      LinkSegment approachSegment, boolean hasOwnSignal);

  /**
   * Verify if this intersection is signalised
   *
   * @return true when signalised, false otherwise
   */
  public default boolean isSignalised(){
    return getControlType() == IntersectionControlType.SIGNALISED;
  }

  /**
   * Verify if this intersection has the kind junction, among any others
   *
   * @return true when it is a junction, false otherwise
   */
  public default boolean isJunction() {
    return getTypes().contains(IntersectionType.JUNCTION);
  }

  /**
   * Verify if this intersection is a signalised junction
   *
   * @return true when it is signalised and has the kind junction, false otherwise
   */
  public default boolean isSignalisedJunction() {
    return isSignalised() && isJunction();
  }

  /**
   * Verify if this intersection has the kind crossing and no other
   *
   * @return true when it is a crossing only, false otherwise
   */
  public default boolean isCrossingOnly() {
    return getTypes().equals(Set.of(IntersectionType.CROSSING));
  }

  /**
   * Verify if a link segment is one of the approach segments of this intersection, the very segment rather than one
   * equal to it
   *
   * @param segment to verify
   * @return true when it is an approach segment, false otherwise
   */
  public default boolean isApproachSegment(LinkSegment segment) {
    return ListUtils.containsByIdentity(getApproachSegments(), segment);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Intersection shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Intersection deepClone();
}
