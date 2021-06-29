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
   * @param name           of the mode
   * @param maxSpeed       maximum speed of the mode
   * @param pcu           value for the mode
   * @param physicalFeatures the physical features of this custom mode
   * @param usabilityFeatures the usability features of this custom mode
   * @return new mode created
   */
  public Mode registerNewCustomMode(final String name, final double maxSpeed, double pcu, PhysicalModeFeatures physicalFeatures, UsabilityModeFeatures usabilityFeatures);
  
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
  
  /** get predefined mode if it is registered 
   * 
   * @param modeType to collect
   * @return predefined mode when available, null otherwise
   */
  public PredefinedMode get(PredefinedModeType modeType);  
  
  /** verify if predefined mode is registered 
   * 
   * @param modeType to verify
   * @return true when available, false otherwise
   */
  public boolean containsPredefinedMode(PredefinedModeType modeType);
      
  /**
   * Collect the first registered mode
   * 
   * @return first registered mode if any
   */
  public Mode getFirst();

  /**
   * Retrieve a Mode by its XML Id
   *  
   * @param xmlId the XML Id of the specified mode
   * @return the retrieved mode, or null if no mode was found
   */
  public Mode getByXmlId(String xmlId);
  
  /**
   * Provide all modes as a set. This collection is a copy so any changes have no impact on the internally registered modes
   * @return all registered modes
   */
  public abstract Set<Mode> copyOfValuesAsSet();

  /**
   * Verify if mode is present
   * 
   * @param mode to verify for
   * @return true when present, false otherwise (including when passed in mode is null)
   */
  public default boolean contains(Mode mode) {
    return mode!= null ? get(mode.getId())!=null : false;
  }

}
