package org.planit.utils.network.layer.macroscopic;

import java.util.Collection;
import org.planit.utils.id.ContainerisedManagedIdEntityFactory;
import org.planit.utils.mode.Mode;

/** Factory to create link segments and register them on its container
 * 
 * @author markr
 *
 */
public interface MacroscopicLinkSegmentTypeFactory extends ContainerisedManagedIdEntityFactory<MacroscopicLinkSegmentType>{
  
  /**
   * Create and register new macroscopic link segment type on network.
   *
   * @param name                   name of the link segment type
   * @param capacityPcuPerHour     capacity of the link segment type
   * @param maximumDensityPcuPerKm maximum density of the link segment type
   * @param allowedModes  create default group access properties for all allowed modes based on the defaults for access (Rather than the mode)
   */
  public MacroscopicLinkSegmentType registerNew(final String name, final double capacityPcuPerHour, final double maximumDensityPcuPerKm,
      final Mode... allowedModes);  
  
  /**
   * Create and register new macroscopic link segment type on network.
   *
   * @param name                   name of the link segment type
   * @param capacityPcuPerHour     capacity of the link segment type
   * @param maximumDensityPcuPerKm maximum density of the link segment type
   * @param groupAccessProperties  mode properties of the link segment type
   * @return the link segment type
   */
  public MacroscopicLinkSegmentType registerNew(final String name, final double capacityPcuPerHour, final double maximumDensityPcuPerKm,
      final Collection<AccessGroupProperties> groupAccessProperties);
  
  /**
   * Create and register new macroscopic link segment type on network.
   *
   * @param name                   name of the link segment type
   * @param capacityPcuPerHour     capacity of the link segment type
   * @param maximumDensityPcuPerKm maximum density of the link segment type
   * @param groupAccessProperties  mode properties of the link segment type
   * @return the link segment type
   */
  public MacroscopicLinkSegmentType registerNew(final String name, final double capacityPcuPerHour, final double maximumDensityPcuPerKm,
      final AccessGroupProperties groupAccessProperties);  
  
  /**
   * Create and register new macroscopic link segment type on network. No mode properties will be set (null)
   *
   * @param name                   name of the link segment type
   * @param capacityPcuPerHour     capacity of the link segment type
   * @param maximumDensityPcuPerKm maximum density of the link segment type
   * @return the link segment type
   */
  public abstract MacroscopicLinkSegmentType registerNew(final String name, final double capacityPcuPerHour, final double maximumDensityPcuPerKm);

}
