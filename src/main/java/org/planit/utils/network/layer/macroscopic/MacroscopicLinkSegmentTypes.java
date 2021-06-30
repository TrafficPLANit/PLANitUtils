package org.planit.utils.network.layer.macroscopic;

import java.util.Map;
import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.mode.Mode;
import org.planit.utils.wrapper.LongMapWrapper;

/**
 * A container interface for macroscopic link segment types
 * 
 * @author markr
 *
 */
public interface MacroscopicLinkSegmentTypes extends LongMapWrapper<MacroscopicLinkSegmentType> {

  /**
   * Create and register new macroscopic link segment type on network.
   *
   * @param name                   name of the link segment type
   * @param capacityPcuPerHour     capacity of the link segment type
   * @param maximumDensityPcuPerKm maximum density of the link segment type
   * @param modeProperties         mode properties of the link segment type
   * @return the link segment type
   * @throws PlanItException thrown if there is an error
   */
  public MacroscopicLinkSegmentType createAndRegisterNew(final String name, final double capacityPcuPerHour, final double maximumDensityPcuPerKm,
      final Map<Mode, MacroscopicModeProperties> modeProperties) throws PlanItException;
  
  /**
   * Create and register new macroscopic link segment type on network. No mode properties will be set (null)
   *
   * @param name                   name of the link segment type
   * @param capacityPcuPerHour     capacity of the link segment type
   * @param maximumDensityPcuPerKm maximum density of the link segment type
   * @return the link segment type
   * @throws PlanItException thrown if there is an error
   */
  public abstract MacroscopicLinkSegmentType createAndRegisterNew(final String name, final double capacityPcuPerHour, final double maximumDensityPcuPerKm) throws PlanItException;
    
  /**
   * Create a unique copy (in terms of id) of the passed in link segment type and register it. All exogenous relations (to parent link, nodes, etc.) remain unchanged
   * 
   * @param linkSegmentTypeToCopy to register unique copy of
   * @return registered unique copy
   */
  public abstract MacroscopicLinkSegmentType registerUniqueCopyOf(final MacroscopicLinkSegmentType linkSegmentTypeToCopy);  
    
  /**
   * Return a MacroscopicLinkSegmentType by its Xml id
   * 
   * @param xmlId the XML id of the MacroscopicLinkSegmentType
   * @return the specified MacroscopicLinkSegmentType instance
   */
  public abstract MacroscopicLinkSegmentType getByXmlId(String xmlId);  
       
  /** collect the first entry that would be returned by the iterator
   * @return first entry
   */
  public abstract MacroscopicLinkSegmentType getFirst();

}
