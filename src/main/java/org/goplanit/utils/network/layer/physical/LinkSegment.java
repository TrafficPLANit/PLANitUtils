package org.goplanit.utils.network.layer.physical;

import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.id.IdGenerator;
import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLink;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
   * Generate unique link segment id
   * 
   * @param groupId, contiguous id generation within this group for instances of this class
   * @return id of this link segment
   */
  public default long generateLinkSegmentId(final IdGroupingToken groupId) {
    return IdGenerator.generateId(groupId, getLinkSegmentIdClass());
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
   * Returns whether vehicles of a specified mode are allowed through this link
   *
   * @param mode the specified mode
   * @return true if vehicles of this mode can drive along this link, false otherwise
   */
  public abstract boolean isModeAllowed(Mode mode);

  /**
   * Returns the modes that are allowed on the link segment (unmodifiable)
   *
   * @return allowed modes
   */
  public abstract Set<Mode> getAllowedModes();

  /** collect the allowed modes from the passed in modes as newly created set
   *
   * @param modes to choose from
   * @return allowed modes
   */
  public default Set<Mode> getAllowedModesFrom(Collection<Mode> modes){
    Set<Mode> allowedModes = new HashSet<>();
    for(Mode mode : modes) {
      if(isModeAllowed(mode)) {
        allowedModes.add(mode);
      }
    }
    return allowedModes;
  }

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
   * @return this link segment
   */
  public abstract LinkSegment setNumberOfLanes(int numberOfLanes);
  
  /**
   * This is the maximum speed that is physically present and a driver can observe from the signs on the road (km/h)
   * 
   * @param maximumSpeedKmH to set
   * @return this linkSegment
   */
  public abstract  LinkSegment setPhysicalSpeedLimitKmH(double maximumSpeedKmH);

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
  public abstract Link getParent();
  
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
   * Verify if downstream node matches given node
   *
   * @param node to check
   * @return true if equal, false otherwise
   */
  public default boolean isDownstreamNode(Node node){
    return getDownstreamNode().equals(node);
  }

  /**
   * Verify if node matches any extreme node of link segment
   *
   * @param node to check
   * @return true if present, false otherwise
   */
  public default boolean hasNode(Node node){
    return isDownstreamNode(node) || isUpstreamNode(node);
  }

  /**
   * Verify if upstream node matches given node
   *
   * @param node to check
   * @return true if equal, false otherwise
   */
  public default boolean isUpstreamNode(Node node){
    return getUpstreamNode().equals(node);
  }

  /**
   * {@inheritDoc}
   */
  public abstract LinkSegment shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract LinkSegment deepClone();
  
  /**
   * Return the parent link of this link segment
   * 
   * @return Link object which is the parent of this link segment
   */
  public default Link getParentLink() {
    return getParent();
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
