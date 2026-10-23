package org.goplanit.utils.network.layer.physical;

import org.apache.commons.collections4.map.MultiKeyMap;
import org.goplanit.utils.graph.GraphEntities;
import org.goplanit.utils.graph.directed.BannedTurn;
import org.goplanit.utils.graph.directed.BannedTurns;
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
   * Verify if there are any banned turns
   *
   * @return true when present, false otherwise
   */
  public default boolean hasBannedTurns(){
    return getBannedTurns()!= null && !getBannedTurns().isEmpty();
  }

  /**
   * Access to the banned turns container, which may be empty
   *
   * @return banned turns container
   */
  public abstract BannedTurns getBannedTurns();

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
   * Number of banned turns
   *
   * @return number of banned turns
   */
  public default long getNumberOfBannedTurns(){
    return getBannedTurns().size();
  }

  /**
   * Create a (new) mapping from entry/exit segment combinations to their banned turn (if any)
   *
   * @return mapping that was created
   */
  public default MultiKeyMap<Object, BannedTurn> createEntryExitSegmentToBannedTurnMapping(){
    MultiKeyMap<Object, BannedTurn> entryExitSegment2BannedTurnMap = new MultiKeyMap<>();
    getBannedTurns().forEach(m -> entryExitSegment2BannedTurnMap.put(m.getSegmentFrom(), m.getSegmentTo(), m));
    return entryExitSegment2BannedTurnMap;
  }

}
