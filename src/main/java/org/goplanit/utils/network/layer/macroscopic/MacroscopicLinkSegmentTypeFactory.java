package org.goplanit.utils.network.layer.macroscopic;

import org.goplanit.utils.id.ManagedIdEntityFactory;
import org.goplanit.utils.mode.Mode;

/** Factory to create link segments and register them on its container
 * 
 * @author markr
 *
 */
public interface MacroscopicLinkSegmentTypeFactory extends ManagedIdEntityFactory<MacroscopicLinkSegmentType>{
      
  /**
   * Create and register new macroscopic link segment type on network. No mode properties will be set (null)
   *
   * @param name                   name of the link segment type
   * @param capacityPcuPerHour     capacity of the link segment type
   * @param maximumDensityPcuPerKm maximum density of the link segment type
   * @return the link segment type
   */
  public abstract MacroscopicLinkSegmentType registerNew(final String name, final double capacityPcuPerHour, final double maximumDensityPcuPerKm);

  /**
   * Create and register new macroscopic link segment type on network. Add default access for a single mode
   *
   * @param name                   name of the link segment type
   * @param capacityPcuPerHour     capacity of the link segment type
   * @param maximumDensityPcuPerKm maximum density of the link segment type
   * @param allowedMode the allowed mode
   * @return the link segment type
   */  
  public abstract MacroscopicLinkSegmentType registerNew(final String name, final double capacityPcuPerHour, final double maximumDensityPcuPerKm, final Mode allowedMode);
  
  /**
   * Create and register new macroscopic link segment type on network without explicitly setting capacity, max density, nor access group properties.
   *
   * @param name                   name of the link segment type
   * @return the link segment type
   */
  public abstract MacroscopicLinkSegmentType registerNew(final String name); 
  
  /**
   * Create and register new macroscopic link segment type on network without explicitly setting capacity, nor access group properties.
   *
   * @param name                   name of the link segment type
   * @param capacityPcuPerHour     capacity of the link segment type
   * @return the link segment type
   */
  public abstract MacroscopicLinkSegmentType registerNewWithCapacity(final String name, final double capacityPcuPerHour);
  
  /**
   * Create and register new macroscopic link segment type on network without explicitly setting capacity, nor accessgroup properties.
   *
   * @param name                   name of the link segment type
   * @param maximumDensityPcuPerKm maximum density of the link segment type
   * @return the link segment type
   */
  public abstract MacroscopicLinkSegmentType registerNewWithMaxDensity(final String name, final double maximumDensityPcuPerKm);
 

}
