package org.goplanit.utils.reflection;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.goplanit.utils.exceptions.PlanItException;

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
   * @param parameters the parameters
   * @return parameter class types
   */
  public static Class<?>[] getParameterTypes(Object[] parameters) {
    // stream and collect class, map to list
    List<Class<?>> parameterTypes = Arrays.stream(parameters).map(Object::getClass).collect(Collectors.toList());
    // return as array of Class<?>
    return parameterTypes.toArray(Class<?>[]::new);    
  }
  
  /** delegates to {@link #createInstance(String, Object...)} only casts result to type provided
   * 
   * @param <T> type of the created instance
   * @param className of the class to be instantiated
   * @param constructorParameters parameters for constructor
   * @return created instance
   * @throws PlanItException when error occurs during instantiation
   */
  @SuppressWarnings("unchecked")
  public static <T> T createTypedInstance(String className, Object...constructorParameters) throws PlanItException {
    return (T) createInstance(className, constructorParameters);
  }
  
  /** Create an instance of given class name and provided constructor parameters
   * @param className of the class to be instantiated
   * @param constructorParameters parameters for constructor
   * @return created instance
   * @throws PlanItException when error occurs during instantiation
   */
  public static Object createInstance(String className, Object...constructorParameters) throws PlanItException {
    Object createdInstance = null;
    
    /* constructor */
    if (constructorParameters != null) {
      Constructor<?> constructor = null;      
      try {                         
        Class<?>[] parameterTypes = ReflectionUtils.getParameterTypes(constructorParameters);
        constructor = Class.forName(className).getConstructor(parameterTypes);    
      }catch ( 
        IllegalArgumentException|
        NoSuchMethodException |
        SecurityException |
        ClassNotFoundException e) {

        //TODO: ideally we loop over all super types and interfaces and its permutations to try
        //      and instantiate automatically via these instead to get around the limitation. No time
        //      yet to implement this as it requires recursion etc.
        LOGGER.severe(String.format(
            "Unable to find constructor for given arguments when constructing instance of %s with constructor parameters %s\n "
            + "likely constructor either requires different number of arguments or it requires super types \n"
            + "whereas provided arguments are implementations of this type \n"
            + "and reflection is not able to infer this as a valid call, see also https://github.com/TrafficPLANit/PLANitUtils/issues/7", className, Arrays.toString(constructorParameters)));
          
        e.printStackTrace();
        LOGGER.severe(e.getMessage());
        throw new PlanItException("Unable to find appropriate constructor for type: "+ className, e);        
      }      
        
      /* instance */
      try {
        createdInstance = constructor.newInstance(constructorParameters);
      } catch (Exception e) {
        throw new PlanItException("Unable to create instance of type: "+ className, e);
      }
    } else {
      
      /* instance */
      try {
        createdInstance = Class.forName(className).getConstructor().newInstance();
      } catch (Exception e) {
        throw new PlanItException("Unable to create instance of type via default constructor: "+ className, e);
      }
    }          
    return createdInstance;    
  }

}
