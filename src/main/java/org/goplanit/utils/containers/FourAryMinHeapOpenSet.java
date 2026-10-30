package org.goplanit.utils.containers;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * A 4-ary heap open set implementation which is more efficient than PriorityQueue for large path finding
 * exercises such as A*
 * <p>
 *   It is assumed the number of vertices passed in at construction is the absolute maximum and the ids of
 *   the vertices are contiguous and increasing from 0-max
 * </p>
 */
public final class FourAryMinHeapOpenSet {

  /** store vertex ids */
  private final int[] heap;

  /** position[vertexId] = index in heap, or -1 if not in heap */
  private final int[] vertexHeapPosition;

  /** score[vertexId] = fScore (g + h) for that vertex */
  private final double[] vertexScores;
  private int size = 0;

  private int maxHeapSize;

  /**
   * Move provided index up (closer to zero position) in the heap until it is in the correct sorted position.
   * Do so by comparing to its parent and if it is cheaper swap position which guarantees the tree is valid if repeated
   * until a parent is found that is no longer cheaper
   *
   * @param heapIndex to use
   */
  private void siftUp(int heapIndex) {
    final int[] localHeapRef = heap;
    final double[] vertexScoresRef = vertexScores;
    final int[] vertexHeapPositionRef = vertexHeapPosition;

    // 1. Store the vertex we are moving.
    int vertexIndex = localHeapRef[heapIndex];
    // 2. Cache its score (f-score) for comparisons.
    double vertexScore = vertexScoresRef[vertexIndex];

    // 3. Stop if we reach the root (index 0).
    while (heapIndex > 0) {
      // 4-ARY HEAP: parent index = (i-1)/4
      final int parent = (heapIndex - 1) >>> 2;

      // 5. Get the vertex ID of the parent.
      final int parentVertex = localHeapRef[parent];
      final double parentScore = vertexScoresRef[parentVertex];

      // 6. If the parent is already smaller/equal, the heap is valid. Stop.
      if (parentScore <= vertexScore) {
        break;
      }

      // 7. Otherwise, pull the parent down into our current slot.
      localHeapRef[heapIndex] = parentVertex;
      // Update parent's position tracker.
      vertexHeapPositionRef[parentVertex] = heapIndex;

      // 8. Move our target "index" up to where the parent used to be.
      heapIndex = parent;
    }

    // 9. Final placement: Put our original vertex into its final correct slot.
    localHeapRef[heapIndex] = vertexIndex;
    vertexHeapPositionRef[vertexIndex] = heapIndex;
  }

  /**
   * Move provided index down (farther from zero position) in the heap until it is in the correct sorted position.
   * Do so by comparing to its children and if it is more expensive swap position which guarantees the tree is valid
   * if repeated until children are found that are no longer more expensive
   *
   * @param heapIndex to use
   */
  private void siftDown(int heapIndex) {
    final int[] localHeapRef = heap;
    final double[] vertexScoresRef = vertexScores;
    final int[] vertexHeapPositionRef = vertexHeapPosition;

    // 1. Store the vertex we are moving.
    final int vertexIndex = localHeapRef[heapIndex];
    // 2. Cache its score.
    final double vertexScore = vertexScoresRef[vertexIndex];

    // 4-ARY HEAP: we'll break when there are no children
    while (true) {
      // 4-ARY HEAP: first child index = 4*i + 1
      final int firstChild = (heapIndex << 2) + 1;
      if (firstChild >= size) {
        // no children -> we're at a leaf
        break;
      }

      int smallestChildIdx = firstChild;
      int smallestVertex = localHeapRef[firstChild];
      double smallestScore = vertexScoresRef[smallestVertex];

      // second child = firstChild + 1
      final int secondChild = firstChild + 1;
      if (secondChild < size) {
        final int v2 = localHeapRef[secondChild];
        final double s2 = vertexScoresRef[v2];
        if (s2 < smallestScore) {
          smallestScore = s2;
          smallestChildIdx = secondChild;
          smallestVertex = v2;
        }
      }

      // third child = firstChild + 2
      final int thirdChild = firstChild + 2;
      if (thirdChild < size) {
        final int v3 = localHeapRef[thirdChild];
        final double s3 = vertexScoresRef[v3];
        if (s3 < smallestScore) {
          smallestScore = s3;
          smallestChildIdx = thirdChild;
          smallestVertex = v3;
        }
      }

      // fourth child = firstChild + 3
      final int fourthChild = firstChild + 3;
      if (fourthChild < size) {
        final int v4 = localHeapRef[fourthChild];
        final double s4 = vertexScoresRef[v4];
        if (s4 < smallestScore) {
          smallestScore = s4;
          smallestChildIdx = fourthChild;
          smallestVertex = v4;
        }
      }

      // 10. If our moving node is already smaller than or equal to the smallest child, stop.
      if (vertexScore <= smallestScore) {
        break;
      }

      // 11. Otherwise, pull the smallest child up into our current slot.
      localHeapRef[heapIndex] = smallestVertex;
      vertexHeapPositionRef[smallestVertex] = heapIndex;

      // 12. Move our target "index" down to the child's old slot.
      heapIndex = smallestChildIdx;
    }

    // 13. Final placement: Put our original vertex into its final correct slot.
    localHeapRef[heapIndex] = vertexIndex;
    vertexHeapPositionRef[vertexIndex] = heapIndex;
  }

