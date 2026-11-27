package org.goplanit.utils;

import org.goplanit.utils.containers.LongKeyArrayMap;
import org.goplanit.utils.wrapper.MapWrapperImpl;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.LongSupplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests of {@link LongKeyArrayMap}, each against a TreeMap given the same changes, as the map is meant to take a
 * TreeMap's place without anything else changing
 */
public class LongKeyArrayMapTest {

  /**
   * Apply the same changes to a TreeMap and a LongKeyArrayMap and assert they hold the same, in the same order
   *
   * @param changes to apply to each
   * @return the LongKeyArrayMap after the changes
   */
  private static LongKeyArrayMap<String> assertSameAsTreeMapAfter(Consumer<Map<Long, String>> changes) {
    var treeMap = new TreeMap<Long, String>();
    var map = new LongKeyArrayMap<String>();
    changes.accept(treeMap);
    changes.accept(map);

    assertSameEntries(treeMap, map);
    assertEquals(map, treeMap);
    assertEquals(treeMap.hashCode(), map.hashCode());
    assertEquals(treeMap.size(), map.size());
    var visitedKeys = new ArrayList<Long>();
    map.forEach((key, value) -> visitedKeys.add(key));
    assertEquals(new ArrayList<>(treeMap.keySet()), visitedKeys);
    return map;
  }

  /**
   * Remove, through the map's entry iterator, the first entry with a key at or above the given one, if any
   *
   * @param map to remove from
   * @param key from which to look
   */
  private static void removeFirstEntryFrom(Map<Long, String> map, long key) {
    var entries = map.entrySet().iterator();
    while (entries.hasNext()) {
      if (entries.next().getKey() >= key) {
        entries.remove();
        return;
      }
    }
  }

  /**
   * Assert a TreeMap and a LongKeyArrayMap hold the same entries in the same order
   *
   * @param treeMap expected
   * @param map to check
   */
  private static void assertSameEntries(Map<Long, String> treeMap, Map<Long, String> map) {
    assertEquals(treeMap, map);
    assertEquals(new ArrayList<>(treeMap.keySet()), new ArrayList<>(map.keySet()));
    assertEquals(new ArrayList<>(treeMap.values()), new ArrayList<>(map.values()));
  }

  /** Puts in and out of key order, a replaced value and removals give what a TreeMap gives, in key order */
  @Test
  public void testSameAsTreeMapForKeysFromZero() {
    var map = assertSameAsTreeMapAfter(m -> {
      for (long key = 0; key < 100; ++key) {
        m.put(key, "v" + key);
      }
      m.put(250L, "v250");
      m.put(120L, "v120");
      m.put(5L, "replaced");
      m.remove(7L);
      m.remove(99L);
      m.remove(1000L);
    });
    assertEquals("replaced", map.get(5L));
    assertNull(map.get(7L));
    assertNull(map.get(-1L));
    assertNull(map.get(Long.MAX_VALUE));
    assertTrue(map.containsKey(250L));
    assertFalse(map.containsKey(7L));
  }

  /**
   * Keys the array cannot hold, below 0, far beyond the others or beyond what an array can hold, still give what a
   * TreeMap gives, alone or among keys that fit
   */
  @Test
  public void testSameAsTreeMapForKeysNotFittingTheArray() {
    assertSameAsTreeMapAfter(m -> {
      m.put(3L, "v3");
      m.put(-4L, "minus");
      m.put(1L, "v1");
    });
    assertSameAsTreeMapAfter(m -> {
      m.put(0L, "v0");
      m.put(1_000_000L, "far");
      m.put(2L, "v2");
      m.remove(0L);
    });
    var map = assertSameAsTreeMapAfter(m -> {
      m.put(Long.MAX_VALUE, "max");
      m.put(Long.MIN_VALUE, "min");
    });
    assertEquals("max", map.get(Long.MAX_VALUE));

    map.clear();
    assertTrue(map.isEmpty());
    map.put(2L, "after clear");
    assertEquals(List.of(2L), new ArrayList<>(map.keySet()));
  }

  /** Keys 10 apart, which the array holds, and 1,000 apart, which go to the tree map, give what a TreeMap gives */
  @Test
  public void testSameAsTreeMapForSpreadKeys() {
    assertSameAsTreeMapAfter(m -> {
      for (long key = 0; key < 1_000; key += 10) {
        m.put(key, "v" + key);
      }
    });
    assertSameAsTreeMapAfter(m -> {
      for (long key = 0; key < 100_000; key += 1_000) {
        m.put(key, "v" + key);
      }
    });
  }

  /** A null value is held, as a TreeMap holds it, and a null key is refused, as a TreeMap refuses it */
  @Test
  public void testNullValueHeldNullKeyRefused() {
    var map = assertSameAsTreeMapAfter(m -> {
      m.put(3L, null);
      m.put(4L, "v4");
    });
    assertTrue(map.containsKey(3L));
    assertNull(map.get(3L));
    assertEquals(2, map.size());

    assertThrows(NullPointerException.class, () -> map.get(null));
    assertThrows(NullPointerException.class, () -> map.put(null, "v"));
    assertThrows(ClassCastException.class, () -> map.get(4));
  }

