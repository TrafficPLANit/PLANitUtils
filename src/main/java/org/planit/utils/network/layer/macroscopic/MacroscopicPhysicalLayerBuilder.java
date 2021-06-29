package org.planit.utils.network.layer.macroscopic;

import java.util.Map;

import org.planit.utils.mode.Mode;
import org.planit.utils.network.layer.physical.Link;
import org.planit.utils.network.layer.physical.Node;
import org.planit.utils.network.layer.physical.PhysicalNetworkLayerBuilder;

/**
 * Create network entities for a macroscopic simulation model using Nodes, Links, and MacroscopicLinkSegments
 * 
 * @author markr
 *
 */
public interface MacroscopicPhysicalLayerBuilder<N extends Node, L extends Link, MLS extends MacroscopicLinkSegment> extends PhysicalNetworkLayerBuilder<N, L, MLS> {

  /**
   * Create a fully functional macroscopic link segment type instance
   * 
   * @param name           the name of this link type
   * @param capacity       the capacity of this link type
   * @param maximumDensity the maximum density of this link type
   * @param modeProperties the mode properties for each mode along this link
   * @return macroscopicLinkSegmentType the created link segment type
   */
  public MacroscopicLinkSegmentType createLinkSegmentType(String name, double capacity, double maximumDensity, Map<Mode, MacroscopicModeProperties> modeProperties);

  /**
   * Create a macroscopic link segment type instance without mode properties
   * 
   * @param name           the name of this link type
   * @param capacity       the capacity of this link type
   * @param maximumDensity the maximum density of this link type
   * @return macroscopicLinkSegmentType the created link segment type
   */
  public MacroscopicLinkSegmentType createLinkSegmentType(String name, double capacity, double maximumDensity);

  /**
   * create a copy of the passed in link segment type where only the unique identifiers are different
   * 
   * @param linkSegmentTypeToCopy to create a unique copy of
   * @return copy of passed on link segment type with unique id
   */
  public MacroscopicLinkSegmentType createUniqueCopyOf(MacroscopicLinkSegmentType linkSegmentTypeToCopy);

}
