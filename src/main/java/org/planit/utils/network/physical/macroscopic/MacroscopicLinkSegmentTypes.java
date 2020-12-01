package org.planit.utils.network.physical.macroscopic;

import java.util.Map;
import java.util.Set;
import org.planit.utils.exceptions.PlanItException;
import org.planit.utils.mode.Mode;

/**
 * A container interface for macroscopic link segment types
 * 
 * @author markr
 *
 */
public interface MacroscopicLinkSegmentTypes extends Iterable<MacroscopicLinkSegmentType> {

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
  public MacroscopicLinkSegmentType createAndRegisterNew(final String name, final double capacityPcuPerHour, final double maximumDensityPcuPerKm) throws PlanItException;
  
  /**
   * Register a link segment type
   *
   * @param linkSegmentType the MacroscopicLinkSegmentType to be registered
   * @return the registered link segment type
   */
  public MacroscopicLinkSegmentType register(final MacroscopicLinkSegmentType linkSegmentType);
  
  /**
   * Create a unique copy (in terms of id) of the passed in link segment type and register it. All exogenous relations (to parent link, nodes, etc.) remain unchanged
   * 
   * @param linkSegmentTypeToCopy to register unique copy of
   * @return registered unique copy
   */
  public MacroscopicLinkSegmentType registerUniqueCopyOf(final MacroscopicLinkSegmentType linkSegmentTypeToCopy);  
  
  /**
   * Return number of registered link segment types
   *
   * @return number of registered link segment types
   */
  public int size();

  /**
   * Return a MacroscopicLinkSegmentType by its id
   * 
   * @param id the id of the MacroscopicLinkSegmentType
   * @return the specified MacroscopicLinkSegmentType instance
   * 
   */
  public MacroscopicLinkSegmentType get(long id);
       
  /**
   * Provide all link segment types as a set. This collection is a copy so any changes have no impact on the internally registered modes
   * @return all registered modes
   */
  public Set<MacroscopicLinkSegmentType> setOf();

}
