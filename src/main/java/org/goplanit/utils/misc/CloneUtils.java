package org.goplanit.utils.misc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Some utility methods to use serialization to allow for deep copy cloning without knowing anything about the objects type
 * 
 * @author markr
 *
 */
public class CloneUtils {

  /** Serialise an object
   * 
   * @param object to serialise
   * @return byte array
   * @throws IOException thrown if error
   */
  public static byte[] serialize(Object object) throws IOException {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      new ObjectOutputStream(os).writeObject(object);
      return os.toByteArray();
  }

  /** Deserialise based on byte array to object
   * 
   * @param <T> type to deserialize
   * @param array to deserialize
   * @return object created
   * @throws IOException thrown if error
   * @throws ClassNotFoundException thrown if error
   */
  @SuppressWarnings("unchecked")
  public static <T> T deSerialize(byte[] array) throws IOException, ClassNotFoundException {
      return (T)new ObjectInputStream(new ByteArrayInputStream(array)).readObject();
  }

  /** clone an object by using the serialise/deserialise methods in this class. This is a costly operation
   * 
   * @param <T> object type
   * @param object to clone
   * @return cloned version
   */
  @SuppressWarnings("unchecked")
  public static <T> T deepClone(T object) {
      try {
          return (T)deSerialize(serialize(object));
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
  }

  /** clone object values (not keys) in source map by using the serialise/deserialise methods in this class. This is a costly operation
   *
   * @param <K> key type
   * @param <V> object value type
   * @param sourceMap to clone
   * @param destinationMap to populate
   * @return destination map
   */
  public static <K,V> Map<K,V> deepCloneFromTo(Map<K,V> sourceMap, Map<K,V> destinationMap ) {
    return deepCloneFromTo(sourceMap, destinationMap, CloneUtils::deepClone);
  }

  /**
   * Put the clone of each value of a map in another map under the same key, each value cloned by the cloner given,
   * such as its own deep clone, the keys not cloned
   *
   * @param <K> key type
   * @param <V> value type
   * @param sourceMap to clone the values of
   * @param destinationMap to put the clones in
   * @param valueCloner giving the clone of a value
   * @return the destination map
   */
  public static <K,V> Map<K,V> deepCloneFromTo(
      Map<K,V> sourceMap, Map<K,V> destinationMap, UnaryOperator<V> valueCloner) {
    sourceMap.forEach((key, value) -> destinationMap.put(key, valueCloner.apply(value)));
    return destinationMap;
  }
}
