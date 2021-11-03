package org.goplanit.utils.mode;

import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.id.IdGroupingToken;
import org.goplanit.utils.id.ManagedIdEntityFactory;

/** Factory interface for creating mode instances
 * 
 * @author markr
 *
 */
public interface ModeFactory extends ManagedIdEntityFactory<Mode> {

  /**
   * Create and register new mode
   *
   * @param name           of the mode
   * @param maxSpeed       maximum speed of the mode
   * @param pcu           value for the mode
   * @param physicalFeatures the physical features of this custom mode
   * @param usabilityFeatures the usability features of this custom mode
   * @return new mode created
   */
  public abstract Mode registerNewCustomMode(final String name, final double maxSpeed, double pcu, PhysicalModeFeatures physicalFeatures, UsabilityModeFeatures usabilityFeatures);
  
  /**
   * Create and register a new predefined mode. When it already exists, the existing entry is returned
   *
   * @param modeType the predefined mode type
   * @return new mode created, or existing mode when already present
   * @throws PlanItException thrown if error
   */
  public abstract PredefinedMode registerNew(PredefinedModeType modeType) throws PlanItException;  
  
  /**
   * create a predefined mode instance without registering it on the container. Use with caution as it can mess up the internal id structure if not registered subsequently
   * 
   * @param groupId  the is generation token
   * @param modeType predefined mode type to create
   * 
   * @return predefined mode instance
   * @throws PlanItException thrown if error
   */
  public abstract PredefinedMode createPredefinedMode(IdGroupingToken groupId, final PredefinedModeType modeType) throws PlanItException;  
}
