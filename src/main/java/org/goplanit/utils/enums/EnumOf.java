package org.goplanit.utils.enums;

import java.util.function.Supplier;

/**
 * Creates an enum instance from its internal value through EnumValue. For example, an enum implementing this interface
 * and EnumValue calls {@link #createFromValues(Supplier, Object)} with its values() method as the supplier and a value
 * attached to one of its instances, and gets the matching instance back.
 *
 * @param <T> enum type
 * @param <V> internal value type of enum
 */
public interface EnumOf<T extends EnumValue<V>, V> {

  /**
   * Create an enum instance from a list of options based on its internal value
   *
   * @param <T> type of the supplier
   * @param enumValuesSupplier the supplier to use
   * @param value internal value to extract enum for
   * @return the corresponding enum found, null when not present
   */
  public default <T> T createFromValues(Supplier<T[]> enumValuesSupplier, V value){
    T[] values = enumValuesSupplier.get();
    for(int index = 0 ; index < values.length; ++index){
      V currValue = ((EnumValue<V>) values[index]).getValue();
      if(currValue.equals(value)){
        return values[index];
      }
    }
    return null;
  }
}
