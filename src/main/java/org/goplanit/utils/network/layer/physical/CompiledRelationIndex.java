package org.goplanit.utils.network.layer.physical;

/**
 * Compiled relation index for primitive longs.
 */
public final class CompiledRelationIndex {

  public static final long BANNED = -1L;

  private final long[][] secondaryKeyIndicesByPrimaryKey;
  private final long[][] valuesByPrimaryKeyAndSecondaryIndex;
  private final long size;

  public CompiledRelationIndex(
      long[][] secondaryKeyIndicesByPrimaryKey, long[][] valuesByPrimaryKeyAndSecondaryIndex, long size) {
    this.secondaryKeyIndicesByPrimaryKey = secondaryKeyIndicesByPrimaryKey;
    this.valuesByPrimaryKeyAndSecondaryIndex = valuesByPrimaryKeyAndSecondaryIndex;
    this.size = size;
  }

  // Slow lookup by key (O(degree))
  public long get(long primaryKey, long secondaryKey) {
    long[] keys = secondaryKeyIndicesByPrimaryKey[(int) primaryKey];
    long[] vals = valuesByPrimaryKeyAndSecondaryIndex[(int) primaryKey];
    if (keys == null) return BANNED;
    for (int i = 0; i < keys.length; i++) {
      if (keys[i] == secondaryKey) return vals[i];
    }
    return BANNED;
  }

  // Fast lookup by local secondary index (O(1))
  public long getByIndex(long primaryKey, int secondaryKeyLocalIndex) {
    long[] vals = valuesByPrimaryKeyAndSecondaryIndex[(int) primaryKey];
    if (vals == null || secondaryKeyLocalIndex < 0 || secondaryKeyLocalIndex >= vals.length) return BANNED;
    return vals[secondaryKeyLocalIndex];
  }

  // Get secondary key by index
  public long getSecondaryKeyByIndex(long primaryKey, int index) {
    long[] keys = secondaryKeyIndicesByPrimaryKey[(int) primaryKey];
    if (keys == null || index < 0 || index >= keys.length) return BANNED;
    return keys[index];
  }

  public long size() {
    return size;
  }

  // Deep copy
  public CompiledRelationIndex copy() {
    int rows = secondaryKeyIndicesByPrimaryKey.length;

    long[][] keysCopy = new long[rows][];
    long[][] valuesCopy = new long[rows][];

    for (int i = 0; i < rows; i++) {
      if (secondaryKeyIndicesByPrimaryKey[i] != null) {
        keysCopy[i] = new long[secondaryKeyIndicesByPrimaryKey[i].length];
        System.arraycopy(secondaryKeyIndicesByPrimaryKey[i], 0, keysCopy[i], 0, keysCopy[i].length);
      }
      if (valuesByPrimaryKeyAndSecondaryIndex[i] != null) {
        valuesCopy[i] = new long[valuesByPrimaryKeyAndSecondaryIndex[i].length];
        System.arraycopy(valuesByPrimaryKeyAndSecondaryIndex[i], 0, valuesCopy[i], 0, valuesByPrimaryKeyAndSecondaryIndex[i].length);
      }
    }

    return new CompiledRelationIndex(keysCopy, valuesCopy, size);
  }
}