package org.goplanit.utils.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.goplanit.utils.exceptions.PlanItException;
import org.goplanit.utils.exceptions.PlanItRunTimeException;

/**
 * Reflection specific utility functions
 * 
 * @author markr
 *
 */
public class ReflectionUtils {
  
  private static final Logger LOGGER = Logger.getLogger(ReflectionUtils.class.getCanonicalName());

  /**
   * Given fields get the key value map version based on filter
   * @param <K> key in result map
   * @param <V> value in result map
   *
   * @param fields to collect from
   * @param keyFunction transforms field to key entry in map
   * @param valueFunction transforms field to value entry in map
   * @param modifierFilter applied to the fields of the class, when false we exclude the field, when true we keep it
   * @return map with entries, or empty if none could be found or something went wrong
   */
  private static <K,V> Map<K,V> convertFieldsOnInstanceToKeyValueMap(
          Field[] fields,
          Object compatibleInstance,
          Function<Field,K> keyFunction,
          BiFunction<Field,Object,V> valueFunction,
          Function<Integer,Boolean> modifierFilter) {
    Map<K,V> fieldValueMap = new HashMap<>();
    for (int index = 0; index < fields.length; ++index) {
      var field = fields[index];
      try {
        if(modifierFilter.apply(field.getModifiers())) {
          fieldValueMap.put(keyFunction.apply(field),valueFunction.apply(field, compatibleInstance));
        }
      }catch (Exception e) {
        // do nothing
      }
    }

    return fieldValueMap;
  }
  
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
   */
  @SuppressWarnings("unchecked")
  public static <T> T createTypedInstance(String className, Object...constructorParameters){
    return (T) createInstance(className, constructorParameters);
  }
  
  /** Create an instance of given class name and provided constructor parameters
   * @param className of the class to be instantiated
   * @param constructorParameters parameters for constructor
   * @return created instance
   */
  public static Object createInstance(String className, Object...constructorParameters){
    Object createdInstance = null;
    
    /* constructor */
    if (constructorParameters != null) {
      Constructor<?> constructor = null;      
      try {                         
        Class<?>[] parameterTypes = ReflectionUtils.getParameterTypes(constructorParameters);
        constructor = Class.forName(className).getConstructor(parameterTypes);    
      }catch ( Exception e) {

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
        throw new PlanItRunTimeException("Unable to find appropriate constructor for type: "+ className, e);
      }      
        
      /* instance */
      try {
        createdInstance = constructor.newInstance(constructorParameters);
      } catch (Exception e) {
        throw new PlanItRunTimeException("Unable to create instance of type: "+ className, e);
      }
    } else {
      
      /* instance */
      try {
        createdInstance = Class.forName(className).getConstructor().newInstance();
      } catch (Exception e) {
        throw new PlanItRunTimeException("Unable to create instance of type via default constructor: "+ className, e);
      }
    }          
    return createdInstance;    
  }

  /** Collect all declared fields of instance in Map in name, value format
   *
   * @param clazz to identify fields from
   * @param compatibleClazzInstance to collect from, should be compatible with class provided
   * @param modifierFilter applied to the fields of the class, when false we exclude the field, when true we keep it
   * @return map with entries, or empty if none could be found or something went wrong
   */
  public static Map<String,Object> declaredFieldsNameValueMap(Class<?> clazz, Object compatibleClazzInstance, Function<Integer,Boolean> modifierFilter) {
    /* value is the field value regardless of accessibility */
    BiFunction<Field, Object, Object> biFunction = (field, object) -> {
      Object value = null;
      try {
        var old = field.canAccess(object);
        field.setAccessible(true);
        value = field.get(object);
        field.setAccessible(old);
      }catch (Exception e) {
        // do nothing
      };
      return value;
    };

    /* key is field name */
    return declaredFieldsToMap(clazz, compatibleClazzInstance, Field::getName, biFunction, modifierFilter);
  }

  /** Collect all declared fields of instance in Map in name, value format
   * 
   * @param settingsClazzInstance to collect from
   * @param modifierFilter applied to the fields of the class, when false we exclude the field, when true we keep it
   * @return map with entries, or empty if none could be found or something went wrong
   */
  public static Map<String,Object> declaredFieldsNameValueMap(Object settingsClazzInstance, Function<Integer,Boolean> modifierFilter) {
    return declaredFieldsNameValueMap(settingsClazzInstance.getClass(), settingsClazzInstance, modifierFilter);
  }

  /** Collect all declared fields of instance in Map based on functions passed in
   *
   *
   * @param <K> key in result map
   * @param <V> value in result map
   *
   * @param clazz specific class to get declared fields for
   * @param compatibleInstance to collect from, should be compatible with class, so instance of it or derived class instance
   * @param keyFunction transforms field to key entry in map
   * @param valueFunction transforms field to value entry in map
   * @param modifierFilter applied to the fields of the class, when false we exclude the field, when true we keep it
   * @return map with entries, or empty if none could be found or something went wrong
   */
  public static <K,V> Map<K,V> declaredFieldsToMap(
          Class<?> clazz, Object compatibleInstance, Function<Field,K> keyFunction,  BiFunction<Field,Object,V> valueFunction, Function<Integer,Boolean> modifierFilter) {
    var fields = clazz.getDeclaredFields();
    return convertFieldsOnInstanceToKeyValueMap(fields, compatibleInstance, keyFunction, valueFunction, modifierFilter);
  }
  
  /** Collect all declared fields of instance in Map based on functions passed in 
   * 
   *
   * @param <K> key in result map
   * @param <V> value in result map
   * 
   * @param settingsClazzInstance to collect from
   * @param keyFunction transforms field to key entry in map
   * @param valueFunction transforms field to value entry in map 
   * @param modifierFilter applied to the fields of the class, when false we exclude the field, when true we keep it
   * @return map with entries, or empty if none could be found or something went wrong
   */
  public static <K,V> Map<K,V> declaredFieldsToMap(Object settingsClazzInstance, Function<Field,K> keyFunction,  BiFunction<Field,Object,V> valueFunction, Function<Integer,Boolean> modifierFilter) {
    var fields = settingsClazzInstance.getClass().getDeclaredFields();
    return convertFieldsOnInstanceToKeyValueMap(fields, settingsClazzInstance, keyFunction, valueFunction, modifierFilter);
  }

  /**
   * For a container with known data class, explicitly cast the internal generics of the collection to this class
   *
   * @param collectionClass the collection class with unattainable class data type
   * @param dataTypeClass of entries of collection
   * @return collection with data type of correct type
   * @param <U> collection type U of ?
   * @param <V> data type
   * @param <W> explicit collection type U with data type V
   */
  public static <U extends Collection<?>, V, W extends Collection<V>> Class<W> injectClassIntoContainerGenerics(Class<U> collectionClass, Class<V> dataTypeClass){
    return (Class<W>) collectionClass;
  }
}
