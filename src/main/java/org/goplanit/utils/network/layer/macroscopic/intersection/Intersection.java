package org.goplanit.utils.network.layer.macroscopic.intersection;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.id.ExternalIdAble;
import org.goplanit.utils.id.ManagedId;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;
import org.goplanit.utils.network.layer.physical.Node;

/**
 * An intersection groups one or more nodes of a layer that together form a junction or crossing, records how it is
 * controlled, and which link segments enter it (approaches) and lie inside it (internal segments).
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
    return getMemberNodes().stream().anyMatch(node -> node == vertex);
  }

  /**
   * Add a member node. Refused when the node is where an approach segment starts, since that segment would no longer
   * enter the intersection.
   *
   * @param node to add
   * @return true when added, false when already a member or refused
   */
  public abstract boolean addMemberNode(Node node);

  /**
   * Remove a member node
   *
   * @param node to remove
   * @param removeDependentReferences when true, the approach segments ending at the node and the internal segments
   *                                  touching it are removed as well; when false only the node is removed, leaving
   *                                  those references to be removed by other changes
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
   * Approach segments of this intersection: the link segments entering it. Every way out of the intersection is allowed
   * from an approach segment unless a banned movement of the layer, starting from it, forbids it
   *
   * @return unmodifiable list of approach segments
   */
  public abstract List<MacroscopicLinkSegment> getApproachSegments();

  /**
   * Approach segment with the given id
   *
   * @param segmentId to look for
   * @return approach segment, null when no approach segment has this id
   */
  public abstract MacroscopicLinkSegment getApproachSegment(long segmentId);

  /**
   * Add an approach segment: a link segment that ends at a member node and starts at a node that is not a member
   *
   * @param segment to add
   * @return true when added, false when already an approach segment or refused because it does not enter the
   * intersection
   */
  public abstract boolean addApproachSegment(MacroscopicLinkSegment segment);

  /**
   * Remove an approach segment
   *
   * @param segment to remove
   * @return true when removed, false when not an approach segment
   */
  public abstract boolean removeApproachSegment(MacroscopicLinkSegment segment);

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
   * Verify if this intersection is signalised
   *
   * @return true when signalised, false otherwise
   */
  public default boolean isSignalised(){
    return getControlType() == IntersectionControlType.SIGNALISED;
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
