package org.planit.utils.mode;

import java.util.Set;

import org.planit.utils.exceptions.PlanItException;

/**
 * container class and factory methods for modes with some
 * 
 * @author markr
 *
 */
public interface Modes extends Iterable<Mode> {

  /**
   * Create and register new mode
   *
   * @param externalModeId the external mode id for the mode
   * @param name           of the mode
   * @param maxSpeed       maximum speed of the mode
   * @param pcu           value for the mode
   * @param physicalFeatures the physical features of this custom mode
   * @param usabilityFeatures the usability features of this custom mode
   * @return new mode created
   */
  public Mode registerNewCustomMode(
      final Object externalModeId, final String name, final double maxSpeed, double pcu, PhysicalModeFeatures physicalFeatures, UsabilityModeFeatures usabilityFeatures);
  
  /**
   * Create and register a new predefined mode. When it already exists, the existing entry is returned
   *
   * @param modeType the predefined mode type
   * @return new mode created, or existing mode when already present
   * @throws PlanItException thrown if error
   */
  public PredefinedMode registerNew(PredefinedModeType modeType) throws PlanItException;  

  /**
   * Return number of registered modes
   *
   * @return number of registered modes
   */
  public int size();

  /**
   * Return a Mode by its id
   * 
   * @param id the id of the Mode
   * @return the specified mode
   * 
   */
  public Mode get(long id);
  
  /** verify if predefined mode is registered 
   * 
   * @param modeType to verify
   * @return true when available, false otherwise
   */
  public boolean containsPredefinedMode(PredefinedModeType modeType);
  
  /** get predefined mode if it is registered 
   * 
   * @param modeType to collect
   * @return predefined mode when available, null otherwise
   */
  public PredefinedMode getPredefinedMode(PredefinedModeType modeType);
    
  /**
   * Collect the first registered mode
   * 
   * @return first registered mode if any
   */
  public Mode getFirst();

  /**
   * Retrieve a Mode by its external Id
   * 
   * This method has the option to convert the external Id parameter into a long value, to find the mode when mode objects use long values for external ids.
   * 
   * @param externalId    the external Id of the specified mode
   * @param convertToLong if true, the external Id is converted into a long before beginning the search
   * @return the retrieved mode, or null if no mode was found
   */
  public Mode getByExternalId(Object externalId, boolean convertToLong);

  /**
   * Retrieve a Mode by its external Id
   * 
   * This method is not efficient, since it loops through all the registered modes in order to find the required time period. The equivalent method in InputBuilderListener is
   * more efficient and should be used in preference to this in Java code.
   * 
   * @param externalId the external Id of the specified mode
   * @return the retrieved mode, or null if no mode was found
   */
  public Mode getByExternalId(Object externalId);
  
  /**
   * Provide all modes as a set. This collection is a copy so any changes have no impact on the internally registered modes
   * @return all registered modes
   */
  public Set<Mode> setOf();
}
