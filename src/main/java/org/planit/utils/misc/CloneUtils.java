package org.planit.utils.misc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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
  public static <T> T clone(T object) {
      try {
          return (T)deSerialize(serialize(object));
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
  }
  
}
