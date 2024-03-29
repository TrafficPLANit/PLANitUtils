package org.goplanit.utils.builder;

import java.util.logging.Logger;

import org.goplanit.utils.exceptions.PlanItException;

/**
 * 
 * Builder class to build something of type T
 * @author markr
 *
 * @param <T>class to build
 */
public abstract class Builder<T> {
  
  /** the logger */
  private static final Logger LOGGER = Logger.getLogger(Builder.class.getCanonicalName());  
  
  /**
   * The traffic assignment class to build in canonical string form
   */
  private final Class<T> classToBuild;
  
  /**
   * The configurator for this builder
   */
  private Configurator<T> configurator;
  
  /** Allow derived classes to provide their own configurator for this builder, by default we create a base class configurator
   * @return appropriate configurator
   * @throws PlanItException thrown if error
   */
  protected abstract Configurator<T> createConfigurator() throws PlanItException;
  
  /** 
   * collect the class to build
   * 
   * @return the class to build
   */
  protected Class<T> getClassToBuild() {
    return classToBuild;
  }
  
 
  /**
   * Constructor 
   * 
   * @param classToBuild to have access to type of T
   */
  protected Builder(Class<T> classToBuild) {
    this.classToBuild = classToBuild;
  }
  
  /** the configurator for this builder. It allows one to hide the builder aspect and expose (parts of) the user available configuration options
   * via this object
   *  
   * @return the configurator, null if no configurator is available nor could be created
   */
  public Configurator<T> getConfigurator(){
    if(configurator == null) {
      try {
        this.configurator = createConfigurator();
      }catch(PlanItException e) {
        LOGGER.severe(String.format("Unable to create the appropriate configurator because: %s", e.getMessage()));
      }
    }
    return configurator;
  }


  /** Build an instance of class T
   * @return instance of T
   * @throws PlanItException thrown when error
   */
  public abstract T build() throws PlanItException;

}
