package org.goplanit.utils.enums;

import java.util.function.Supplier;

/**
 * Interface that deals with injecting way to create an enum based on its internal enum value through EnumValue, e.g.
 * and enum can implement this interface and EnumValue, then call {@link #createFromValues(Supplier, Object)} where the supplier
 * is its values() method and the value is some value that is attached to the enum instance. It will then find that matching instance and
 * return it.
 *
 * @param <T> enum type
 * @param <V> internal value type of enum
 */
public interface EnumOf<T extends EnumValue<V>, V> {

  /**
   * Create an enum instance from a list of options based on its internal value
   *
   * @param value internal value to extract enum for
   * @return the corresponding enum found, null when not present
   */
  public default <T> T createFromValues(Supplier<T[]> enumValuesSupplier, V value){
    T[] values = enumValuesSupplier.get();
    for(int index = 0 ; index < values.length; ++index){
      if(((EnumValue<V>) values[index]).getValue() == value){
        return values[index];
      }
    }
    return null;
  }
}
