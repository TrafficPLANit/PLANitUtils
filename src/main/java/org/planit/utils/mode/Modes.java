package org.planit.utils.mode;

import java.util.logging.Logger;

import org.planit.utils.id.ManagedIdEntities;

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
   * clone modes container
   */
  @Override
  public abstract Modes clone();

  /**
   * Collect the edge factory to use for creating instances
   * 
   * @return edgeFactory to create edges for this container
   */
  @Override
  public default ModeFactory getFactory(){
    /** override to change return type signature on interface, implementation must still
     * implement this method to provide access to an actual instance */
    Logger.getLogger(Modes.class.getCanonicalName()).warning("getFactory not implemented yet for modes implementation");
    return null;
  }
  

}
