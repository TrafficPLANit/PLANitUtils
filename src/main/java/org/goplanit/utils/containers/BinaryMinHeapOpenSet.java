package org.goplanit.utils.containers;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * A binary heap open set implementation which is more efficient than PriorityQueue for large path finding exercises
 * such as A*
 * <p>
 *   It is assumed the number of vertices passed in at construction is the absolute maximum and the ids of the vertices
 *   are contiguous and increasing from 0-max
 * </p>
 */
public final class BinaryMinHeapOpenSet {

  private final int[] heap;       // store vertex ids
  private final int[] vertexHeapPosition;   // position[vertexId] = index in heap, or -1 if not in heap
  private final double[] vertexScores;     // score[vertexId] = fScore (g + h) for that vertex
  private int size = 0;

  /**
   * Move provided index up (closer to zero position) in the heap until it is in the correct sorted position.
   * Do so by comparing to its parent and if it is cheaper swap position which guarantees the tree is valid if repeated
   * until a parent is found that is no longer cheaper
   *
   * @param heapIndex to use
   */
  private void siftUp(int heapIndex) {
    int vertexIndex = heap[heapIndex];                      // 1. Store the vertex we are moving.
    double vertexScore = vertexScores[vertexIndex];          // 2. Cache its score (f-score) for comparisons.

    while (heapIndex > 0) {                       // 3. Stop if we reach the root (index 0).
      int parent = (heapIndex - 1) >>> 1;         // 4. Calculate parent index: (idx-1)/2 using unsigned right hand shift
      int parentVertex = heap[parent];      // 5. Get the vertex ID of the parent.

      // 6. If the parent is already smaller/equal, the heap is valid. Stop.
      if (vertexScores[parentVertex] <= vertexScore) {
        break;
      }

      // 7. Otherwise, pull the parent down into our current slot.
      heap[heapIndex] = parentVertex;
      vertexHeapPosition[parentVertex] = heapIndex; // Update parent's position tracker.

      // 8. Move our target "index" up to where the parent used to be.
      heapIndex = parent;
    }

    // 9. Final placement: Put our original vertex into its final correct slot.
    heap[heapIndex] = vertexIndex;
    vertexHeapPosition[vertexIndex] = heapIndex;
  }

  /**
   * Move provided index down (farther from zero position) in the heap until it is in the correct sorted position.
   * Do so by comparing to its children and if it is more expensive swap position which guarantees the tree is valid
   * if repeated until children are found that are no longer more expensive
   *
   * @param heapIndex to use
   */
  private void siftDown(int heapIndex) {
    int vertexIndex = heap[heapIndex];               // 1. Store the vertex we are moving.
    double vertexScore = vertexScores[vertexIndex];  // 2. Cache its score.
    int half = size >>> 1;                            // 3. 'half' is the index of the last node with children (Unsigned Right Shift to do a /2)

    while (heapIndex < half) {                        // 4. While the node has at least one child...
      int left = (heapIndex << 1) + 1;                // 5. Left child index: (2*idx)+1.
      int right = left + 1;                           // 6. Right child index.
      int smallest = left;                            // 7. Assume left child is the smallest for now.
      int smallestVertex = heap[left];

      // 8. If right child exists AND is smaller than the left child...
      if (right < size && vertexScores[heap[right]] < vertexScores[smallestVertex]) {
        smallest = right;             // 9. ...the right child is the one we care about.
        smallestVertex = heap[right];
      }

      // 10. If our moving node is already smaller than the smallest child, stop.
      if (vertexScore <= vertexScores[smallestVertex]) {
        break;
      }

      // 11. Otherwise, pull the smallest child up into our current slot.
      heap[heapIndex] = smallestVertex;
      vertexHeapPosition[smallestVertex] = heapIndex;

      // 12. Move our target "index" down to the child's old slot.
      heapIndex = smallest;
    }

    // 13. Final placement: Put our original vertex into its final correct slot.
    heap[heapIndex] = vertexIndex;
    vertexHeapPosition[vertexIndex] = heapIndex;
  }

  /**
   * Constructor
   *
   * @param numberOfVertices on heap at maximum
   */
  public BinaryMinHeapOpenSet(int numberOfVertices) {
    this.heap = new int[numberOfVertices];
    this.vertexHeapPosition = new int[numberOfVertices];
    this.vertexScores = new double[numberOfVertices];
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
    int pos = vertexHeapPosition[vertexId];
    if (pos == -1) {                                // New vertex in heap
      heap[size] = vertexId;
      vertexHeapPosition[vertexId] = size;
      vertexScores[vertexId] = newScore;
      siftUp(size);                                 // move new entry up until it is in the righ position
      size++;
    } else if (newScore < vertexScores[vertexId]) { // Decrease score of existing
      vertexScores[vertexId] = newScore;
      siftUp(pos);                                  // move new entry up until it is in the righ position
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
    int minVertexId = heap[0];
    // 3. Decrement the size of the heap.
    size--;

    // 4. If the heap isn't empty after removing the root, we need to fill the hole.
    if (size > 0) {
      // Move the very last element in the heap to the root position such that we remain a heap
      heap[0] = heap[size];
      // Update the position tracker for that moved element so we know it's now at index 0.
      vertexHeapPosition[heap[0]] = 0;
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
  }
