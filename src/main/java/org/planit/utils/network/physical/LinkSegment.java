package org.planit.utils.network.physical;

import java.util.Set;

import org.planit.utils.graph.EdgeSegment;
import org.planit.utils.mode.Mode;

public interface LinkSegment extends EdgeSegment {

  /**
   * Default number of lanes
   */
  short DEFAULT_NUMBER_OF_LANES = 1;
  /**
   * Default maximum speed on a link segment in km/h
   */
  double DEFAULT_MAX_SPEED = 130;

  /**
   * Default maximum link density in pcu/km
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
   * This is the maximum speed that is physically present and a driver can observe from the signs on the road (km/h)
   * 
   * @param maximumSpeedKmH
   */
  public void setMaximumSpeedKmH(double maximumSpeedKmH);

  /**
   * This is the maximum speed (Km/h) that is physically present and a driver can observe from the signs on the road
   * 
   * @param maximumSpeedKmH
   */
  public double getMaximumSpeedKmH(); 

  /**
   * Return the parent link of this link segment
   * 
   * @return Link object which is the parent of this link segment
   */
  public Link getParentLink();

  /**
   * Returns whether vehicles of a specified mode are allowed through this link
   * 
   * @param mode the specified mode
   * @return true if vehicles of this mode can drive along this link, false otherwise
   */
  public boolean isModeAllowed(Mode mode);
  
  /**
   * Returns the modes that are allowed on the link segment
   * 
   * @return allowed modes
   */
  public Set<Mode> getAllowedModes();

}
