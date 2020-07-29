package org.planit.utilsreflection;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.planit.utils.exceptions.PlanItException;

/**
 * Reflection specific utility functions
 * 
 * @author markr
 *
 */
public class ReflectionUtils {
  
  private static final Logger LOGGER = Logger.getLogger(ReflectionUtils.class.getCanonicalName());
  
  /** Method that constructs the parameter types that go with the passed in parameters.
   * 
   * @param parameters
   * @return parameter class types
   */
  public static Class<?>[] getParameterTypes(Object[] parameters) {
    // stream and collect class, map to list
    List<Class<?>> parameterTypes = Arrays.stream(parameters).map(Object::getClass).collect(Collectors.toList());
    // return as array of Class<?>
    return parameterTypes.toArray(Class<?>[]::new);    
  }
  
  
  /** Create an instance of given class name and provided constructor parameters
   * @param className of the class to be instantiated
   * @param constructorParameters parameters for constructor
   * @return created instance
   * @throws PlanItException when error occurs during instantiation
   */
  public static Object createInstance(String className, Object...constructorParameters) throws PlanItException {
    Object createdInstance = null;
    try {
      if (constructorParameters != null) {
        Class<?>[] parameterTypes = ReflectionUtils.getParameterTypes(constructorParameters);
        createdInstance = Class.forName(className).getConstructor(parameterTypes).newInstance(constructorParameters);
      } else {
        createdInstance = Class.forName(className).getConstructor().newInstance();
      }
    } catch ( InstantiationException |
              IllegalAccessException |
              IllegalArgumentException|
              InvocationTargetException|
              NoSuchMethodException |
              SecurityException |
              ClassNotFoundException e) {
      e.printStackTrace();
      LOGGER.severe(e.getMessage());
      throw new PlanItException("Unable to create instance of type: "+ className, e);    
    }
    return createdInstance;    
  }

}
