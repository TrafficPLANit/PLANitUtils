package org.goplanit.utils.misc;

import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.wrapper.MapWrapper;
import org.goplanit.utils.wrapper.MapWrapperImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

/**
 * Class that allows one to track multiple entities of classes by a custom index based on a provided mapping for a given
 * instance. User should explicitly initialise for each class that requires a custom tracker container via {@link #initialiseEntityContainer(Class, Function)}
 *
 * @author markr
 */
public class CustomIndexTracker {

  private static final Logger LOGGER = Logger.getLogger(CustomIndexTracker.class.getCanonicalName());

  /** track PLANit entities by any single property via this map */
  protected final Map<Class<?>, MapWrapper<?, ?>> entitiyByIndexTracker = new HashMap<>();

  /**
   * Stores an object by its source Id, after checking whether the external Id is a duplicate
   *
   * @param <V> type of object being stored
   * @param obj object being stored by its class signature, assuming ithat is the identifier it is registered under
   */
  public <V> void register(final V obj) {
    register(obj.getClass(), obj);
  }

  /**
   * Stores an object by its source Id, after checking whether the external Id is a duplicate
   *
   * @param <U>      type of object being stored
   * @param <V>      value to store
   * @param theClazz class to identify the correct container
   * @param obj      object being stored
   */
  public <U, V> void register(Class<U> theClazz, final V obj){

    @SuppressWarnings("unchecked")
    var mapWrapper = (MapWrapper<U, V>) entitiyByIndexTracker.get(theClazz);
    if(mapWrapper == null) {
      throw new PlanItRunTimeException("No source id container registered for PLANit entity of type %s, unable to register, perhaps consider registering via its superclass explicitly",
          obj.getClass().getName());
    }
    var old = mapWrapper.register(obj);
    if(old!=null){
      throw new PlanItRunTimeException("PLANit entity of type %s already registered by its source id %s, unable to register", obj.getClass().getName(),
          mapWrapper.getKeyByValue(old).toString());
    }
  }

  /**
   * register a new source id tracker (empty) where a function is used to extract the source id from the entity and the class is used unique identifier for the underlying tracking
   * container
   *
   * @param <K>        key type used
   * @param <V>        value type used
   * @param clazz      identifier in container of containers
   * @param valueToKey function mapping value to key
   */
  public <K, V> void initialiseEntityContainer(Class<V> clazz, final Function<V, K> valueToKey) {
    if (entitiyByIndexTracker.containsKey(clazz)) {
      LOGGER.warning(String.format("Unable to register PLANit entity tracker for %s, already present", clazz.getName()));
    }
    entitiyByIndexTracker.put(clazz, new MapWrapperImpl<>(new HashMap<>(), valueToKey));
  }

  /**
   * register a new source id tracker (empty) where a function is used to extract the source id from the entity and the class is used unique identifier for the underlying tracking
   * container
   *
   * @param <K>              key type used
   * @param <V>              value type used
   * @param clazz            identifier in container of containers
   * @param valueToKey       function mapping value to key
   * @param addToSourceIdMap add all entities in iterable to the newly created source id map upon creation
   */
  public <K, V> void initialiseEntityContainer(Class<V> clazz, final Function<V, K> valueToKey, Iterable<V> addToSourceIdMap) {
    initialiseEntityContainer(clazz, valueToKey);
    getEntityContainer(clazz).addAll(addToSourceIdMap);
  }

  /**
   * access to the container with custom Ids
   *
   * @param <V>   value of the container
   * @param clazz to collect container for
   * @return the source id map wrapper, null if not present
   */
  @SuppressWarnings("unchecked")
  public <V> MapWrapper<?, V> getEntityContainer(Class<V> clazz) {
    return (MapWrapper<?, V>) entitiyByIndexTracker.get(clazz);
  }

  /**
   * Get an entry of register class by its custom id
   *
   * @param <V>   return type
   * @param <K>   key to find it by
   * @param clazz class identifier for selecting the correct container tracker
   * @param key   the actual key to use
   * @return value found, null if not present
   */
  @SuppressWarnings("unchecked")
  public <V, K> V get(Class<V> clazz, K key) {
    return ((MapWrapper<K, V>) entitiyByIndexTracker.get(clazz)).get(key);
  }
}
