package org.goplanit.utils;

import org.goplanit.utils.containers.ContainerUtils;
import org.goplanit.utils.containers.FourAryMinHeapOpenSet;
import org.goplanit.utils.containers.ListUtils;
import org.goplanit.utils.misc.Pair;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.PriorityQueue;
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
}
