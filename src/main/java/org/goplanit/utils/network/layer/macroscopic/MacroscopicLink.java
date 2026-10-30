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
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicLink shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicLink deepClone();
}
