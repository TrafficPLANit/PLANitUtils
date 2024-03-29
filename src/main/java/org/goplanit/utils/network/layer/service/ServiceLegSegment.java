package org.goplanit.utils.network.layer.service;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.network.layer.physical.Link;
import org.goplanit.utils.network.layer.physical.LinkSegment;
import org.locationtech.jts.geom.LineString;

import java.util.List;

/**
 * Interface for directed ServiceLegSegment part of non-directional ServiceLeg.
 * 
 * @author markr
 *
 */
public interface ServiceLegSegment extends EdgeSegment {
  
  /**
   * Return the parent leg of this leg segment
   * 
   * @return leg instance which is the parent of this leg segment
   */
  @Override
  public ServiceLeg getParent();

  /**
   * Collect the geometry of this service leg segment. Because
   * service leg segments comprise one or more physical link segments, they may have unique geometry compared
   * to their opposite direction counterpart. Therefore, they are expected to be able to provide their own
   * unique access to their geometry rather than rely on their parent, also this may be generated on the fly rather than stored
   * on the instance
   *
   * @return lineString
   */
  public abstract LineString getGeometry();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ServiceLegSegment shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract ServiceLegSegment deepClone();

  /**
   * Get the segment's upstream service node
   *
   * @return upstream vertex
   */
  public default ServiceNode getUpstreamServiceNode() {
    return isDirectionAb() ? getParent().getServiceNodeA() : getParent().getServiceNodeB();
  }

  /**
   * Get the segment's downstream vertex
   *
   * @return downstream service node
   */
  public default ServiceNode getDownstreamServiceNode() {
    return isDirectionAb() ? getParent().getServiceNodeB() : getParent().getServiceNodeA();
  }

  public default boolean hasGeometry() {
    if(getPhysicalParentSegments() == null || getPhysicalParentSegments().isEmpty()){
      return false;
    }
    return getPhysicalParentSegments().stream().allMatch(ls -> ls.hasGeometry());
  }

  /** Collect the links that make up this leg ordered and in direction from A to B
   *
   * @return parent links this leg represents
   */
  public abstract List<? extends LinkSegment> getPhysicalParentSegments();

  /**
   * Set the network layer links that make up this leg irrespective wether they have been set before. Use with caution
   * @param networkLayerLinkSegments to use
   */
  public abstract void setPhysicalParentSegments(final List<? extends LinkSegment> networkLayerLinkSegments);

  /** verify if any physical parent leg segments are registered for this service leg segment
   *
   * @return true when present false otherwise
   */
  public default  boolean hasPhysicalParentSegments(){
    return !(getPhysicalParentSegments()==null || getPhysicalParentSegments().isEmpty());
  }

  /** Collect the first physical link underpinning the service leg route
   *
   * @return first parent link
   */
  public default LinkSegment getFirstPhysicalLinkSegment() {
    return getPhysicalParentSegments().get(0);
  }

  /** Collect the last parent link
   *
   * @return last parent link
   */
  public default LinkSegment getLastPhysicalLinkSegment() {
    return getPhysicalParentSegments().get(getPhysicalParentSegments().size()-1);
  }

}
