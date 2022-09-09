package org.goplanit.utils.network.layer.macroscopic;

import org.goplanit.utils.mode.Mode;
import org.goplanit.utils.network.layer.physical.Link;
import org.goplanit.utils.network.layer.physical.LinkSegment;

import java.util.Collection;

/**
 * Macroscopic Link interface which extends the Link interface with additional mode specific functionality
 * 
 * @author markr
 *
 */
public interface MacroscopicLink extends Link {

  /** collect edgeSegment as something extending LinkSegment which is to be expected for any link. Convenience method
   * for readability
   *
   * @param directionAb the direction
   * @return link segment in given direction
   */
  @SuppressWarnings("unchecked")
  @Override
  public default MacroscopicLinkSegment getLinkSegment(boolean directionAb) {
    return (MacroscopicLinkSegment) Link.super.getLinkSegment(directionAb);
  }   
  
  /** collect edgeSegment Ab as something extending LinkSegment which is to be expected for any link. Convenience method
   * for readability
   *
   * @return link segment in given direction
   */
  @Override
  public default MacroscopicLinkSegment getLinkSegmentAb() {
    return getLinkSegment(true);
  }   

  /** collect edgeSegment Ba as something extending LinkSegment which is to be expected for any link. Convenience method
   * for readability
   *
   * @return link segment in given direction
   */
  @Override
  public default MacroscopicLinkSegment getLinkSegmentBa() {
    return getLinkSegment(false);
  } 

  /** collect all available link segments of this link
   * @return available link segments
   */
  @SuppressWarnings("unchecked")
  @Override
  public default Collection<? extends MacroscopicLinkSegment> getLinkSegments(){
    return (Collection<MacroscopicLinkSegment>) getEdgeSegments();
  }

  /**
   * Verify if given mode is allowed on any of the two segments, where at least one segment must be registered to allow for a positive
   * result
   *
   * @param mode to check
   * @return true when a segment allows the mode, false otherwise
   */
  public default boolean isModeAllowedOnAnySegment(Mode mode){
    return (hasLinkSegmentBa() || hasLinkSegmentBa()) &&
        ((hasLinkSegmentBa() && getLinkSegmentBa().isModeAllowed(mode)) || (hasLinkSegmentAb() && getLinkSegmentAb().isModeAllowed(mode)));
  }

  /**
   * Verify if given mode is allowed on both segments, where at least one segment must be registered to allow for a positive
   * result
   *
   * @param mode to check
   * @return true both segments allow the mode, false otherwise
   */
  public default boolean isModeAllowedOnAllSegments(Mode mode){
    return (hasLinkSegmentBa() || hasLinkSegmentBa()) &&
      ((!hasLinkSegmentBa() || getLinkSegmentBa().isModeAllowed(mode)) && (!hasLinkSegmentAb() || getLinkSegmentAb().isModeAllowed(mode)));
  }

  /** Collect the one way link segment for the mode if the link is in fact one way. If it is not (for the mode), null is returned
   *
   * @param mode to check one-way characteristic
   * @return edge segment that is one way for the mode, i.e., the other edge segment (if any) does not support this mode, null if this is not the case
   */
  public default  MacroscopicLinkSegment getLinkSegmentIfLinkIsOneWayForMode(Mode mode) {
    MacroscopicLinkSegment segment = null;
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
  
}