  /**
   * Constructor
   *
   * @param numberOfVertices on heap at maximum
   */
  public FourAryMinHeapOpenSet(int numberOfVertices) {
    this.heap = new int[numberOfVertices];
    this.vertexHeapPosition = new int[numberOfVertices];
    this.vertexScores = new double[numberOfVertices];
    reset();
  }

  /**
   * Reset so it can be reused if number of vertices remains unchanged
   */
  public void reset() {
    size = 0;
    // ok to leave heap polluted as size governs "clean" portion
    Arrays.fill(vertexHeapPosition, -1);
    Arrays.fill(vertexScores, Double.POSITIVE_INFINITY);
  }

  /**
   * Check if heap is empty
   *
   * @return true when empty, false otherwise
   */
  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * Insert the vertex with given score, or decrease its score if the newScore is better.
   *
   * @param vertexId the vertex id to add or reduce score for
   * @param newScore to use
   */
  public void insertOrDecrease(int vertexId, double newScore) {
    final int pos = vertexHeapPosition[vertexId];
    // New vertex in heap
    if (pos == -1) {
      heap[size] = vertexId;
      vertexHeapPosition[vertexId] = size;
      vertexScores[vertexId] = newScore;
      // move new entry up until it is in the right position
      siftUp(size);
      size++;
      maxHeapSize = Math.max(maxHeapSize, size);
    } else if (newScore < vertexScores[vertexId]) {
      vertexScores[vertexId] = newScore;
      // move new entry up until it is in the righ position
      siftUp(pos);
    }
    // If newScore >= old score, do nothing (existing is better)
  }

  /**
   * Poll the vertexId with the smallest score, i.e., highest priority.
   *
   * @return provide highest priority vertex id from the prioritised heap
   */
  public int poll() {
    if (size == 0) {
      throw new NoSuchElementException("Heap is empty");
    }

    // 2. Grab the vertex at index 0 (the root), which is always the minimum in a min-heap.
    final int minVertexId = heap[0];
    // 3. Decrement the size of the heap.
    size--;

    // 4. If the heap isn't empty after removing the root, we need to fill the hole.
    if (size > 0) {
      // Move the very last element in the heap to the root position such that we remain a heap
      final int newHeapZeroVertexId = heap[size];
      heap[0] = newHeapZeroVertexId;
      // Update the position tracker for that moved element so we know it's now at index 0.
      vertexHeapPosition[newHeapZeroVertexId] = 0;
      // Push the new root down to its correct sorted position to restore heap properties.
      siftDown(0);
    }

    // 5. Mark the original minimum vertex as "not in heap" by setting its position to -1.
    vertexHeapPosition[minVertexId] = -1;
    // 6. Return the ID of the vertex we just removed.
    return minVertexId;
  }

  /**
   * Get score for vertex
   * @param vertexId the vertex
   * @return score
   */
  public double getScore(int vertexId) {
    return vertexScores[vertexId];
  }

  public int getHeapSize(){
    return size;
  }

  public int getMaxHeapSize(){
    return maxHeapSize;
  }
}
