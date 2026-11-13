package org.goplanit.utils.reflection;

import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
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

  /** delegates to {@link #createInstance(String, Object...)} only casts result to type provided
   *
   * @param <T> type of the created instance
   * @param clazz the clazz containing the type of the to be created array
   * @param arrayLength length of the array to create
   * @return created array via reflection of a given length of type T
   */
  @SuppressWarnings("unchecked")
  public static <T> T[] createTypedArrayInstance(Class<T> clazz, int arrayLength){
    return (T[]) java.lang.reflect.Array.newInstance(clazz, arrayLength);
  }

  /** Collect all declared fields of instance in Map in name, value format
   *
   * @param clazz to identify fields from
   * @param compatibleClazzInstance to collect from, should be compatible with class provided
   * @param modifierFilter applied to the fields of the class, when false we exclude the field, when true we keep it
   * @return map with entries, or empty if none could be found or something went wrong
   */
  public static Map<String,Object> declaredFieldsNameValueMap(
          Class<?> clazz, Object compatibleClazzInstance, Function<Integer,Boolean> modifierFilter) {
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
  public static Map<String,Object> declaredFieldsNameValueMap(
          Object settingsClazzInstance, Function<Integer,Boolean> modifierFilter) {
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
  public static <K,V> Map<K,V> declaredFieldsToMap(
          Object settingsClazzInstance, Function<Field,K> keyFunction,  BiFunction<Field,Object,V> valueFunction, Function<Integer,Boolean> modifierFilter) {
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
  public static <U extends Collection<?>, V, W extends Collection<V>> Class<W> injectClassIntoContainerGenerics(
          Class<U> collectionClass, Class<V> dataTypeClass){
    return (Class<W>) collectionClass;
  }

  /**
   * The class a declared type comes down to: the class itself, the class before its type arguments, such as
   * {@code List} for {@code List<String>}, or the upper bound of a wildcard or type variable, such as {@code Number}
   * for {@code ? extends Number}. Any other type gives {@code Object}.
   * <p>
   * A method's declared parameter types, from {@code method.getGenericParameterTypes()}, keep their type arguments, so
   * they come as several kinds of {@link Type}. This gives the one class to decide on, such as whether a parameter
   * takes a list, a map or a single value. See {@link #getTypeArgument(Type, int)} for an example on an annotated
   * method.
   * </p>
   *
   * @param type a declared type, such as a method's generic parameter type
   * @return its class
   */
  public static Class<?> getRawClass(Type type) {
    if (type instanceof Class) {
      return (Class<?>) type;
    }
    if (type instanceof ParameterizedType) {
      return getRawClass(((ParameterizedType) type).getRawType());
    }
    if (type instanceof WildcardType) {
      return getRawClass(((WildcardType) type).getUpperBounds()[0]);
    }
    if (type instanceof TypeVariable) {
      return getRawClass(((TypeVariable<?>) type).getBounds()[0]);
    }
    return Object.class;
  }

  /**
   * A type argument of a declared type, such as {@code String} for argument 0 of {@code List<String>}. A wildcard
   * argument gives its upper bound, and a type without type arguments, such as a raw {@code List}, gives
   * {@code Object}.
   * <p>
   * This tells code what a generic parameter of a method holds, which it needs when it calls the method through
   * reflection with values it makes itself, such as values read from a file. Java keeps type arguments in a method's
   * signature, but not in the objects passed to it: a {@code UserOverride} object cannot say what it holds, while the
   * parameter it is passed as can. For example, a method found through its annotation:
   * </p>
   * <pre>
   * &#64;SettingsWrite(key = "trafficSignalKindOverrides", effect = SettingsKeyEffect.PUT_EACH,
   *     level = SettingsKeyLevel.UNCOMMON, description = "Traffic signal kinds by node id")
   * public void overrideTrafficSignalKind(long nodeId, UserOverride&lt;TrafficSignalKind&gt; kind)
   * </pre>
   * <p>
   * For the entry {@code 123: RAMP_METER} in a file, the code reads the second parameter to learn that the text
   * {@code RAMP_METER} must become a {@code TrafficSignalKind}, wrapped in a {@code UserOverride}:
   * </p>
   * <pre>
   * Type declared = method.getGenericParameterTypes()[1];   // UserOverride&lt;TrafficSignalKind&gt;
   * getRawClass(declared);                                  // UserOverride
   * getTypeArgument(declared, 0);                           // TrafficSignalKind
   * </pre>
   * <p>
   * The argument returned can have type arguments of its own, such as {@code UserOverride<TrafficSignalKind>} for
   * argument 1 of {@code Map<Long, UserOverride<TrafficSignalKind>>}, so the code asks again one level down until it
   * reaches a plain class.
   * </p>
   *
   * @param type a declared type
   * @param index of the type argument, from 0
   * @return the type argument, which can have type arguments of its own
   */
  public static Type getTypeArgument(Type type, int index) {
    if (!(type instanceof ParameterizedType)) {
      return Object.class;
    }
    var argument = ((ParameterizedType) type).getActualTypeArguments()[index];
    return argument instanceof WildcardType ? ((WildcardType) argument).getUpperBounds()[0] : argument;
  }

  /**
   * The wrapper class of a primitive type, such as {@code Integer} for {@code int}, and any other class itself.
   * <p>
   * A method can take a primitive, but a value passed to it through reflection is always an object, an {@code int}
   * arriving as an {@code Integer}. So code that checks or makes a value for a parameter by its type works with the
   * wrapper. For example, for {@code setSteps(int steps)}, {@code getBoxedClass(method.getParameterTypes()[0])} gives
   * {@code Integer}, and {@code Integer.valueOf("5")} makes the value {@code method.invoke} takes.
   * </p>
   *
   * @param type a class, which can be primitive
   * @return its wrapper class when primitive, the class itself otherwise
   */
  public static Class<?> getBoxedClass(Class<?> type) {
    return MethodType.methodType(type).wrap().returnType();
  }
}
