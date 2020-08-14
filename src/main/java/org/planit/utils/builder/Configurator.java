package org.planit.utils.builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.planit.utils.exceptions.PlanItException;

/**
 * 
 * This is the base class for all configuration oriented proxy classes. Whenever we configure a traffic assignment component, or any other component with functionality that should
 * not be exposed to the user, while at the same time this user must be able to configure this class by setting one or more configuration options, we must use this class to provide
 * a convenient mapping mechanism that work for any such situation without having to manually implement all the configuration options that are already present on the class that
 * contains the functionality.
 * 
 * The aim of this configurator is to store all the function calls that should be delayed and deferred to the class that contains the actual functionality in a concise and general
 * way.
 * 
 * @author markr
 *
 */
public class Configurator<T> {

  /** the logger */
  private static final Logger LOGGER = Logger.getLogger(Configurator.class.getCanonicalName());
  
  /**
   * the class type of the instance we are configuring
   */
  private final Class<T> configuratorClassType;  

  /** the methods to invoke on the to be configured object instance and their parameters */
  protected final Map<String, Object[]> delayedMethodCalls;

  /**
   * collect the parameter types of the passed in object in their original order
   * 
   * @param parameters the parameters
   * @return parameterTypes array
   * @throws PlanItException thrown if error
   */
  protected Class<?>[] collectParameterTypes(Object... parameters) throws PlanItException {
    PlanItException.throwIf(parameters == null, "The parameters to collect signature for are null");
    Class<?>[] parameterTypes = new Class<?>[parameters.length];
    for (int index = 0; index < parameters.length; ++index) {
      parameterTypes[index] = parameters[index].getClass();
    }
    return parameterTypes;
  }

  /**
   * Call a void method on the toConfigure class instance
   * 
   * @param instance   to call method on
   * @param methodName to call
   * @param parameters to add to call
   * @throws IllegalAccessException    thrown if error
   * @throws IllegalArgumentException  thrown if error
   * @throws InvocationTargetException thrown if error
   * @throws PlanItException           thrown if instance or its class are unknown
   * @throws NoSuchMethodException     thrown if error
   * @throws SecurityException         thrown if error
   */
  protected void callVoidMethod(T instance, String methodName, Object... parameters)
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, PlanItException, NoSuchMethodException, SecurityException {
    PlanItException.throwIf(instance == null, "The instance to configure by calling " + methodName + " is not available");
    
    // check if each parameter is assignable for the method at hand. first match we choose
    for (Method method : instance.getClass().getMethods()) {
      if (!method.getName().equals(methodName)) {
          continue;
      }
      Class<?>[] parameterTypes = method.getParameterTypes();
      boolean matches = true;
      if(parameterTypes.length != parameters.length) {
        matches = false;
      }else
      {
        for (int i = 0; i < parameterTypes.length; i++) {
            if (!parameterTypes[i].isAssignableFrom(parameters[i].getClass())) {
                matches = false;
                break;
            }
        }
      }
      if (matches) {
        // obtain a Class[] based on the passed arguments as Object[]
        method.invoke(instance,  parameters);
      }
    }    
  }

  /**
   * Constructor
   * 
   * @param instanceType the class type of the instance we are configuring
   */
  public Configurator(Class<T> instanceType) {
    this.configuratorClassType = instanceType;
    this.delayedMethodCalls = new HashMap<String, Object[]>();
  }
  
  /** collect the class type we are configuring for
   * @return class type
   */
  public Class<T> getClassTypeToConfigure(){
    return configuratorClassType;
  }  
  
  /**
   * Register a method call to a setter that should be invoked on the to be configured object instance once it is available
   * 
   * @param methodName the method name
   * @param parameters the parameters of the method
   */
  public void registerDelayedMethodCall(String methodName, Object... parameters) {
    delayedMethodCalls.put(methodName, parameters);
  }  
  
  /** collect the first parameter submitted with method call of given signature. If not available null is returned
   * 
   * @param methodName
   * @return first parameter of delay method name call
   */
  public Object getFirstParameterOfDelayedMethodCall(String methodName) {
    Object[] parameters = delayedMethodCalls.get(methodName);
    if(parameters!= null && parameters.length >= 1) {
      return parameters[0];
    }else {
      return null;
    }
  }

  /**
   * Configure the passed in instance with the registered method calls
   * 
   * @param toConfigureInstance the instance to configure
   * @throws PlanItException thrown if error
   */
  public void configure(T toConfigureInstance) throws PlanItException {
    for (Map.Entry<String, Object[]> methodCall : delayedMethodCalls.entrySet()) {
      try {
        callVoidMethod(toConfigureInstance, methodCall.getKey(), methodCall.getValue());
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
        LOGGER.severe(e.getMessage());
        throw new PlanItException("could not call configurator delayed method call to " + methodCall.getKey() + " on class " + toConfigureInstance.getClass().getCanonicalName());
      }
    }
  }

}
