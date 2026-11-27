package org.goplanit.utils;

import org.goplanit.utils.containers.ContainerUtils;
import org.goplanit.utils.containers.FourAryMinHeapOpenSet;
import org.goplanit.utils.containers.ListUtils;
import org.goplanit.utils.containers.MapUtils;
import org.goplanit.utils.exceptions.PlanItRunTimeException;
import org.goplanit.utils.misc.CollectionUtils;
import org.goplanit.utils.misc.Pair;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.PriorityQueue;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionsTest {

  /**
   * A number goes once the number before it is gone, 1 excepted, so 5 goes in the first pass and 6, whose number before
   * it was still there then, in the second
   */
  @Test
  public void removeUntilNoneMatchesRepeatsPassesUntilNoneGoes() {
    var numbers = new LinkedHashSet<>(List.of(1, 2, 3, 5, 6));

    boolean removed = ContainerUtils.removeUntilNoneMatches(numbers, () -> {
      var atStartOfPass = Set.copyOf(numbers);
      return number -> number > 1 && !atStartOfPass.contains(number - 1);
    });

    assertTrue(removed);
    assertEquals(Set.of(1, 2, 3), numbers);
    assertFalse(ContainerUtils.removeUntilNoneMatches(numbers, () -> number -> number > 3));
  }
  @Test
  public void FourAryMinHeapTest(){
    int numVertices = 10;

    PriorityQueue<Pair<Integer,Double>> referenceContainer =
            new PriorityQueue<>(Comparator.comparingDouble(Pair::second));
    FourAryMinHeapOpenSet fourAryHeap = new FourAryMinHeapOpenSet(numVertices);

    List<Pair<Integer,Double>> values = List.of(
      Pair.of(0,10.0),
      Pair.of(1,14.3),
      Pair.of(2,90000.0),
      Pair.of(3,3.0),
      Pair.of(4,4.0),
      Pair.of(5,5.0),
      Pair.of(6,6.0),
      Pair.of(7,7.0),
      Pair.of(8,8.0),
      Pair.of(9,999999999.0));

    referenceContainer.addAll(values);
    values.forEach(e -> fourAryHeap.insertOrDecrease(e.first(), e.second()));

    while(!referenceContainer.isEmpty()) {
      var next = referenceContainer.poll();
      assert(!fourAryHeap.isEmpty());
      var nextIntInHeap = fourAryHeap.poll();
      double nextInHeapValue = values.get(nextIntInHeap).second();
      assertEquals(next.first(), nextIntInHeap);
      assertEquals(next.second(), nextInHeapValue);
    }
    assert(fourAryHeap.isEmpty());

  }

  /**
   * Entries a to e with the pairs (b, d), (d, a) and (c, c) give the disjoint sets {a, b, d}, {c} and {e}: joined
   * entries chain, an entry joined only to itself or to nothing is a set of its own, and the sets come in the order of
   * their first entry, each in the order of the entries
   */
  @Test
  public void extractDisjointSets() {
    var entries = List.of("a", "b", "c", "d", "e");
    var joinedPairs = List.of(Pair.of("b", "d"), Pair.of("d", "a"), Pair.of("c", "c"));

    var disjointSets = ListUtils.extractDisjointSets(entries, joinedPairs);

    assertEquals(List.of(List.of("a", "b", "d"), List.of("c"), List.of("e")),
        disjointSets.stream().map(List::copyOf).collect(Collectors.toList()));
  }

  /**
   * Each number is kept when it and the numbers kept before it sum to at most 8. So 4 is left out after 5, while 2
   * and 1 after it are still kept
   */
  @Test
  public void filterEachTogetherWithThoseKeptJudgesEachWithThoseKeptBefore() {
    var kept = ListUtils.filterEachTogetherWithThoseKept(List.of(5, 4, 2, 1),
        numbers -> numbers.stream().mapToInt(Integer::intValue).sum() <= 8);

    assertEquals(List.of(5, 2, 1), kept);
  }

  /** An array of a primitive type holds the values unboxed, and no values give an empty array */
  @Test
  public void toArrayOfComponentTypeTakesPrimitiveTypes() {
    assertArrayEquals(new long[] {1, 2}, (long[]) CollectionUtils.toArray(List.of(1L, 2L), long.class));
    assertEquals(0, ((String[]) CollectionUtils.toArray(List.of(), String.class)).length);
  }

  /**
   * The values of an array in order, those of a primitive type boxed, the reverse of making the array, and the values
   * of a collection in its order; nothing else
   */
  @Test
  public void toListTakesArraysOfPrimitiveTypesAndCollections() {
    assertEquals(List.of(1L, 2L), CollectionUtils.toList(new long[] {1, 2}));
    assertEquals(List.of("a", "b"), CollectionUtils.toList(new String[] {"a", "b"}));
    assertEquals(List.of(), CollectionUtils.toList(new int[0]));
    assertEquals(List.of("a", "b"), CollectionUtils.toList(new TreeSet<>(List.of("b", "a"))));
    assertThrows(IllegalArgumentException.class, () -> CollectionUtils.toList("neither an array nor a collection"));
  }

  /** A list for a List or Collection, a set for a Set, both in the order of the values; no other kind */
  @Test
  public void toCollectionAssignableToMakesAListOrASet() {
    assertEquals(ArrayList.class, CollectionUtils.toCollectionAssignableTo(List.of("a"), List.class).getClass());
    assertEquals(ArrayList.class, CollectionUtils.toCollectionAssignableTo(List.of("a"), Iterable.class).getClass());
    var set = CollectionUtils.toCollectionAssignableTo(List.of("b", "a", "b"), Set.class);
    assertTrue(set instanceof Set);
    assertEquals(List.of("b", "a"), new ArrayList<>(set));
    assertThrows(PlanItRunTimeException.class,
        () -> CollectionUtils.toCollectionAssignableTo(List.of("a"), TreeSet.class));
  }

  /** The values as a set in the order given, each once */
  @Test
  public void toOrderedSetKeepsTheOrderGiven() {
    assertEquals(List.of(3L, 1L), new ArrayList<>(CollectionUtils.toOrderedSet(3, 1, 3)));
  }

  /** Neither level of a map of maps can be changed through it, while a later change to an inner map shows */
  @Test
  public void unmodifiableMapOfMapsRefusesChangesAtBothLevels() {
    var inner = new TreeMap<>(Map.of(1L, "a"));
    var view = MapUtils.unmodifiableMapOfMaps(new TreeMap<>(Map.of("x", inner)));

    assertThrows(UnsupportedOperationException.class, () -> view.put("y", Map.of()));
    assertThrows(UnsupportedOperationException.class, () -> view.get("x").put(2L, "b"));
    inner.put(2L, "b");
    assertEquals(Map.of(1L, "a", 2L, "b"), view.get("x"));
  }

  /** The items meeting the condition are removed and given back in the order they were held; none, nothing changes */
  @Test
  public void removeMatchingRemovesAndGivesBackWhatMatches() {
    var numbers = new LinkedHashSet<>(List.of(1, 2, 3, 4, 5, 6));

    assertEquals(List.of(2, 4, 6), ContainerUtils.removeMatching(numbers, number -> number % 2 == 0));
    assertEquals(Set.of(1, 3, 5), numbers);
    assertTrue(ContainerUtils.removeMatching(numbers, number -> number > 5).isEmpty());
    assertEquals(Set.of(1, 3, 5), numbers);
  }

  /** The index of the first entry meeting a condition is found, -1 when no entry meets it */
  @Test
  public void findIndexOfFirstMatchingGivesTheFirstOrMinusOne() {
    var numbers = List.of(3, 8, 5, 10);

    assertEquals(1, ListUtils.findIndexOfFirstMatching(numbers, number -> number % 2 == 0));
    assertEquals(-1, ListUtils.findIndexOfFirstMatching(numbers, number -> number > 10));
  }

  /** The union holds each element of any of the sets once, an empty set adding nothing, and refuses changes */
  @Test
  public void unmodifiableUnionOfHoldsEachElementOnceAndRefusesChanges() {
    Set<Number> union = ContainerUtils.unmodifiableUnionOf(Set.of(1, 2), Set.of(2, 3), Set.of(4.5), Set.of());

    assertEquals(Set.of(1, 2, 3, 4.5), union);
    assertThrows(UnsupportedOperationException.class, () -> union.add(5));
  }

  /**
   * Each entry held by two or more owners is given with those owners, in the order of the owners; an entry only one
   * owner holds is not. For example, sections a, b and c holding link segments 1 and 2, 2 and 3, and 3, 4 and 2
   */
  @Test
  public void findEntriesSharedBetweenGivesEachSharedEntryWithItsOwners() {
    var entriesByOwner = new LinkedHashMap<String, List<Integer>>();
    entriesByOwner.put("a", List.of(1, 2));
    entriesByOwner.put("b", List.of(2, 3));
    entriesByOwner.put("c", List.of(3, 4, 2));

    var shared = ContainerUtils.findEntriesSharedBetween(entriesByOwner.keySet(), entriesByOwner::get);
    assertEquals(Map.of(2, List.of("a", "b", "c"), 3, List.of("b", "c")), shared);
    assertTrue(ContainerUtils.findEntriesSharedBetween(List.of("a"), entriesByOwner::get).isEmpty());
  }

  /** An entry is added right after one found by identity, at the end when that is not found, and never twice */
  @Test
  public void addAfterAddsRightAfterTheExistingEntry() {
    var list = new ArrayList<>(List.of("a", "b", "c"));
    assertTrue(ListUtils.addAfter(list, "a", "x"));
    assertTrue(ListUtils.addAfter(list, "z", "y"));
    assertFalse(ListUtils.addAfter(list, "c", "x"));
    assertEquals(List.of("a", "x", "b", "c", "y"), list);
  }

  /** Each unordered pair of different entries once, in the order of the list; a list of one or none has no pair */
  @Test
  public void getPairCombinationsGivesEachPairOnce() {
    assertEquals(List.of(Pair.of("x", "y"), Pair.of("x", "z"), Pair.of("y", "z")),
        ListUtils.getPairCombinations(List.of("x", "y", "z")));
    assertTrue(ListUtils.getPairCombinations(List.of("x")).isEmpty());
    assertTrue(ListUtils.getPairCombinations(List.of()).isEmpty());
  }

  /**
   * Each entry is found at its position by identity, an equal entry that is another instance not at all, and an entry
   * held twice at its first position
   */
  @Test
  public void createIndexByIdentityGivesEachEntryItsPosition() {
    var first = "a";
    var equalButOther = new String(first);
    var second = "b";
    var index = ListUtils.createIndexByIdentity(List.of(first, second, first));
    assertEquals(0, index.get(first));
    assertEquals(1, index.get(second));
    assertNull(index.get(equalButOther));
    assertEquals(2, index.size());
  }
}
