package org.planit.utils.network.physical.macroscopic;

import org.planit.exceptions.PlanItException;
import org.planit.utils.network.physical.LinkSegment;
import org.planit.utils.network.physical.Mode;

/**
 * Macroscopic traffic modeling oriented link segment
 * 
 * @author markr
 *
 */
public interface MacroscopicLinkSegment extends LinkSegment {

  /**
   * Return the total link segment capacity in pcu/h
   * 
   * @return linkSegmentCapacity in PCU/h
   */
  double computeCapacity();

  /**
   * Compute the free flow travel time by mode, i.e. when the link's maximum speed
   * might be capped by the mode's maximum speed
   * 
   * If the input data are invalid, this method logs the problem and returns a
   * negative value.
   * 
   * @param mode mode of travel
   * @return freeFlowTravelTime for this mode
   * @throws PlanItException thrown if error
   */
  double computeFreeFlowTravelTime(Mode mode) throws PlanItException;

  /**
   * Set the link segment type this link segment adheres to
   * 
   * @param linkSegmentType the link segment type
   */
  void setLinkSegmentType(MacroscopicLinkSegmentType linkSegmentType);

  /**
   * Collect the link segment type of the link segment
   * 
   * @return the link segment
   */
  MacroscopicLinkSegmentType getLinkSegmentType();

}
