package org.goplanit.utils.network.layer.physical;

import java.util.Collection;

import org.goplanit.utils.graph.directed.DirectedEdge;
import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.macroscopic.MacroscopicLinkSegment;

/**
 * Link interface which extends the Edge interface with a unique id (not all edges are links) as
 * well as an external id
 * 
 * @author markr
 *
 */
public interface Link extends DirectedEdge {
  
  /** id class for generating ids */
  public static final Class<Link> LINK_ID_CLASS = Link.class;   
  
  /**
   * Return id of this instance. This id is expected to be generated using the
   * org.planit.utils.misc.IdGenerator
   * 
   * @return linkId
   */
  public abstract long getLinkId();   
    
  /**
   * Return class used to generate unique link ids via the id generator
   * 
   * @return class type
   */
  public default Class<? extends Link> getLinkIdClass(){
    return LINK_ID_CLASS;
  }

  /** collect vertex A as something extending node which is to be expected for any link. Convenience method
   * for readability
   * 
   * @param <N> node type
   * @return nodeA
   */
  @SuppressWarnings("unchecked")
  public default <N extends Node> N getNodeA() {
    return (N) getVertexA();
  }
  
  /** collect vertex A as something extending node which is to be expected for any link. Convenience method
   * for readability
   * 
   * @param <N> node type
   * @return nodeA
   */
  @SuppressWarnings("unchecked")
  public default <N extends Node> N getNodeB() {
    return (N) getVertexB();
  }  
  
  /** collect edgeSegment as something extending LinkSegment which is to be expected for any link. Convenience method
   * for readability
   *
   * @param directionAb the direction
   * @return link segment in given direction
   */
  @SuppressWarnings("unchecked")
  public default LinkSegment getLinkSegment(boolean directionAb) {
    return (LinkSegment) getEdgeSegment(directionAb);
  }   
  
  /** collect edgeSegment Ab as something extending LinkSegment which is to be expected for any link. Convenience method
   * for readability
   *
   * @return link segment in given direction
   */
  public default LinkSegment getLinkSegmentAb() {
    return getLinkSegment(true);
  }   
  
  /** verify if linkSegment Ab is present
   * 
   * @return true when link segment is present, false otherwise
   */
  public default boolean hasLinkSegmentAb() {
    return hasEdgeSegmentAb();
  }   
  
  /** collect edgeSegment Ba as something extending LinkSegment which is to be expected for any link.
   * Convenience method for readability
   *
   * @return link segment in given direction
   */
  public default LinkSegment getLinkSegmentBa() {
    return getLinkSegment(false);
  } 
  
  /** verify if linkSegment Ba is present
   * 
   * @return true when link segment is present, false otherwise
   */
  public default boolean hasLinkSegmentBa() {
    return hasEdgeSegmentBa();
  }

  /** verify if name is present on link
   * @return true when present, false otherwise
   */
  public default boolean hasName() {
    return getName()!=null && !getName().isBlank();
  }

  /** collect all available link segments of this link
   * @return available link segments
   */
  @SuppressWarnings("unchecked")
  public default Collection<? extends LinkSegment> getLinkSegments(){
    return (Collection<LinkSegment>) getEdgeSegments();
  }

  /**
   * Verify if any given mode is allowed on any of the two segments, where at least one segment must be registered to
   * allow for a positive result
   *
   * @param modes to check
   * @return true when a segment allows the mode, false otherwise
   */
  public default boolean isAnyModeAllowedOnAnySegment(Collection<Mode> modes){
    return modes.stream().anyMatch(this::isModeAllowedOnAnySegment);
  }

  /**
   * Verify if given mode is allowed on any of the two segments, where at least one segment must be registered to
   * allow for a positive result
   *
   * @param mode to check
   * @return true when a segment allows the mode, false otherwise
   */
  public default boolean isModeAllowedOnAnySegment(Mode mode){
    return (hasLinkSegmentBa() || hasLinkSegmentBa()) &&
            ((hasLinkSegmentBa() && getLinkSegmentBa().isModeAllowed(mode)) ||
                (hasLinkSegmentAb() && getLinkSegmentAb().isModeAllowed(mode)));
  }

  /**
   * Verify if given mode is allowed on both segments, where at least one segment must be registered to allow for
   * a positive result
   *
   * @param mode to check
   * @return true both segments allow the mode, false otherwise
   */
  public default boolean isModeAllowedOnAllSegments(Mode mode){
    return (hasLinkSegmentBa() || hasLinkSegmentBa()) &&
            ((!hasLinkSegmentBa() || getLinkSegmentBa().isModeAllowed(mode)) &&
                (!hasLinkSegmentAb() || getLinkSegmentAb().isModeAllowed(mode)));
  }

  /**
   * Verify if all modes are allowed on both segments, where at least one segment must be registered to
   * allow for a positive result
   *
   * @param modes to check
   * @return true when a segment allows the mode, false otherwise
   */
  public default boolean isAllModesAllowedOnAllSegments(Collection<Mode> modes){
    return modes.stream().allMatch(this::isModeAllowedOnAllSegments);
  }

  /** Collect the one way link segment for the mode if the link is in fact one way.
   * If it is not (for the mode), null is returned
   *
   * @param mode to check one-way characteristic
   * @return edge segment that is one way for the mode, i.e., the other edge segment (if any) does not
   * support this mode, null if this is not the case
   */
  public default LinkSegment getLinkSegmentIfLinkIsOneWayForMode(Mode mode) {
    LinkSegment segment = null;
    if(hasEdgeSegmentAb() != hasEdgeSegmentBa()) {
      /* link is one way across all modes */
      segment = hasEdgeSegmentAb() ? getLinkSegmentAb() : getLinkSegmentBa();
      segment = segment.isModeAllowed(mode) ? segment : null;
    }else if(getLinkSegmentAb().isModeAllowed(mode) != getLinkSegmentBa().isModeAllowed(mode)) {
      /* link is one way for our mode */
      segment = getLinkSegmentAb().isModeAllowed(mode) ? getLinkSegmentAb() : getLinkSegmentBa();
    }

    return segment;
  }


  /**
   * {@inheritDoc}
   */
  public abstract Link shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Link deepClone();
  
}
