package org.goplanit.utils;

import org.goplanit.utils.containers.FourAryMinHeapOpenSet;
import org.goplanit.utils.misc.Pair;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionsTest {
  @Test
  public void FourAryMinHeapTest(){
    int numVertices = 10;

    PriorityQueue<Pair<Integer,Double>> referenceContainer =
            new PriorityQueue<>(Comparator.comparingDouble(Pair::second));
    FourAryMinHeapOpenSet binaryHeap = new FourAryMinHeapOpenSet(numVertices);

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
    values.forEach(e -> binaryHeap.insertOrDecrease(e.first(), e.second()));

    while(!referenceContainer.isEmpty()) {
      var next = referenceContainer.poll();
      assert(!binaryHeap.isEmpty());
      var nextIntInHeap = binaryHeap.poll();
      double nextInHeapValue = values.get(nextIntInHeap).second();
      assertEquals(next.first(), nextIntInHeap);
      assertEquals(next.second(), nextInHeapValue);
    }
    assert(binaryHeap.isEmpty());

  }
}
