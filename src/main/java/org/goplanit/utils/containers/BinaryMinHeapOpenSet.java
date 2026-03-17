package org.goplanit.utils.containers;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * A binary heap open set implementation which is more efficient than PriorityQueue for large path finding exercises
 * such as A*
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
   * @param idx to use
   */
  private void siftUp(int idx) {
    int v = heap[idx];                      // 1. Store the vertex we are moving.
    double vKey = vertexScores[v];          // 2. Cache its score (f-score) for comparisons.

    while (idx > 0) {                       // 3. Stop if we reach the root (index 0).
      int parent = (idx - 1) >>> 1;         // 4. Calculate parent index: (idx-1)/2 using unsigned right hand shift
      int parentVertex = heap[parent];      // 5. Get the vertex ID of the parent.

      // 6. If the parent is already smaller/equal, the heap is valid. Stop.
      if (vertexScores[parentVertex] <= vKey) {
        break;
      }

      // 7. Otherwise, pull the parent down into our current slot.
      heap[idx] = parentVertex;
      vertexHeapPosition[parentVertex] = idx; // Update parent's position tracker.

      // 8. Move our target "index" up to where the parent used to be.
      idx = parent;
    }

    // 9. Final placement: Put our original vertex into its final correct slot.
    heap[idx] = v;
    vertexHeapPosition[v] = idx;
  }

  /**
   * Move provided index down (farther from zero position) in the heap until it is in the correct sorted position.
   * Do so by comparing to its children and if it is more expensive swap position which guarantees the tree is valid
   * if repeated until children are found that are no longer more expensive
   *
   * @param idx to use
   */
  private void siftDown(int idx) {
    int v = heap[idx];               // 1. Store the vertex we are moving.
    double vKey = vertexScores[v];   // 2. Cache its key.
    int half = size >>> 1;           // 3. 'half' is the index of the last node with children (Unsigned Right Shift to do a /2)

    while (idx < half) {         // 4. While the node has at least one child...
      int left = (idx << 1) + 1;        // 5. Left child index: (2*idx)+1.
      int right = left + 1;             // 6. Right child index.
      int smallest = left;              // 7. Assume left child is the smallest for now.
      int smallestVertex = heap[left];

      // 8. If right child exists AND is smaller than the left child...
      if (right < size && vertexScores[heap[right]] < vertexScores[smallestVertex]) {
        smallest = right;             // 9. ...the right child is the one we care about.
        smallestVertex = heap[right];
      }

      // 10. If our moving node is already smaller than the smallest child, stop.
      if (vKey <= vertexScores[smallestVertex]) {
        break;
      }

      // 11. Otherwise, pull the smallest child up into our current slot.
      heap[idx] = smallestVertex;
      vertexHeapPosition[smallestVertex] = idx;

      // 12. Move our target "index" down to the child's old slot.
      idx = smallest;
    }

    // 13. Final placement: Put our original vertex into its final correct slot.
    heap[idx] = v;
    vertexHeapPosition[v] = idx;
  }

  /**
   * Constructor
   * @param numberOfVertices to use
   */
  public BinaryMinHeapOpenSet(int numberOfVertices) {
    this.heap = new int[numberOfVertices];
    this.vertexHeapPosition = new int[numberOfVertices];
    this.vertexScores = new double[numberOfVertices];
    Arrays.fill(vertexHeapPosition, -1);
    Arrays.fill(vertexScores, Double.POSITIVE_INFINITY);
  }

  public boolean isEmpty() {
    return size == 0;
  }

  /**
   * Insert the vertex with given score, or decrease its score if the newScore is better.
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

  public double getScore(int vertexId) {
    return vertexScores[vertexId];
  }
  }
