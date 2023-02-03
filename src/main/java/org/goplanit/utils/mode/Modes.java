package org.goplanit.utils.mode;

import org.goplanit.utils.id.ManagedIdEntities;

/**
 * container class and factory methods for modes with some
 * 
 * @author markr
 *
 */
public interface Modes extends ManagedIdEntities<Mode> {
    
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
   * Retrieve a Mode by its XML Id
   *  
   * @param xmlId the XML Id of the specified mode
   * @return the retrieved mode, or null if no mode was found
   */
  public Mode getByXmlId(String xmlId);

  /**
   * {@inheritDoc
   */
  @Override
  public abstract Modes shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract Modes deepClone();

  /**
   * Collect the mode factory to use for creating instances
   * 
   * @return modeFactory to create modes for this container
   */
  @Override
  public abstract ModeFactory getFactory();
  

}
