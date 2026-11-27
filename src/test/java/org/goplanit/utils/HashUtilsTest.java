package org.goplanit.utils;

import org.goplanit.utils.id.ExternalIdAbleImpl;
import org.goplanit.utils.misc.HashUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Tests of {@link HashUtils}
 */
public class HashUtilsTest {

  /** The same parts in the same order give the same seed, the same parts in another order a different one */
  @Test
  public void testCombinedSeedDependsOnThePartsAndTheirOrder() {
    assertEquals(HashUtils.createCombinedSeed(42, 5, 7), HashUtils.createCombinedSeed(42, 5, 7));
    assertNotEquals(HashUtils.createCombinedSeed(42, 5, 7), HashUtils.createCombinedSeed(42, 7, 5));
  }

  /**
   * The id based seed of an entity is the 64-bit finaliser of its id, so the draws seeded by it keep their results.
   * The value is the one given for id 1 before the finaliser was shared
   */
  @Test
  public void testIdBasedSeedOfAnEntity() {
    assertEquals(-5451962507482445012L, new ExternalIdAbleImpl(1).generateIdBasedRandomSeed());
    assertEquals(HashUtils.mix64(1), new ExternalIdAbleImpl(1).generateIdBasedRandomSeed());
  }
}
