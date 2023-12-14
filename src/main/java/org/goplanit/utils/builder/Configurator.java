package org.goplanit.utils.builder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.exceptions.PlanItRunTimeException;

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

  /** The methods to invoke on the to be configured object instance and their parameters.
   * Since the same method could be called multiple times (with different parameters) we track a list per call */
  protected final Map<String, List<Object[]>> delayedMethodCalls;

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
      throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    PlanItRunTimeException.throwIf(instance == null, "The instance to configure by calling " + methodName + " is not available");
    
    // check if each parameter is assignable for the method at hand. first match we choose
    boolean matches = false;
    for (Method method : instance.getClass().getMethods()) {
      if (!method.getName().equals(methodName)) {
          continue;
      }
      Class<?>[] parameterTypes = method.getParameterTypes();
      if(parameterTypes.length != parameters.length) {
        continue;
      }else
      {
        boolean parametersMatch = true;
        for (int i = 0; i < parameterTypes.length; i++) {
          Class<?> manuallyBoxedParameter = parameterTypes[i];
          if(parameterTypes[i].isPrimitive()) {
            // no auto-boxing, do it manually to check for match
            switch (parameterTypes[i].getName()) {
            case "double":
              manuallyBoxedParameter = Double.class;
              break;
            case "int":
              manuallyBoxedParameter = Integer.class;
              break; 
            case "byte":
              manuallyBoxedParameter = Byte.class;
              break; 
            case "short":
              manuallyBoxedParameter = Short.class;
              break; 
            case "long":
              manuallyBoxedParameter = Long.class;
              break; 
            case "float":
              manuallyBoxedParameter = Float.class;
              break; 
            case "char":
              manuallyBoxedParameter = Character.class;
              break;
            case "boolean":
              manuallyBoxedParameter = Boolean.class;
              break; 
            default:
              break;
            }
            // now try again
            if (!manuallyBoxedParameter.isAssignableFrom(parameters[i].getClass())) {
              parametersMatch = false;  
              break;
            }
          }
          matches = parametersMatch;
        }
      }
      if (matches) {
        // obtain a Class[] based on the passed arguments as Object[]
        method.invoke(instance,  parameters);
        break;
      }
    } 
    PlanItRunTimeException.throwIf(!matches, String.format(
        "unable to call registered method call %s no match found (or invalid argument list) on instance of type %s",
        methodName, instance.getClass().getCanonicalName()));
  }

  /**
   * Register a method call to a setter that should be invoked on the to be configured object instance once it is available
   * 
   * @param methodName the method name
   * @param parameters the parameters of the method
   */
  protected void registerDelayedMethodCall(String methodName, Object... parameters) {
    List<Object[]> parametersPerCall = delayedMethodCalls.get(methodName);
    if(parametersPerCall == null) {
      parametersPerCall = new ArrayList<Object[]>();
      delayedMethodCalls.put(methodName, parametersPerCall);  
    }
    parametersPerCall.add(parameters);    
  }

  /** Collect the first parameter submitted with (last) registered delayed method call of given signature. If not available null is returned.
   * Useful to mimic getters for a given setter on configurator derived class.
   * 
   * @param methodName that reflects the delayed call
   * @return first parameter of delay method name call
   */
  protected Object getFirstParameterOfDelayedMethodCall(String methodName) {
    List<Object[]> parametersPerCall = delayedMethodCalls.get(methodName);
    if(parametersPerCall!= null) {
      Object[] parametersOfLastCall = parametersPerCall.get(parametersPerCall.size()-1);
      if(parametersOfLastCall.length >= 1) {
        return parametersOfLastCall[0];
      }
    }
    return null;
  }

  /**
   * Constructor
   * 
   * @param instanceType the class type of the instance we are configuring
   */
  public Configurator(Class<T> instanceType) {
    this.configuratorClassType = instanceType;
    this.delayedMethodCalls = new HashMap<String, List<Object[]>>();
  }
  
  /** collect the class type we are configuring for
   * @return class type
   */
  public Class<T> getClassTypeToConfigure(){
    return configuratorClassType;
  }  
  
  /**
   * Configure the passed in instance with the registered method calls
   * 
   * @param toConfigureInstance the instance to configure
   */
  public void configure(T toConfigureInstance){
    /* cycle through unique method calls */
    for (Map.Entry<String, List<Object[]>> methodCall : delayedMethodCalls.entrySet()) {
      try {
        /* cycle through all calls to same method with given parameters */
        for(Object[] parametersOfCall : methodCall.getValue()) {
          callVoidMethod(toConfigureInstance, methodCall.getKey(), parametersOfCall);
        }
      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
        LOGGER.severe(e.getMessage());
        throw new PlanItRunTimeException(
                "Could not call configurator delayed method call to %s on class %s",methodCall.getKey(), toConfigureInstance.getClass().getCanonicalName());
      }
    }
  }

}
