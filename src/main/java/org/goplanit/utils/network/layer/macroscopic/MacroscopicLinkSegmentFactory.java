package org.goplanit.utils.network.layer.macroscopic;

import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.graph.GraphEntityFactory;
import org.goplanit.utils.misc.Pair;
import org.goplanit.utils.network.layer.physical.Link;

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
  public MacroscopicLinkSegment create(final MacroscopicLink parentLink, final boolean directionAB) throws PlanItException;
  
  /**
   * Create macroscopic link segments in both directions and register them
   *
   * @param parentLink            the parent of this segment
   * @param registerOnLink option to register the new segment on the underlying link
   * @return the created segments as a pair with direction (Ab,Ba)
   */
  public Pair<MacroscopicLinkSegment,MacroscopicLinkSegment> registerNew(MacroscopicLink parentLink, boolean registerOnLink);

  /**
   * Create a macroscopic link segment and register it
   *
   * @param parentLink            the parent of this segment
   * @param directionAb           direction of travel
   * @param registerOnLink option to register the new segment on the underlying link
   * @return the created segment
   */
  public MacroscopicLinkSegment registerNew(MacroscopicLink parentLink, boolean directionAb, boolean registerOnLink);
  
  /**
   * Create a macroscopic link segment and register it
   *
   * @param parentLink            the parent of this segment
   * @param type                  the type of the link segment
   * @param directionAb           direction of travel
   * @param registerOnLink option to register the new segment on the underlying link
   * @return the created segment
   */
  public MacroscopicLinkSegment registerNew(
          MacroscopicLink parentLink, MacroscopicLinkSegmentType type, boolean directionAb, boolean registerOnLink);

}
