package org.planit.utils.pcu;

/**
 * Interface that signifies implementing classes adopt some form of capacitated restriction based on PCUs (passenger car units), these can be
 * links, link segments, or other entities that are otherwise not necessarily related in terms of their class hierarchy, but do support this 
 * capacitated feature. 
 * 
 * @author markr
 *
 */
public interface PcuCapacitated {

  /** Collect the explicitly available/set capacity in PCU/hour. In case no explicit capacity is provided a default is to be returned
   * 
   * @return capacity in PCu/h
   */
  public abstract double getCapacityOrDefaultPcuH();
  
  /** Collect the explicitly available/set capacity in PCU/hour/lane. In case no explicit capacity is provided a default is to be returned
   * 
   * @return capacity in PCu/h
   */
  public abstract double getCapacityOrDefaultPcuHLane();
      
}
