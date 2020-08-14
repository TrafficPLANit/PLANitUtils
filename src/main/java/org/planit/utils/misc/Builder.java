package org.planit.utils.misc;

import org.planit.utils.configurator.Configurator;
import org.planit.utils.exceptions.PlanItException;

/**
 * 
 * Builder class to build something of type T
 * @author markr
 *
 * @param <T>class to build
 */
public abstract class Builder<T> {
  
  /**
   * The traffic assignment class to build in canonical string form
   */
  private final Class<T> classToBuild;
  
  /**
   * The configurator for this builder
   */
  private Configurator<T> configurator;
  
  /** Allow derived classes to provide their own configurator for this builder, by default we create a base class configurator
   * @return
   */
  protected Configurator<T> createConfigurator(){
    return new Configurator<T>();
  }
  
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
   * @return the configurator
   */
  public Configurator<T> getConfigurator(){
    if(configurator == null) {
      this.configurator = createConfigurator();
    }
    return configurator;
  }


  /** Build an instance of class T
   * @return instance of T
   * @throws PlanItException thrown when error
   */
  public abstract T build() throws PlanItException;

}
