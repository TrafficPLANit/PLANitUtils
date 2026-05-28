package org.goplanit.utils.network.layer.physical;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.goplanit.utils.graph.GraphEntities;
import org.goplanit.utils.network.layer.UntypedDirectedGraphLayer;

/**
 * Physical topological Network consisting of nodes, links and link segments 
 *
 * @author markr
 */
public interface UntypedPhysicalLayer<N extends Node, L extends Link, LS extends LinkSegment>
        extends UntypedDirectedGraphLayer<N, L, LS> {

  /**
   * Collect the links
   * 
   * @return the links
   */
  public abstract GraphEntities<L> getLinks();

  /**
   * Collect the link segments
   * 
   * @return the linkSegments
   */
  public abstract GraphEntities<LS> getLinkSegments();

  /**
   * Collect the nodes
   * 
   * @return the nodes
   */
  public abstract GraphEntities<N> getNodes();

  /**
   * Verify if movements have been generated and are non-empty
   *
   * @return true when present, false otherwise
   */
  public default boolean hasMovements(){
    return getBannedMovements()!= null && !getBannedMovements().isEmpty();
  }

  /**
   * Access to movements container (which may be empty if no movements have been generated)
   *
   * @return movements container
   */
  public abstract BannedMovements getBannedMovements();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UntypedPhysicalLayer shallowClone();

  /**
   * {@inheritDoc}
   */
  @Override
  public abstract UntypedPhysicalLayer deepClone();

  /**
   * Number of nodes
   * 
   * @return number of nodes
   */
  public default long getNumberOfNodes() {
    return getNodes().size();
  }

  /**
   * Number of links
   * 
   * @return number of links
   */
  public default long getNumberOfLinks() {
    return getLinks().size();
  }

  /**
   * Number of link segments
   * 
   * @return number of link segments
   */
  public default long getNumberOfLinkSegments() {
    return getLinkSegments().size();
  }

  /**
   * Create a (new) mapping from entry/sexit segment combinations to their movement (if any)
   *
   * @return mapping that was created
   */
  public default MultiKeyMap<Object, BannedMovement> createEntryExitSegmentToMovementMapping(){
    MultiKeyMap<Object, BannedMovement> entryExitSegment2MovementMap = new MultiKeyMap<>();
    getBannedMovements().forEach(m -> entryExitSegment2MovementMap.put(m.getSegmentFrom(), m.getSegmentTo(), m));
    return entryExitSegment2MovementMap;
  }

}
