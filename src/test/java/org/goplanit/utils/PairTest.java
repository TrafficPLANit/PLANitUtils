package org.goplanit.utils;

import org.goplanit.utils.misc.ComparablePair;
import org.goplanit.utils.misc.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PairTest {

  @Test
  public void pairTest(){

    assertFalse(Pair.of(1,2).equals(Pair.of(1,3)));
    assertTrue(Pair.of(1,2).equals(Pair.of(1,2)));
  }

  @Test
  public void ComparablePairTest(){

    assertEquals(ComparablePair.of(1,2).compareTo(ComparablePair.of(1,3)), -1);
    assertEquals(ComparablePair.of(1,2).compareTo(ComparablePair.of(1,2)), 0);
    assertEquals(ComparablePair.of(1,2).compareTo(ComparablePair.of(1,1)), 1);

    assertEquals(ComparablePair.of(1,2).compareTo(ComparablePair.of(null,1)), 1);
    assertEquals(ComparablePair.of(1,2).compareTo(ComparablePair.of(1,null)), 1);

    assertEquals(ComparablePair.of(null,2).compareTo(ComparablePair.of(1,2)), -1);
    assertEquals(ComparablePair.of(1,null).compareTo(ComparablePair.of(1,2)), -1);

    assertEquals(ComparablePair.of(1,null).compareTo(ComparablePair.of(1,null)), 0);
    assertEquals(ComparablePair.of(null,null).compareTo(ComparablePair.of(null,null)), 0);

  }
}
