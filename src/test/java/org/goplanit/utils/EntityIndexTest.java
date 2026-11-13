package org.goplanit.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.goplanit.utils.containers.EntityIndex;
import org.goplanit.utils.containers.IdentityEntityIndex;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the index of entities by the keys they refer to: lookup by identity, order of indexing, and staying in step
 * through update, replace and reindex
 *
 * @author markr
 */
public class EntityIndexTest {

  /** an entity referring to keys that can be changed */
  private static class Referrer {
    final List<Object> keys = new ArrayList<>();
    Referrer(Object... keys) { this.keys.addAll(Arrays.asList(keys)); }
  }

  private final EntityIndex<Object, Referrer> index = new IdentityEntityIndex<>(referrer -> referrer.keys);

  /** An entity is found once under each key it refers to, repeats and nulls ignored */
  @Test
  public void entityFoundOnceByEachKey() {
    var a = new Object();
    var b = new Object();
    var referrer = new Referrer(a, b, a, null);
    index.index(referrer);
    assertEquals(List.of(referrer), index.get(a));
    assertEquals(List.of(referrer), index.get(b));
  }

  /** Keys are compared by identity, not equality */
  @Test
  public void keysComparedByIdentity() {
    index.index(new Referrer(new String("key")));
    assertTrue(index.get(new String("key")).isEmpty());
  }

  /** The first indexed entity comes first, and the next is found once the first is unindexed */
  @Test
  public void nextEntityFoundOnceFirstIsUnindexed() {
    var key = new Object();
    var first = new Referrer(key);
    var second = new Referrer(key);
    index.index(first);
    index.index(second);
    assertSame(first, index.getFirst(key));
    index.unindex(first);
    assertSame(second, index.getFirst(key));
  }

  /** An update moves the entity to the keys it refers to afterwards */
  @Test
  public void updateMovesEntityToItsNewKeys() {
    var before = new Object();
    var after = new Object();
    var referrer = new Referrer(before);
    index.index(referrer);
    index.update(referrer, r -> r.keys.set(0, after));
    assertTrue(index.get(before).isEmpty());
    assertEquals(List.of(referrer), index.get(after));
  }

  /** Replacing an entity by itself keeps a single entry */
  @Test
  public void replacingWithItselfKeepsOneEntry() {
    var key = new Object();
    var referrer = new Referrer(key);
    index.index(referrer);
    index.replace(referrer, referrer);
    assertEquals(List.of(referrer), index.get(key));
  }

  /** A raw change is not seen until the index is rebuilt */
  @Test
  public void rawChangeSeenOnlyAfterReindex() {
    var before = new Object();
    var after = new Object();
    var referrer = new Referrer(before);
    index.index(referrer);
    referrer.keys.set(0, after);
    assertEquals(List.of(referrer), index.get(before));
    index.reindex(List.of(referrer));
    assertTrue(index.get(before).isEmpty());
    assertEquals(List.of(referrer), index.get(after));
  }
}
