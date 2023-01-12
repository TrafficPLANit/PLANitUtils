package org.goplanit.utils.network.layer.service;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.network.layer.physical.Link;

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
  public abstract List<Link> getPhysicalParentSegments();

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
  public default Link getFirstPhysicalLink() {
    return getPhysicalParentSegments().get(0);
  }

  /** Collect the last parent link
   *
   * @return last parent link
   */
  public default Link getLastPhysicalLink() {
    return getPhysicalParentSegments().get(getPhysicalParentSegments().size()-1);
  }

}
