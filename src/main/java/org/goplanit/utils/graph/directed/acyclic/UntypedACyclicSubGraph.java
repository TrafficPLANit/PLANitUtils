package org.goplanit.utils.graph.directed.acyclic;

import java.util.Deque;
import java.util.Iterator;
import java.util.Set;

import org.goplanit.utils.graph.directed.DirectedSubGraph;
import org.goplanit.utils.graph.directed.DirectedVertex;
import org.goplanit.utils.graph.directed.EdgeSegment;
import org.goplanit.utils.misc.Pair;

/**
 * 
 * An untyped acyclic sub graph contains a subset of a full graph, with generics regarding vertices and edges, without cycles. The active subset of the graph is tracked by
 * explicitly registering edge segments. Edge segments are by definition directed.
 * <p>
 * A topological sort on the current state of the graph allows for fast traversal of the graph for various algorithms (shortest path). It also reveals if the graph is still
 * acyclic.
 * <p>
 * To allow for maximum flexibility we do not require any information on how the edges, vertices, edge segments of this graph are configured, i.e., they may be an amalgamation of
 * other (combined) graphs. As long as their internal structure (downstream, upstream vertices, exit and entry segments) represent a valid acyclic graph structure, then any
 * implementation should be able to deal with it.
 * 
 * @author markr
 *
 */
public interface UntypedACyclicSubGraph<V extends DirectedVertex, E extends EdgeSegment> extends DirectedSubGraph<V, E>, Iterable<V> {

  /**
   * Root vertices of this acyclic graph. Roots can either be a starting point or end point depending on the direction of the dag
   * 
   * @return root vertices
   */
  public abstract Set<V> getRootVertices();
  
  /**
   * Add a new root vertex
   * 
   * @param rootVertex to add
   */
  public abstract void addRootVertex(V rootVertex);  

  /**
   * Indicates if the direction of the graph is inverted, i.e., when inverted the root vertex is the final vertex and all other vertices precede it, otherwise it is a starting
   * point and all other vertices succeed it
   * 
   * @return true when inverted, false otherwise
   */
  public abstract boolean isDirectionInverted();

  /**
   * Perform a topological sort on this graph. It is expected that this is conducted before any operations that require
   * this sorting to be in place are invoked, e.g., min-max path tree for example.
   * 
   * @param update when true the topological sort is conducted based on the current state of the subgraph, when false
   *               the most recent (if any) result is returned
   * @return return topological sorting found, null when it was found not to be possible to create a topological sorting
   */
  public abstract Deque<V> topologicalSort(boolean update);

  /**
   * Collect iterator over topologically sorted vertices
   * 
   * @param update when true the topological sort is conducted based on the current state of the subgraph, when false the most recent (if any) result is returned
   * @return iterator
   */
  public default Iterator<V> getTopologicalIterator(boolean update) {
    return getTopologicalIterator(update, false);
  }

  /**
   * Collect iterator over topologically sorted vertices
   * 
   * @param update             when true the topological sort is conducted based on the current state of the subgraph,
   *                           when false the most recent (if any) result is returned
   * @param reverseIterator when true, iterator direction is reversed, when false it is not
   * @return iterator
   */
  public default Iterator<V> getTopologicalIterator(boolean update, boolean reverseIterator) {
    var topologicallySortedVertices = topologicalSort(update);
    if(topologicallySortedVertices == null){
      return null;
    }
    return reverseIterator ? topologicallySortedVertices.descendingIterator() : topologicallySortedVertices.iterator();
  }

  /**
   * Do a breadth first search from start vertex to see with what partition of provided vertices
   * it can reach. It is required that both partitions are disjunct and that it does not matter
   * what vertex in the partition is reached. This only determines what partition can be reached (which
   * then implies the others cannot be reached.
   *
   * @param startVertex to use
   * @param disjunctVertexPartitions to check agains
   * @return partition index that a match was found to and the vertex that was found to be reachable first, null if no
   *  match could be found across partitions
   */
  public default Pair<Integer, V> bfsToReachableVertexPartition(
          V startVertex, Set<V>... disjunctVertexPartitions){
    //TODO: implement this method by doing a BFS
    //do not use topological iterator, but just do BFS
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UntypedACyclicSubGraph<V, E> shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UntypedACyclicSubGraph<V, E> deepClone();

}
