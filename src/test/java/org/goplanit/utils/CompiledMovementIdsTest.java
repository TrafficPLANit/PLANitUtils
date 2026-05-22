package org.goplanit.utils;

import org.goplanit.utils.network.layer.physical.CompiledRelationIndex;
import org.goplanit.utils.network.layer.physical.CompiledRelationMapping;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompiledRelationIndexTest {

  @Test
  void test_secondaryKeyBased_and_indexBased_lookup() {

    /*
     * PrimaryKey (inIndex):
     *
     * 0 -> secondaryKeys [1, 2]   -> values [100, 101]
     * 1 -> secondaryKeys [2]      -> values [200]
     * 2 -> null
     */

    long[][] secondaryKeys = new long[][] {
        {1L, 2L},  // primaryKey 0
        {2L},      // primaryKey 1
        null       // primaryKey 2
    };

    long[][] values = new long[][] {
        {100L, 101L}, // primaryKey 0
        {200L},       // primaryKey 1
        null          // primaryKey 2
    };

    var compiled = new CompiledRelationIndex(
        secondaryKeys,
        values,
        3
    );

    // ------------------------------------------------------------
    // Secondary-key lookups (slow path)
    // ------------------------------------------------------------
    assertEquals(100L, compiled.get(0L, 1L));
    assertEquals(101L, compiled.get(0L, 2L));
    assertEquals(200L, compiled.get(1L, 2L));

    assertEquals(CompiledRelationIndex.BANNED,
        compiled.get(0L, 999L));

    assertEquals(CompiledRelationIndex.BANNED,
        compiled.get(2L, 1L));

    // ------------------------------------------------------------
    // INDEX-based lookups (fast path)
    // ------------------------------------------------------------
    assertEquals(100L, compiled.getByIndex(0L, 0));
    assertEquals(101L, compiled.getByIndex(0L, 1));
    assertEquals(200L, compiled.getByIndex(1L, 0));

    assertEquals(CompiledRelationIndex.BANNED, compiled.getByIndex(1L, 5));
    assertEquals(CompiledRelationIndex.BANNED, compiled.getByIndex(2L, 0));
  }

  @Test
  void test_sparse_and_missing_rows() {

    long[][] secondaryKeys = new long[4][];
    long[][] values = new long[4][];

    secondaryKeys[0] = new long[] {1L};
    values[0] = new long[] {10L};

    // row 1 missing
    secondaryKeys[2] = new long[] {0L, 1L};
    values[2] = new long[] {20L, 21L};

    secondaryKeys[3] = new long[] {};
    values[3] = new long[] {};

    CompiledRelationIndex compiled = new CompiledRelationIndex(
        secondaryKeys,
        values,
        3
    );

    assertEquals(10L, compiled.get(0L, 1L));
    assertEquals(20L, compiled.get(2L, 0L));
    assertEquals(21L, compiled.get(2L, 1L));

    assertEquals(CompiledRelationIndex.BANNED, compiled.get(1L, 0L));
    assertEquals(CompiledRelationIndex.BANNED, compiled.get(3L, 0L));
  }

  @Test
  void test_index_lookup_consistency() {

    long[][] secondaryKeys = new long[][] {
        {10L, 11L, 12L}
    };

    long[][] values = new long[][] {
        {100L, 101L, 102L}
    };

    CompiledRelationIndex compiled = new CompiledRelationIndex(
        secondaryKeys,
        values,
        3
    );

    // verify index consistency indirectly via getByIndex ordering
    assertEquals(100L, compiled.getByIndex(0L, 0));
    assertEquals(101L, compiled.getByIndex(0L, 1));
    assertEquals(102L, compiled.getByIndex(0L, 2));

    assertEquals(CompiledRelationIndex.BANNED, compiled.getByIndex(0L, 99));
  }

  @Test
  void testCopyPublicApi() {
    long[][] secondaryKeys = new long[][] {
        {1L, 2L},
        {3L},
        null
    };

    long[][] values = new long[][] {
        {10L, 11L},
        {20L},
        null
    };

    long size = 3;

    CompiledRelationIndex original = new CompiledRelationIndex(secondaryKeys, values, size);
    CompiledRelationIndex copy = original.copy();

    // Contents should match using public API
    assertEquals(original.size(), copy.size());

    assertEquals(original.get(0L, 1L), copy.get(0L, 1L));
    assertEquals(original.get(0L, 2L), copy.get(0L, 2L));
    assertEquals(original.get(1L, 3L), copy.get(1L, 3L));
    assertEquals(original.get(2L, 0L), copy.get(2L, 0L));

    // Index-based lookup
    assertEquals(original.getByIndex(0L, 0), copy.getByIndex(0L, 0));
    assertEquals(original.getByIndex(0L, 1), copy.getByIndex(0L, 1));
    assertEquals(original.getByIndex(1L, 0), copy.getByIndex(1L, 0));

    // Secondary key lookup
    assertEquals(original.getSecondaryKeyByIndex(0L, 0), copy.getSecondaryKeyByIndex(0L, 0));
    assertEquals(original.getSecondaryKeyByIndex(0L, 1), copy.getSecondaryKeyByIndex(0L, 1));

    // Modifying the copy (new instance) does not affect original
    long[][] newSecondaryKeys = new long[][] { {99L} };
    long[][] newValues = new long[][] { {999L} };
    CompiledRelationIndex modifiedCopy = new CompiledRelationIndex(newSecondaryKeys, newValues, 1);

    assertEquals(10L, original.get(0L, 1L), "Original should remain unchanged");
    assertEquals(999L, modifiedCopy.get(0L, 99L), "Modified copy should reflect new data");
  }
}