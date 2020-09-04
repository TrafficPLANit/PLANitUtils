package org.planit.utils.network.physical;

import org.planit.utils.graph.EdgeSegment;

public interface LinkSegment extends EdgeSegment {

  /**
   * Default number of lanes
   */
  short DEFAULT_NUMBER_OF_LANES = 1;
  /**
   * Default maximum speed on a link segment
   */
  double DEFAULT_MAX_SPEED = 130;

  /**
   * Default maximum link density
   */
  double MAXIMUM_DENSITY = 180;

  /**
   * Return id of this instance. This id is expected to be generated using the
   * org.planit.utils.misc.IdGenerator
   * 
   * @return link segment id
   */
  long getLinkSegmentId();

  /**
   * Collect the number of lanes of this segment
   * 
   * @return number of lanes
   */
  int getNumberOfLanes();

  /**
   * Set the number of lanes
   * 
   * @param numberOfLanes to set
   */
  void setNumberOfLanes(int numberOfLanes);
  
  /**
   * This is the maximum speed that is physically present and a driver can observe from the signs on the road
   * 
   * @param maximumSpeed
   */
  public void setMaximumSpeed(double maximumSpeed);

  /**
   * This is the maximum speed that is physically present and a driver can observe from the signs on the road
   * 
   * @param maximumSpeed
   */
  public double getMaximumSpeed(); 

  /**
   * Return the parent link of this link segment
   * 
   * @return Link object which is the parent of this link segment
   */
  Link getParentLink();

  /**
   * Returns whether vehicles of a specified mode are allowed through this link
   * 
   * @param mode the specified mode
   * @return true if vehicles of this mode can drive along this link, false otherwise
   */
  boolean isModeAllowed(Mode mode);

}