  /**
   * Removing through an iterator removes the entry, setting a value through an entry sets it in the map, and a change
   * made other than through the iterator stops the iteration, as a TreeMap's does
   */
  @Test
  public void testIteratorRemoveSetValueAndChangeDuringIteration() {
    var map = assertSameAsTreeMapAfter(m -> {
      for (long key = 0; key < 10; ++key) {
        m.put(key, "v" + key);
      }
      var entries = m.entrySet().iterator();
      while (entries.hasNext()) {
        var entry = entries.next();
        if (entry.getKey() % 2 == 0) {
          entries.remove();
        } else {
          entry.setValue("odd" + entry.getKey());
        }
      }
      m.replaceAll((key, value) -> value + "!");
    });
    assertEquals("odd3!", map.get(3L));

    var values = map.values().iterator();
    values.next();
    map.put(100L, "v100");
    assertThrows(ConcurrentModificationException.class, values::next);

    var keys = map.keySet().iterator();
    keys.next();
    map.put(-1L, "into the tree map");
    assertThrows(ConcurrentModificationException.class, keys::next);
  }

  /** A map wrapper copied makes an empty map of the same kind and copies the entries, spread keys included */
  @Test
  public void testCopiedByMapWrapper() {
    var map = new LongKeyArrayMap<String>();
    var wrapper = new MapWrapperImpl<>(map, (String value) -> Long.valueOf(value.substring(1)));
    for (long key = 0; key < 1_000; key += 10) {
      wrapper.register("v" + key);
    }

    var copy = new MapWrapperImpl<>(wrapper);
    var copiedValues = new ArrayList<String>();
    copy.forEach(copiedValues::add);
    assertEquals(new ArrayList<>(map.values()), copiedValues);
    assertEquals("v990", copy.get(990L));
  }

  /**
   * Ids rising from a high start, as transfer zones' after the OD zones', stay in the array, as do ids from 0, and so
   * does a copy; an id below the first one, or far beyond the others, moves the entries to the tree map; once emptied,
   * the map counts from the next id it holds. The entries are a TreeMap's in every case
   */
  @Test
  public void testIdsRisingFromHighStartKeptInArray() {
    var fromHighStart = assertSameAsTreeMapAfter(m -> {
      for (long key = 3_378; key < 35_739; ++key) {
        m.put(key, "v" + key);
      }
    });
    assertTrue(fromHighStart.keepsEntriesInArray());
    var copy = new LongKeyArrayMap<String>();
    copy.putAll(fromHighStart);
    assertTrue(copy.keepsEntriesInArray());
    assertSameEntries(fromHighStart, copy);

    var fromZero = assertSameAsTreeMapAfter(m -> {
      for (long key = 0; key < 1_000; key += 10) {
        m.put(key, "v" + key);
      }
    });
    assertTrue(fromZero.keepsEntriesInArray());

    var belowFirst = assertSameAsTreeMapAfter(m -> {
      m.put(3_378L, "first");
      m.put(3_377L, "below");
    });
    assertFalse(belowFirst.keepsEntriesInArray());

    var farBeyond = assertSameAsTreeMapAfter(m -> {
      m.put(0L, "v0");
      m.put(1_000_000L, "far");
    });
    assertFalse(farBeyond.keepsEntriesInArray());

    var emptied = new LongKeyArrayMap<String>();
    emptied.put(5_000L, "first");
    emptied.remove(5_000L);
    emptied.put(10L, "after emptied");
    assertTrue(emptied.keepsEntriesInArray());
    assertEquals("after emptied", emptied.get(10L));
    assertNull(emptied.get(5_000L));
  }

  /**
   * Many random puts, removals, removals through an iterator, copies and clears give what a TreeMap gives after every
   * change, for ids from 0, ids rising from a high start with some earlier ids again, ids around 0 with negatives, ids
   * anywhere in the long range, and two runs of ids far apart
   */
  @Test
  public void testSameAsTreeMapForRandomChanges() {
    var random = new Random(42);
    long[] lastRising = {3_377};
    List<LongSupplier> keyPatterns = List.of(
        () -> random.nextInt(2_000),
        () -> lastRising[0] >= 3_378 && random.nextInt(10) == 0
            ? 3_378 + random.nextInt((int) (lastRising[0] - 3_377))
            : (lastRising[0] = lastRising[0] < 3_378 ? 3_378 : lastRising[0] + 1 + random.nextInt(3)),
        () -> random.nextInt(2_000) - 1_000,
        random::nextLong,
        () -> random.nextBoolean() ? random.nextInt(100) : 1_000_000 + random.nextInt(100));

    for (var nextKey : keyPatterns) {
      Map<Long, String> treeMap = new TreeMap<>();
      Map<Long, String> map = new LongKeyArrayMap<>();
      for (int change = 0; change < 20_000; ++change) {
        long key = nextKey.getAsLong();
        int choice = random.nextInt(1_000);
        if (choice < 650) {
          assertEquals(treeMap.put(key, "v" + change), map.put(key, "v" + change));
        } else if (choice < 950) {
          assertEquals(treeMap.remove(key), map.remove(key));
        } else if (choice < 990) {
          removeFirstEntryFrom(treeMap, key);
          removeFirstEntryFrom(map, key);
        } else if (choice < 998) {
          var copy = new LongKeyArrayMap<String>();
          copy.putAll(map);
          map = copy;
        } else {
          treeMap.clear();
          map.clear();
        }
        assertEquals(treeMap.get(key), map.get(key));
        assertEquals(treeMap.containsKey(key), map.containsKey(key));
        assertEquals(treeMap.size(), map.size());
        if (change % 1_000 == 0) {
          assertSameEntries(treeMap, map);
        }
      }
      assertSameEntries(treeMap, map);
    }
  }
}
