package org.goplanit.utils.network.layer.macroscopic;

import org.goplanit.utils.graph.ManagedGraphEntities;
import org.goplanit.utils.mode.Mode;

/**
 * Primary managed container for MacroscopicLinkSegments explicitly and create them on the container via
 * its dedicated factory class
 * 
 * @author markr
 *
 */
public interface MacroscopicLinkSegments extends ManagedGraphEntities<MacroscopicLinkSegment> {
  /* do not derive from LinkSegments since we require to override the factory method return type. This is only
   * allowed when the return type directly derives from the original return type. MacroscopicLinkSegmentsFactory cannot
   * derive from LinkSegmentFactory since the signature of the factory methods differs. Hence, we must derive from
   * the base interface instead which has an empty dummy factory return type which one can always overwrite and
   * the MacroscopicLinkSegmentFactory is derived from */
  
  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicLinkSegmentFactory getFactory();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicLinkSegments clone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract MacroscopicLinkSegments deepClone();
  
  /** Create a raw array of all free flow travel times of the registered macroscopic link segments where the index in the array corresponds
   * to the link segment id (not id). 
   * 
   * @param mode to use
   * @return free flow travel times for all link segments for the given mode
   */
  public default double[] getFreeFlowTravelTimeHourPerLinkSegment(Mode mode) {
    double[] linkSegmentFreeFlowTravelTimes = new double[size()];
    for(MacroscopicLinkSegment linkSegment : this){
      linkSegmentFreeFlowTravelTimes[(int) linkSegment.getLinkSegmentId()] = linkSegment.computeFreeFlowTravelTimeHour(mode);
    }
    return linkSegmentFreeFlowTravelTimes;
  }

}
