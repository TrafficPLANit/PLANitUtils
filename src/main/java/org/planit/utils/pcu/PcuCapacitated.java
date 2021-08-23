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

  /** Determine the capacity in PCU/hour
   * 
   * @return capacity in PCY/h
   */
  public abstract double computeCapacityPcuH();
}
