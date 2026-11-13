package org.goplanit.utils;

import org.goplanit.utils.network.layer.physical.CompiledRelationIndex;
import org.goplanit.utils.network.layer.physical.CompiledRelationMapping;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** identical to {@link org.goplanit.utils.CompiledRelationIndexTest only now for generics}
 *
 */
class CompiledRelationMappingTest {

  @Test
  void test_secondaryKeyBased_and_indexBased_lookup() {

    /*
     * PrimaryKey (inIndex):
     *
     * 0 -> secondaryKeys [1, 2]   -> values [100, 101]
     * 1 -> secondaryKeys [2]      -> values [200]
     * 2 -> null
     */

    long[][] secondaryKeys = new long[][]{
        {1L, 2L},  // primaryKey 0
        {2L},      // primaryKey 1
        null       // primaryKey 2
    };

    Long[][] values = new Long[][]{
        {100L, 101L}, // primaryKey 0
        {200L},       // primaryKey 1
        null          // primaryKey 2
    };

    var compiled = new CompiledRelationMapping<>(Long.class, secondaryKeys, values, 3);

    // ------------------------------------------------------------
    // Secondary-key lookups (slow path)
    // ------------------------------------------------------------
    assertEquals(Long.valueOf(100L), compiled.get(0L, 1L));
    assertEquals(Long.valueOf(101L), compiled.get(0L, 2L));
    assertEquals(Long.valueOf(200L), compiled.get(1L, 2L));

    assertNull(compiled.get(0L, 999L));
    assertNull(compiled.get(2L, 1L));

    // ------------------------------------------------------------
    // INDEX-based lookups (fast path)
    // ------------------------------------------------------------
    assertEquals(Long.valueOf(100L), compiled.getByIndex(0L, 0));
    assertEquals(Long.valueOf(101L), compiled.getByIndex(0L, 1));
    assertEquals(Long.valueOf(200L), compiled.getByIndex(1L, 0));

    assertNull(compiled.getByIndex(1L, 5));
    assertNull(compiled.getByIndex(2L, 0));
  }

  @Test
  void test_sparse_and_missing_rows() {

    long[][] secondaryKeys = new long[4][];
    Long[][] values = new Long[4][];

    secondaryKeys[0] = new long[]{1L};
    values[0] = new Long[]{10L};

    // row 1 missing
    secondaryKeys[2] = new long[]{0L, 1L};
    values[2] = new Long[]{20L, 21L};

    secondaryKeys[3] = new long[]{};
    values[3] = new Long[]{};

    CompiledRelationMapping<Long> compiled = new CompiledRelationMapping<>(Long.class, secondaryKeys, values, 3);

    assertEquals(Long.valueOf(10L), compiled.get(0L, 1L));
    assertEquals(Long.valueOf(20L), compiled.get(2L, 0L));
    assertEquals(Long.valueOf(21L), compiled.get(2L, 1L));

    assertNull(compiled.get(1L, 0L));
    assertNull(compiled.get(3L, 0L));
  }

  @Test
  void test_index_lookup_consistency() {

    long[][] secondaryKeys = new long[][]{
        {10L, 11L, 12L}
    };

    Long[][] values = new Long[][]{
        {100L, 101L, 102L}
    };

    CompiledRelationMapping<Long> compiled = new CompiledRelationMapping<>(Long.class, secondaryKeys, values, 3);

    // verify index consistency indirectly via getByIndex ordering
    assertEquals(Long.valueOf(100L), compiled.getByIndex(0L, 0));
    assertEquals(Long.valueOf(101L), compiled.getByIndex(0L, 1));
    assertEquals(Long.valueOf(102L), compiled.getByIndex(0L, 2));

    assertNull(compiled.getByIndex(0L, 99));
  }

  @Test
  void testCopyPublicApi() {

    long[][] secondaryKeys = new long[][]{
        {1L, 2L},
        {3L},
        null
    };

    Long[][] values = new Long[][]{
        {10L, 11L},
        {20L},
        null
    };

    long size = 3;

    CompiledRelationMapping<Long> original = new CompiledRelationMapping<>(Long.class, secondaryKeys, values, size);
    CompiledRelationMapping<Long> copy = original.copy();

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
    Long[][] newValues = new Long[][]{{999L}};
    long[][] newSecondaryKeys = new long[][]{{99L}};
    CompiledRelationMapping<Long> modifiedCopy = new CompiledRelationMapping<>(Long.class, newSecondaryKeys, newValues, 1);

    assertEquals(Long.valueOf(10L), original.get(0L, 1L), "Original should remain unchanged");
    assertEquals(Long.valueOf(999L), modifiedCopy.get(0L, 99L), "Modified copy should reflect new data");
  }
}