package org.goplanit.utils;

import org.goplanit.utils.misc.CloneUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests of {@link CloneUtils}: the values of a map copied into another, each one cloned, by the cloner given or by
 * serialising it
 *
 * @author markr
 */
public class CloneUtilsTest {

  /** With a cloner, each value of the source map is put in the destination map as the clone the cloner gives */
  @Test
  public void testValuesClonedByTheClonerGiven() {
    var source = new LinkedHashMap<String, String>();
    source.put("a", "1");
    source.put("b", "2");
    var destination = new TreeMap<String, String>();

    var result = CloneUtils.deepCloneFromTo(source, destination, value -> value + " cloned");

    assertSame(destination, result);
    assertEquals(Map.of("a", "1 cloned", "b", "2 cloned"), destination);
    assertEquals(Map.of("a", "1", "b", "2"), source);
  }

  /** Without a cloner, each value is cloned by serialising it, giving an equal value that is not the same object */
  @Test
  public void testValuesClonedBySerialising() {
    var source = Map.of("a", new ArrayList<>(List.of("1", "2")));
    var destination = new TreeMap<String, ArrayList<String>>();

    CloneUtils.deepCloneFromTo(source, destination);

    assertEquals(source, destination);
    assertNotSame(source.get("a"), destination.get("a"));
  }
}
