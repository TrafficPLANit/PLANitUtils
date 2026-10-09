package org.goplanit.utils.network.layer.physical;

import java.lang.reflect.Array;

/**
 * Generic dense compiled relation mapping for an object type to store data by a contiguous index based on two
 * other ids with minimal overhead. Example: allowed turn data.
 *
 * Supports:
 *  - lookup by key (O(degree)) where the key needs a mapping to a local index, e.g. outgoing link id
 *  - lookup by index (O(1))
 *
 * Assumes:
 *  - inIndex is contiguous (0..N-1), e.g. incoming link id
 *  - arrays are aligned per index position
 *
 * @param <T> Type of object to store as value
 */
public final class CompiledRelationMapping<T> {

  /**
   * Marker for missing values
   */
  public static final Object MISSING = null;

  /**
   * For each primary key (incoming link id), the list of secondary keys (e.g. outgoing link IDs) where its location
   * determines how to get to the value in the values array
   */
  private final long[][] secondaryKeyIndicesByPrimaryKey;

  /**
   * For each primary key index, the list of values aligned by secondary key index.
   */
  private final Object[][] valuesByPrimaryKeyAndSecondaryIndex;

  private final long size;

  private final Class<T> clazz; // needed to create generic arrays

  /**
   * Constructor
   *
   * @param clazz                           Class of the value type T
   * @param secondaryKeyIndicesByPrimaryKey array of secondary key indices per primary key
   * @param valuesByPrimaryKeyAndSecondaryIndex array of values per primary key and secondary index
   * @param size                             total size (number of primary keys)
   */
  @SuppressWarnings("unchecked")
  public CompiledRelationMapping(
      Class<T> clazz,
      long[][] secondaryKeyIndicesByPrimaryKey,
      T[][] valuesByPrimaryKeyAndSecondaryIndex,
      long size) {
    this.clazz = clazz;
    this.secondaryKeyIndicesByPrimaryKey = secondaryKeyIndicesByPrimaryKey;
    this.valuesByPrimaryKeyAndSecondaryIndex = valuesByPrimaryKeyAndSecondaryIndex;
    this.size = size;
  }

  /**
   * Slow path: lookup by key value (O(degree))
   */
  @SuppressWarnings("unchecked")
  public T get(long primaryKey, long secondaryKey) {
    long[] keys = secondaryKeyIndicesByPrimaryKey[(int) primaryKey];
    Object[] vals = valuesByPrimaryKeyAndSecondaryIndex[(int) primaryKey];

    if (keys == null) {
      return (T) MISSING;
    }

    for (int i = 0; i < keys.length; i++) {
      if (keys[i] == secondaryKey) {
        return (T) vals[i];
      }
    }

    return (T) MISSING;
  }

  /**
   * Fast path: lookup by precomputed position (O(1))
   */
  @SuppressWarnings("unchecked")
  public T getByIndex(long primaryKey, int secondaryKeyLocalIndex) {
    Object[] vals = valuesByPrimaryKeyAndSecondaryIndex[(int) primaryKey];

    if (vals == null || secondaryKeyLocalIndex < 0 || secondaryKeyLocalIndex >= vals.length) {
      return (T) MISSING;
    }

    return (T) vals[secondaryKeyLocalIndex];
  }

  /**
   * Optional helper: returns key at position
   */
  public long getSecondaryKeyByIndex(long primaryKey, int index) {
    long[] keys = secondaryKeyIndicesByPrimaryKey[(int) primaryKey];

    if (keys == null || index < 0 || index >= keys.length) {
      return -1L;
    }

    return keys[index];
  }

  public long size() {
    return size;
  }

  /**
   * Creates a deep copy of this CompiledRelationIndex.
   */
  @SuppressWarnings("unchecked")
  public CompiledRelationMapping<T> copy() {
    int rows = secondaryKeyIndicesByPrimaryKey.length;

    long[][] keysCopy = new long[rows][];
    T[][] valuesCopy = (T[][]) Array.newInstance(clazz, rows, 0);

    for (int i = 0; i < rows; i++) {
      if (secondaryKeyIndicesByPrimaryKey[i] != null) {
        keysCopy[i] = new long[secondaryKeyIndicesByPrimaryKey[i].length];
        System.arraycopy(secondaryKeyIndicesByPrimaryKey[i], 0, keysCopy[i], 0,
            secondaryKeyIndicesByPrimaryKey[i].length);
      }
      if (valuesByPrimaryKeyAndSecondaryIndex[i] != null) {
        valuesCopy[i] = (T[]) Array.newInstance(clazz, valuesByPrimaryKeyAndSecondaryIndex[i].length);
        System.arraycopy(valuesByPrimaryKeyAndSecondaryIndex[i], 0, valuesCopy[i], 0,
            valuesByPrimaryKeyAndSecondaryIndex[i].length);
      }
    }

    return new CompiledRelationMapping<>(clazz, keysCopy, valuesCopy, size);
  }
}