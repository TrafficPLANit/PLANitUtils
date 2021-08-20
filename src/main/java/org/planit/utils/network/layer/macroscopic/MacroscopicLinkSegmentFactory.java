package org.planit.utils.network.layer.macroscopic;

import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.graph.GraphEntityFactory;
import org.planit.utils.network.layer.physical.Link;

/** Factory to create link segments and register them on its container
 * 
 * @author markr
 *
 */
public interface MacroscopicLinkSegmentFactory extends GraphEntityFactory<MacroscopicLinkSegment>{
  
  /**
   * Create macroscopic link segment, do not register nor register on nodes and link
   *
   * @param parentLink  the parent of this segment
   * @param directionAB direction of travel
   * @return the created segment
   * @throws PlanItException thrown if error
   */
  public MacroscopicLinkSegment create(final Link parentLink, final boolean directionAB) throws PlanItException;

  /**
   * Create a macroscopic link segment and register it
   *
   * @param parentLink            the parent of this segment
   * @param directionAb           direction of travel
   * @param registerOnNodeAndLink option to register the new segment on the underlying link and its nodes
   * @return the created segment
   * @throws PlanItException thrown if error
   */
  public MacroscopicLinkSegment registerNew(Link parentLink, boolean directionAb, boolean registerOnNodeAndLink) throws PlanItException;
  
  /**
   * Create a macroscopic link segment and register it
   *
   * @param parentLink            the parent of this segment
   * @param type                  the type of the link segment
   * @param directionAb           direction of travel
   * @param registerOnNodeAndLink option to register the new segment on the underlying link and its nodes
   * @return the created segment
   * @throws PlanItException thrown if error
   */
  public MacroscopicLinkSegment registerNew(Link parentLink, MacroscopicLinkSegmentType type, boolean directionAb, boolean registerOnNodeAndLink) throws PlanItException;  

}
