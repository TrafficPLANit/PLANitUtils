package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.EdgeSegment;

/**
 * Interface for link segments (directional) part of link (non-directional).
 * 
 * @author markr
 *
 */
public interface LinkSegment extends EdgeSegment {
  
  /** additional id class for generating link segment ids */
  public static final Class<LinkSegment> LINK_SEGMENT_ID_CLASS = LinkSegment.class;   
  
  /**
   * Return class used to generate unique link ids via the id generator
   * 
   * @return class type
   */
  public default Class<? extends LinkSegment> getLinkSegmentIdClass(){
    return LINK_SEGMENT_ID_CLASS;
  }  

  /**
   * Default number of lanes
   */
  public final short DEFAULT_NUMBER_OF_LANES = 1;
  /**
   * Default maximum speed on a link segment in km/h
   */
  public final double DEFAULT_MAX_SPEED = 130;

  /**
   * Default maximum link density in pcu/km
   */
  public final double MAXIMUM_DENSITY = 180;

  /**
   * Return id of this instance. This id is expected to be generated using the
   * org.planit.utils.misc.IdGenerator
   * 
   * @return link segment id
   */
  public abstract long getLinkSegmentId();

  /**
   * Collect the number of lanes of this segment
   * 
   * @return number of lanes
   */
  public abstract int getNumberOfLanes();

  /**
   * Set the number of lanes
   * 
   * @param numberOfLanes to set
   */
  public abstract void setNumberOfLanes(int numberOfLanes);
  
  /**
   * This is the maximum speed that is physically present and a driver can observe from the signs on the road (km/h)
   * 
   * @param maximumSpeedKmH to set
   */
  public abstract  void setPhysicalSpeedLimitKmH(double maximumSpeedKmH);

  /**
   * This is the maximum speed (Km/h) that is physically present and a driver can observe from the signs on the road
   * 
   * @return maximumSpeedKmH
   */
  public abstract double getPhysicalSpeedLimitKmH(); 
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Link getParentEdge();
  
  /**
   * {@inheritDoc}
   */
  @Override  
  public abstract Node getUpstreamVertex();
  
  /**
   * {@inheritDoc}
   */
  @Override  
  public abstract Node getDownstreamVertex();
  
  /**
   * Return the parent link of this link segment
   * 
   * @return Link object which is the parent of this link segment
   */
  public default Link getParentLink() {
    return getParentEdge();
  }

  /** Collect upstream vertex as node
   * 
   * @return upstream node
   */  
  public default Node getUpstreamNode() {
    return getUpstreamVertex();
  }
  
  /** Collect downstream vertex as node
   * 
   * @return downstream node
   */
  public default Node getDownstreamNode() {
    return getDownstreamVertex();
  }  

}