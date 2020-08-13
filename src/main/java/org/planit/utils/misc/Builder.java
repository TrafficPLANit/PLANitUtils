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
  private final Configurator<T> configurator;
  
  /** 
   * collect the class to build
   * 
   * @return the class to build
   */
  protected Class<T> getClassToBuild() {
    return classToBuild;
  }
  
  /** Allow derived builder to register delayed method calls to the instance of T
   *  which are then invoked upon calling {@code invokedDelayedMethodCalls}. Useful to construct a chain of setters on the
   *  instance T after it has been created but before returned to the user
   * 
   * @param methodName the delayed method call
   * @param parameters parameters of the method
   */
  protected void registerDelayedMethodCall(String methodName, Object...  parameters) {
    configurator.registerDelayedMethodCall(methodName, parameters);
  }
  
  /** perform the method calls that are registered earlier via {@code registerDelayedMethodCall}
   * @param theInstance to invoke the calls on
   * @throws PlanItException thrown when error
   */
  protected void invokeDelayedMethodCalls(T theInstance) throws PlanItException {
    configurator.configure(theInstance);
  }
  
  /**
   * Constructor 
   * 
   * @param classToBuild to have access to type of T
   */
  protected Builder(Class<T> classToBuild) {
    this.classToBuild = classToBuild;
    this.configurator = new Configurator<T>();
  }


  /** Build an instance of class T
   * @return instance of T
   * @throws PlanItException thrown when error
   */
  public abstract T build() throws PlanItException;

}
